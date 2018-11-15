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

package org.datatech.baikal.fulldump.jdbc

/**
  * Used to adjust automatically the parallelism number of spark jdbc according to the row count of table for better performance
  **/

object ChangeParallelismNumStrategy {

  def changeParallelismNum(rowCount: Long, parallelismNum: Int, partitionNum: Int, dbType: String): Tuple2[Int, Int] = {

    // different parallelism number for different row number of the table
    var changedParallelismNum = parallelismNum
    var changedpartitionNum = partitionNum
    if (rowCount <= 100000 && parallelismNum > 8) {
      changedParallelismNum = 8
    }
    else if (rowCount > 100000 && rowCount <= 1000000 && parallelismNum > 16) {
      changedParallelismNum = 16
    }
    else if (rowCount > 1000000 && rowCount <= 5000000 && parallelismNum > 32 && (dbType == "DB2" || dbType == "MYSQL")) {
      changedParallelismNum = 32
    }
    if (rowCount >= 50000000) {
      changedpartitionNum = 128
    }

    (changedParallelismNum, changedpartitionNum)

  }


}
