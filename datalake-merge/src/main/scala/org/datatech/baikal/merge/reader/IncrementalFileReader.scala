/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datatech.baikal.merge.reader

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.nio.charset.Charset

import com.google.gson.stream.MalformedJsonException
import com.google.gson.{JsonObject, JsonParser, JsonSyntaxException}
import javax.xml.bind.DatatypeConverter
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.datatech.baikal.merge.exception.NoPrimaryKeyException
import org.datatech.baikal.merge.util.Utils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * Class to read & merge incremental files
  */
object IncrementalFileReader {

  val insertData = mutable.HashMap[String, String]()
  val keyMapping = new mutable.HashMap[String, String]()

  val log: Logger = LoggerFactory.getLogger("IncrementalFileReader")

  /**
    * P1: key in incdata is oldest key
    * P2: key in insertdata is new key
    * P3: for no pk table, update any column will change key
    * Merge to make sure every row is unique
    *
    * @param incData to store update/delete data in incremental files
    * @param insert  to store insert data in incremental files
    * @return
    */
  // {"o":"i","after": {"pk":"Adg0df", "cl1":"fdkasljfs"}}
  // {"o":"u","before":{"pk":"Adg0df", "cl5":"fdsaf"}, "after": {"pk":"Adg0df","cl5":"bjigpX"}}
  // {"o":"d","before": {"pk":"fdsafsa"}}
  // {"o":"uk","before":{"pk":"fdsafsa"}, "after": {"pk":"2test_test22", "cl3":"djfkwo"}}
  def readIncrementalData(pathList: Array[String],
                          schema: StructType,
                          incData: mutable.HashMap[String, String],
                          deletedKeys: mutable.HashMap[String, String],
                          insert: ArrayBuffer[Row],
                          sourcepk: Array[String],
                          fs: FileSystem): Unit = {
    val insertData = new mutable.HashMap[String, String]()
    val keyMapping = new mutable.HashMap[String, String]()
    val jp = new JsonParser
    pathList.foreach { path =>
      try {
        val inputStream = fs.open(new Path(path))
        val br = new BufferedReader(new InputStreamReader(inputStream))
        breakable {
          while (br.ready()) {
            try {
              distributeIncrementalData(br.readLine(), incData, deletedKeys, sourcepk, insertData, keyMapping,
                schema)
            } catch {
              case e@(_: IllegalStateException | _: MalformedJsonException | _: JsonSyntaxException) =>
                log.warn(">>>>> Warning! Malformed json object: \n" + e.getMessage)
                break
            }
          }
        }
      } catch {
        case e@(_: NoPrimaryKeyException | _: IOException) => log.error(e.getMessage)
      }
    }

    insertData.values.toArray.map(x =>
      insert += Utils.convertJson2Row(jp.parse(x).getAsJsonObject, schema))

  }

  def distributeIncrementalData(inputStr: String, incData: mutable.HashMap[String, String],
                                deletedKeys: mutable.HashMap[String, String], sourcepk: Array[String],
                                insertData: mutable.HashMap[String, String],
                                keyMapping: mutable.HashMap[String, String],
                                schema: StructType): Unit = {
    val jp = new JsonParser()
    val str = new String(DatatypeConverter.parseBase64Binary(inputStr), Charset.forName("iso-8859-1"))
    val jsonOb = jp.parse(str).getAsJsonObject
    val (keyValues, aftValues) = jsonOb.get("o").getAsString match {
      case "d" => (decrypt(jsonOb.get("before").getAsJsonObject), null)
      case "i" => (decrypt(jsonOb.get("after").getAsJsonObject), decrypt(jsonOb.get("after").getAsJsonObject))
      case "u" => (decrypt(jsonOb.get("before").getAsJsonObject), decrypt(jsonOb.get("after").getAsJsonObject))
      case "uk" => if (sourcepk.size == 0) {
        throw new NoPrimaryKeyException(
          ">>>>> PrimaryKey of source table is NOT DEFINED but 'uk' exist, pls check!")
      } else {
        (decrypt(jsonOb.get("before").getAsJsonObject), decrypt(jsonOb.get("after").getAsJsonObject))
      }
    }
    jsonOb.get("o").getAsString match {
      case "i" => {
        val key = generateKey(sourcepk, keyValues, schema)
        insertData.put(key, aftValues.toString)
      }
      case "u" => {
        val key = generateKey(sourcepk, keyValues, schema)
        if (insertData.get(key) == None) {
          if (incData.get(key) == None) {
            val oldKey = keyMapping.get(key)
            if (oldKey == None) {
              incData.put(key, aftValues.toString)
              if (sourcepk.size == 0) {
                val newKey = generateKey(sourcepk, aftValues, schema)
                keyMapping.put(newKey, key)
              }
            } else {
              // not exists in insert, not exists in update/delete, but, exists in key mapping,
              val newValue = mergeValues(aftValues, incData.get(oldKey.get).get)
              val newKey = generateKey(sourcepk, newValue, schema)
              incData.update(oldKey.get, newValue.toString)
              keyMapping.remove(key)
              keyMapping.put(newKey, oldKey.get)
            }
          } else {
            // not exists in insert, but exists in update/delete, which means not exists in key mapping
            val newValue = mergeValues(aftValues, incData.get(key).get)
            incData.update(key, newValue.toString)
            if (sourcepk.size == 0) { // else, newKey == key, no need to store in keyMapping
              val newKey = generateKey(sourcepk, newValue, schema)
              keyMapping.put(newKey, key)
            }
          }
        } else {
          // exists in insert
          val newValue = mergeValues(aftValues, insertData.get(key).get)
          val newKey = generateKey(sourcepk, newValue, schema)
          insertData.remove(key)
          insertData.put(newKey, newValue.toString)
        }
      }
      case "d" => {
        val key = generateKey(sourcepk, keyValues, schema)
        if (insertData.get(key) == None) {
          if (incData.get(key) == None) {
            val oldKey = keyMapping.get(key)
            if (oldKey == None) {
              deletedKeys.put(key, null)
            } else {
              incData.remove(oldKey.get)
              deletedKeys.put(oldKey.get, null)
            }
          } else {
            incData.remove(key)
            deletedKeys.put(key, null)
          }
        } else {
          insertData.remove(key)
        }

      }
      case "uk" => {
        val key = generateKey(sourcepk, keyValues, schema)
        if (insertData.get(key) == None) {
          if (incData.get(key) == None) {
            val oldKey = keyMapping.get(key)
            if (oldKey == None) {
              val newKey = generateKey(sourcepk, aftValues, schema)
              incData.put(key, aftValues.toString)
              keyMapping.put(newKey, key)
            } else {
              val newValue = mergeValues(aftValues, incData.get(oldKey.get).get)
              val newKey = generateKey(sourcepk, newValue, schema)
              incData.update(oldKey.get, newValue.toString)
              keyMapping.remove(key)
              keyMapping.put(newKey, oldKey.get)
            }
          } else {
            val newValue = mergeValues(aftValues, incData.get(key).get)
            val newKey = generateKey(sourcepk, newValue, schema)
            incData.update(key, newValue.toString)
            keyMapping.put(newKey, key)
          }
        } else {
          val newValue = mergeValues(aftValues, insertData.get(key).get)
          val newKey = generateKey(sourcepk, newValue, schema)
          insertData.remove(key)
          insertData.put(newKey, newValue.toString)
        }
      }
    }
  }

