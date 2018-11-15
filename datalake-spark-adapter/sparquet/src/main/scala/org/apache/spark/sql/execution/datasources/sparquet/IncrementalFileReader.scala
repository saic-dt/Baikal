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

package org.apache.spark.sql.execution.datasources.sparquet

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.nio.charset.Charset

import com.google.gson.stream.MalformedJsonException
import com.google.gson.{JsonObject, JsonParser, JsonSyntaxException}
import javax.xml.bind.DatatypeConverter
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.Row
import org.apache.spark.sql.execution.datasources.Logging
import org.apache.spark.sql.execution.datasources.common.constant.DatalakeConstant
import org.apache.spark.sql.execution.datasources.common.util.CommonUtils
import org.apache.spark.sql.execution.datasources.sparquet.exception.NoPrimaryKeyException
import org.apache.spark.sql.execution.datasources.sparquet.util.Utils
import org.apache.spark.sql.types.StructType

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * Class to read & merge incremental files
  */
class IncrementalFileReader(relation: SParquetRelation, schema: StructType) extends Logging {
  /**
    * to store update/delete data in incremental files
    */
  val incData = new mutable.HashMap[String, String]()
  /**
    * to store insert data in incremental files  -- temp
    */
  val insertData = new mutable.HashMap[String, String]()
  /**
    * to store old key to new key mapping relationship  --temp
    */
  val keyMapping = new mutable.HashMap[String, String]()
  /**
    * to store deleted keys in incremental files
    */
  val deletedKeys = new mutable.HashMap[String, String]()
  /**
    * to store insert data converted to row
    */
  val insert = new ArrayBuffer[Row]()

  def getIncData(): mutable.HashMap[String, String] = {
    incData
  }

  def getDeletedKeys(): mutable.HashMap[String, String] = {
    deletedKeys
  }

  def getInsertRows(): ArrayBuffer[Row] = {
    insert
  }

  /**
    * P1: key in incdata is oldest key
    * P2: key in insertdata is new key
    * P3: for no pk table, update any column will change key
    * Merge to make sure every row is unique
    *
    * @return
    */
  // {"o":"i","after": {"pk":"Adg0df", "cl1":"fdkasljfs"}}
  // {"o":"u","before":{"pk":"Adg0df", "cl5":"fdsaf"}, "after": {"pk":"Adg0df","cl5":"bjigpX"}}
  // {"o":"d","before": {"pk":"fdsafsa"}}
  // {"o":"uk","before":{"pk":"fdsafsa"}, "after": {"pk":"2test_test22", "cl3":"djfkwo"}}
  def readIncrementalData(): Unit = {
    val pathList = relation.incrementalFiles
    val sourcepk = relation.dtm.sourcePK
    val jp = new JsonParser
    pathList.foreach { path =>
      try {
        val inputStream = relation.fs.open(new Path(path))
        val br = new BufferedReader(new InputStreamReader(inputStream))
        breakable {
          while (br.ready()) {
            try {
              distributeIncrementalData(br.readLine(), incData, deletedKeys, sourcepk, insertData, keyMapping)
            } catch {
              case e@(_: IllegalStateException | _: MalformedJsonException | _: JsonSyntaxException) =>
                logWarning(">>>>> Warning! Malformed json object: \n" + e.getMessage)
                break
            }
          }
        }
      } catch {
        case e@(_: NoPrimaryKeyException | _: IOException) => logError(e.getMessage)
      }
    }

    insertData.values.toArray.map(x =>
      insert += Utils.convertJson2Row(jp.parse(x).getAsJsonObject, schema))
  }

  def distributeIncrementalData(inputStr: String, incData: mutable.HashMap[String, String],
                                deletedKeys: mutable.HashMap[String, String], sourcepk: Array[String],
                                insertData: mutable.HashMap[String, String], keyMapping: mutable.HashMap[String, String]): Unit = {
    val jp = new JsonParser()
    val str = new String(DatatypeConverter.parseBase64Binary(inputStr), DatalakeConstant.DECRYPT_CHARSET)
    val jsonOb = jp.parse(str).getAsJsonObject
    val (keyValues, aftValues) = jsonOb.get("o").getAsString match {
      case "d" => (jsonOb.get("before").getAsJsonObject, null)
      case "i" => (jsonOb.get("after").getAsJsonObject, jsonOb.get("after").getAsJsonObject)
      case "u" => (jsonOb.get("before").getAsJsonObject, jsonOb.get("after").getAsJsonObject)
      case "uk" => if (sourcepk.size == 0) {
        throw new NoPrimaryKeyException(
          ">>>>> PrimaryKey of source table is NOT DEFINED but 'uk' exist, pls check!")
      } else {
        (jsonOb.get("before").getAsJsonObject, jsonOb.get("after").getAsJsonObject)
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
              val newValue = CommonUtils.mergeValues(aftValues, incData.get(oldKey.get).get)
              val newKey = generateKey(sourcepk, newValue, schema)
              incData.update(oldKey.get, newValue.toString)
              keyMapping.remove(key)
              keyMapping.put(newKey, oldKey.get)
            }
          } else {
            // not exists in insert, but exists in update/delete, which means not exists in key mapping
            val newValue = CommonUtils.mergeValues(aftValues, incData.get(key).get)
            incData.update(key, newValue.toString)
            if (sourcepk.size == 0) { // else, newKey == key, no need to store in keyMapping
              val newKey = generateKey(sourcepk, newValue, schema)
              keyMapping.put(newKey, key)
            }
          }
        } else {
          // exists in insert
          val newValue = CommonUtils.mergeValues(aftValues, insertData.get(key).get)
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
              val newValue = CommonUtils.mergeValues(aftValues, incData.get(oldKey.get).get)
              val newKey = generateKey(sourcepk, newValue, schema)
              incData.update(oldKey.get, newValue.toString)
              keyMapping.remove(key)
              keyMapping.put(newKey, oldKey.get)
            }
          } else {
            val newValue = CommonUtils.mergeValues(aftValues, incData.get(key).get)
            val newKey = generateKey(sourcepk, newValue, schema)
            incData.update(key, newValue.toString)
            keyMapping.put(newKey, key)
          }
        } else {
          val newValue = CommonUtils.mergeValues(aftValues, insertData.get(key).get)
          val newKey = generateKey(sourcepk, newValue, schema)
          insertData.remove(key)
          insertData.put(newKey, newValue.toString)
        }
      }
    }
  }

  def generateKey(sourcepk: Array[String], keyValues: JsonObject, schema: StructType): String = {
    // one PK
    if (sourcepk.size == 1) {
      val columnName = sourcepk(0)
      new String(keyValues.get(columnName).getAsString
        .getBytes(Charset.forName(DatalakeConstant.DECRYPT_CHARSET)))
    } else if (sourcepk.size > 1) { // multiple PKs
      val pkValues = ArrayBuffer[String]()
      sourcepk.foreach { columnName =>
        pkValues += new String(keyValues.get(columnName).getAsString
          .getBytes(DatalakeConstant.DECRYPT_CHARSET))
      }
      pkValues.mkString("|")
    } else { // no pk
      val pkValues = ArrayBuffer[String]()
      schema.fieldNames.foreach { fName =>
        val jsonValue = keyValues.get(fName)
        val value = if (jsonValue == null) {
          "null"
        } else {
          new String(jsonValue.getAsString.getBytes(DatalakeConstant.DECRYPT_CHARSET))
        }
        pkValues += value
      }
      pkValues.mkString("|")
    }
  }
}
