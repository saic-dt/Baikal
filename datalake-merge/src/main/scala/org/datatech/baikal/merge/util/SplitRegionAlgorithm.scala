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

import scala.util.control.Breaks._

object SplitRegionAlgorithm {


  val hex: Array[Byte] = Array[Byte]('0'.toByte, '1'.toByte, '2'.toByte, '3'.toByte, '4'.toByte, '5'.toByte, '6'.toByte, '7'.toByte, '8'.toByte, '9'.toByte, 'a'.toByte, 'b'.toByte, 'c'.toByte, 'd'.toByte, 'e'.toByte, 'f'.toByte)

  def getMod(rowKey: Array[Byte]): Int = {
    val regionFlagBytes = Array[Byte](rowKey(8), rowKey(9))
    //println(">> regionFlagBytes:"
    // + String.valueOf(regionFlagBytes(0).asInstanceOf[Int]) + String.valueOf(regionFlagBytes(1).asInstanceOf[Int]))
    HexBytesToInt(regionFlagBytes) % 64
  }

  private def HexBytesToInt(num: Array[Byte]) = {
    var result = 0
    var p = 0
    for (i <- num.indices.reverse) {
      breakable {
        for (j <- hex.indices) {
          if (hex(j) == num(i)) {
            result = result + j * Math.pow(16, p).asInstanceOf[Int]
            print(result)
            p += 1
            break
          }
        }
      }
    }
    //println("HexBytesToInt:" + result)
    result
  }

}