  /**
    * merge two Json Objects into one
    * old {"ID":"3971","NAME":"zhou","ADDR":"chunyan"}
    * new {"ID":"3971","NAME":"caolu","ADDR":"shanghai"}
    *
    * @param newValue
    * @param oldValueString
    * @return
    */
  def mergeValues(newValue: JsonObject, oldValueString: String): JsonObject = {
    val jp = new JsonParser
    val oldValue = jp.parse(oldValueString).getAsJsonObject
    val newIt = newValue.entrySet().iterator()
    while (newIt.hasNext) {
      val newChild = newIt.next()
      val oldChild = oldValue.get(newChild.getKey)
      if (oldChild == null) {
        oldValue.add(newChild.getKey, newChild.getValue)
      } else {
        oldValue.remove(newChild.getKey)
        oldValue.add(newChild.getKey, newChild.getValue)
      }
    }
    oldValue
  }

  def generateKey(sourcepk: Array[String], keyValues: JsonObject, schema: StructType): String = {
    // one PK
    if (sourcepk.size == 1) {
      keyValues.get(sourcepk(0)).getAsString
    } else if (sourcepk.size > 1) { // multiple PKs
      val pkValues = ArrayBuffer[String]()
      sourcepk.foreach(pkValues += keyValues.get(_).getAsString)
      pkValues.mkString("|")
    } else { // no pk
      val pkValues = ArrayBuffer[String]()
      schema.fieldNames.foreach { fName =>
        val obj = keyValues.get(fName)
        if (obj != null) {
          val value = obj.getAsString
          pkValues += value
        }
        else
          pkValues += "null"
      }
      //      val vIt = keyValues.entrySet().iterator()
      //      while (vIt.hasNext) {
      //        pkValues += vIt.next().getValue.getAsString
      //      }
      pkValues.mkString("|")
    }
  }

  def decrypt(jsonObject: JsonObject): JsonObject = {
    val result: JsonObject = new JsonObject
    val vIt = jsonObject.entrySet().iterator()
    while (vIt.hasNext) {
      val entry = vIt.next()
      val value = entry.getValue.getAsString.getBytes(Charset.forName("iso-8859-1"))
      val data = new String(value)
      //log.info(">>> decrypt data:" + data)
      result.addProperty(entry.getKey, data)
    }
    result
  }

  def generateKey(sourcepk: Array[String], pkIndices: Array[Int], row: Row, schema: StructType): String = {
    if (sourcepk.size == 0) {
      var str = row.mkString("|")
      for (_ <- row.length until schema.length)
        str = str.concat("|null")
      str
    } else if (sourcepk.size == 1) {
      row(pkIndices(0)).toString
    } else {
      val arr = new ArrayBuffer[String]()
      pkIndices.foreach { index =>
        arr += row(index).toString
      }
      arr.mkString("|")
    }
  }
}
