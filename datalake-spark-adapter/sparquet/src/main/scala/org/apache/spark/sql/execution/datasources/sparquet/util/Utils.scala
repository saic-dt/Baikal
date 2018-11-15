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

package org.apache.spark.sql.execution.datasources.sparquet.util

import com.google.gson.JsonObject
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType

import scala.collection.mutable.ArrayBuffer

object Utils {

  def convertJson2Row(json: JsonObject, schema: StructType): Row = {
    val arr = new ArrayBuffer[String]
    schema.fieldNames.foreach { col =>
      val value = json.get(col)
      arr += {
        if (value == null) null else {
          value.getAsString
        }
      }
    }
    Row.fromSeq(arr)
  }
}
