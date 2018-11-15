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

import java.io.{PrintWriter, StringWriter}
import java.security.{MessageDigest, NoSuchAlgorithmException}

import org.apache.commons.codec.binary.Hex
import org.slf4j.LoggerFactory

object Md5Util {

  private val logger = LoggerFactory.getLogger("MD5Util")

  private val md = getMd

  def getMD5(plainText: String): String = Hex.encodeHexString(md.digest(plainText.getBytes("UTF-8")))

  private def getMd = try
    MessageDigest.getInstance("MD5")
  catch {
    case e: NoSuchAlgorithmException =>
      val sw = new StringWriter
      e.printStackTrace(new PrintWriter(sw))
      logger.error(sw.toString)
      null
  }

}
