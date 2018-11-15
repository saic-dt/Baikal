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

package org.datatech.baikal.fulldump.utils

/**
  * This class is used to get the partition number based on the original rowkey value
  * then the rowkey can be obtained by putting the partition number at the head of rowkey value
  **/

object SplitRegionAlgorithm {

  val hex: Array[Byte] = Array[Byte]('0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9', 'a', 'b',
    'c', 'd', 'e', 'f')

  def getMod(rowkey: Array[Byte], partitionNum: Int): Int = {
    val regionflag = Array(rowkey(0), rowkey(1))
    HexBytesToInt(regionflag) % partitionNum
  }

  private def HexBytesToInt(num: Array[Byte]): Int = {
    var result = 0
    var p = 0
    import scala.util.control.Breaks._
    for (i <- num.indices.reverse) {
      for (j <- hex.indices)
        breakable {
          if (hex(j) == num(i)) {
            result = result + (j * Math.pow(16, p).asInstanceOf[Int])
            p = p + 1
            break
          }
        }
    }
    result
  }

}
