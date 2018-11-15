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

package org.datatech.baikal.merge.util

import java.text.SimpleDateFormat

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.datatech.baikal.merge.exception.MalformedDataTypeException
import org.slf4j.{Logger, LoggerFactory}

object CommonUtils {

  val log: Logger = LoggerFactory.getLogger("CommonUtils")
  val timeInterval = 1000 * 3600 * 24 * 365
  val keyPath = "/datalake/config/key.db"
  val metaPath = "/datalake/metastore/"
  val tenantPath = "/datalake/tenant/"
  var keySeed: Array[Byte] = null
  var lastKeySeedTime: Long = 0

  def convert2SparkDataType(originRow: Row, schema: StructType, df: SimpleDateFormat, tf: SimpleDateFormat): Row = {
    val newRowArr = new Array[Any](originRow.size)
    val FIXED_DECIMALTYPE = """decimaltype\(\s*(\d+)\s*,\s*(\-?\d+)\s*\)""".r
    for (i <- 0 until originRow.size) {
      val dataType = schema(i).dataType
      val x = originRow(i)
      val value = dataType match {
        case LongType => if (x == null) null else String.valueOf(x).toDouble.toLong
        case IntegerType => if (x == null) null else String.valueOf(x).toDouble.toInt
        case StringType => if (x == null) null else String.valueOf(x)
        case DoubleType => if (x == null) null else String.valueOf(x).toDouble
        case FloatType => if (x == null) null else String.valueOf(x).toFloat
        case ShortType => if (x == null) null else String.valueOf(x).toDouble.toShort
        case DateType => if (x == null) null else df.parse(String.valueOf(x))
        case TimestampType => if (x == null) null else tf.parse(String.valueOf(x))
        case FIXED_DECIMALTYPE(_, _) => if (x == null) null else String.valueOf(x).toDouble
        case _ => throw new MalformedDataTypeException("Unsupported datatype: " + dataType.toString)
      }
      newRowArr(i) = value
    }
    Row.fromSeq(newRowArr)
  }

  def join(array: Array[String], delim: String): String = {
    val sb = new StringBuilder
    for (i <- 0 until array.length - 2)
      sb.append(array(i)).append(delim)
    sb.append(array(array.length - 1))
    sb.toString
  }


}
