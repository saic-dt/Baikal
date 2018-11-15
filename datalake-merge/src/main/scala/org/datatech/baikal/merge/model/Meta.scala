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

case class Meta(
                 var SOURCE_PK: String,
                 var PREFIX: String,
                 var META_FLAG: String
               ) {
  def getSOURCE_PK: String = SOURCE_PK

  def setSOURCE_PK(source_pk: String): Unit = {
    SOURCE_PK = source_pk
  }

  def getPREFIX: String = PREFIX

  def setPREFIX(prefix: String): Unit = {
    PREFIX = prefix
  }

  def getMETA_FLAG: String = META_FLAG

  def setMETA_FLAG(metaflag: String): Unit = {
    META_FLAG = metaflag
  }

  override def toString: String = {
    "{\tsource_pk:" + SOURCE_PK + ",\tprefix:" + PREFIX + ",\tmeta_flag:" + META_FLAG + "}"
  }
}

