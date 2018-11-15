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

package org.apache.spark.sql.execution.datasources.common.util

import java.text.SimpleDateFormat

import com.google.gson.{JsonObject, JsonParser}
import org.apache.spark.sql.Row
import org.apache.spark.sql.execution.datasources.Logging
import org.apache.spark.sql.execution.datasources.common.constant.DatalakeConstant
import org.apache.spark.sql.execution.datasources.common.exception.MalformedDataTypeException
import org.apache.spark.sql.types._

object CommonUtils extends Logging {

  def convert2SparkDataType(originRow: Row, schema: StructType, df: SimpleDateFormat, tf: SimpleDateFormat): Row = {
    val newRowArr = new Array[Any](originRow.size)
    val FIXED_DECIMALTYPE = """DecimalType\(\s*(\d+)\s*,\s*(\-?\d+)\s*\)""".r
    for (i <- 0 until originRow.size) {
      val dataType = schema(i).dataType
      val x = originRow(i)
      val value = dataType match {
        case LongType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x).toDouble.toLong
        case IntegerType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x).toDouble.toInt
        case StringType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x)
        case DoubleType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x).toDouble
        case FloatType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x).toFloat
        case ShortType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x).toDouble.toShort
        case DateType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else df.parse(String.valueOf(x))
        case TimestampType => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else tf.parse(String.valueOf(x))
        case FIXED_DECIMALTYPE(_, _) => if (x == null || String.valueOf(x).toLowerCase() == "null") null
        else String.valueOf(x).toDouble
        case _ => throw new MalformedDataTypeException("Unsupported datatype: " + dataType.toString)
      }
      newRowArr(i) = value
    }
    Row.fromSeq(newRowArr)
  }

  def convert2SparkDataType(x: String, dataType: DataType, df: SimpleDateFormat, tf: SimpleDateFormat): Any = {
    val FIXED_DECIMALTYPE = """DecimalType\(\s*(\d+)\s*,\s*(\-?\d+)\s*\)""".r
    val value = dataType match {
      case LongType => if (x == null || x.toLowerCase() == "null") null else x.toDouble.toLong
      case IntegerType => if (x == null || x.toLowerCase() == "null") null else x.toDouble.toInt
      case StringType => if (x == null || x.toLowerCase() == "null") null else x
      case DoubleType => if (x == null || x.toLowerCase() == "null") null else x.toDouble
      case FloatType => if (x == null || x.toLowerCase() == "null") null else x.toFloat
      case ShortType => if (x == null || x.toLowerCase() == "null") null else x.toDouble.toShort
      case DateType => if (x == null || x.toLowerCase() == "null") null else df.parse(x)
      case TimestampType => if (x == null || x.toLowerCase() == "null") null else tf.parse(x)
      case FIXED_DECIMALTYPE(_, _) => if (x == null || x.toLowerCase() == "null") null else x.toDouble
      case _ => throw new MalformedDataTypeException("Unsupported datatype: " + dataType.toString)
    }
    value
  }

  /**
    * decrypt the data according to acl info
    *
    * @param row
    * @return
    */
  def decryptRow(row: Row): Row = {
    Row.fromSeq(row.toSeq.zipWithIndex.
      map(x =>
        if (x._1 == null) {
          null
        } else {
          if (x._1.isInstanceOf[String]) {
            new String(x._1.asInstanceOf[String].getBytes(DatalakeConstant.DECRYPT_CHARSET))
          } else {
            new String(x._1.asInstanceOf[Array[Byte]])
          }
        }
      )
    )
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
}
