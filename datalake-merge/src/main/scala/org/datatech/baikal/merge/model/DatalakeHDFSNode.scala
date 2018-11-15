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

package org.datatech.baikal.merge.model

import org.datatech.baikal.merge.constants.ZooKeeperConstants

case class DatalakeHDFSNode(
                             absolutePath: String,
                             tenantName: String,
                             instanceName: String,
                             schemaName: String,
                             tableName: String,
                             prefix: String,
                             meta: Meta,
                             lastTs: String,
                             ts: String) {

  def getNewHDFSTempPath: String = {
    getNewHDFSPath.concat("___temp")
  }

  def getNewHDFSPath: String = {
    getPrefixAbsolutePath.concat("/").concat(ts)
  }

  def getPrefixAbsolutePath: String = {
    getTableAbsolutePath.concat("/").concat(prefix)
  }

  def getTableAbsolutePath: String = {
    ZooKeeperConstants.DATALAKE_ROOT
      .concat("/").concat(tenantName)
      .concat("/").concat(instanceName)
      .concat("/").concat(schemaName)
      .concat("/").concat(tableName)
  }

  def zkLockPath: String = {
    ZooKeeperConstants.DATALAKE_MERGE_LOCK
      .concat("/").concat(tenantName)
      .concat("/").concat(instanceName)
      .concat("/").concat(schemaName)
      .concat("/").concat(tableName)
  }

  def zkLastMergePath: String = {
    ZooKeeperConstants.DATALAKE_LAST_SUCCESSFUL_MERGE
      .concat("/").concat(tenantName)
      .concat("/").concat(instanceName)
      .concat("/").concat(schemaName)
      .concat("/").concat(tableName)
  }

  override def toString: String = {
    "{\n" + "\tabsolutePath:" + absolutePath + "," + "\n\tmeta:" + meta + "\n}"
  }

}
