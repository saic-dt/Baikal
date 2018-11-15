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

import java.security.MessageDigest

import org.apache.commons.codec.binary.Hex

object Md5Util {

  def getMd5(plainText: String): String = {
    Hex.encodeHexString(getMd.digest(plainText.getBytes("UTF-8")))
  }

  private def getMd: MessageDigest = {
    MessageDigest.getInstance("MD5")
  }

}
