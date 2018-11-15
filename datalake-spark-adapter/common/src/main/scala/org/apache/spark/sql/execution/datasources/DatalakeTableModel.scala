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

package org.apache.spark.sql.execution.datasources

/**
  * Model class to store source table related info
  *
  * @param prefix
  * @param sourcePK
  * @param tenantName
  * @param oiName origin instance
  * @param osName origin schema
  * @param otName origin table
  */
case class DatalakeTableModelS(prefix: String,
                               sourcePK: Array[String],
                               dateFormat: String,
                               timestampFormat: String,
                               tenantName: String,
                               oiName: String,
                               osName: String,
                               otName: String)

case class DatalakeTableModel(prefix: String,
                              tenantName: String,
                              oiName: String,
                              osName: String,
                              otName: String)

