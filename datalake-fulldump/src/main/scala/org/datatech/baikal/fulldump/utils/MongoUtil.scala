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

import com.mongodb._
import org.slf4j.LoggerFactory


class MongoUtil {

}


object MongoUtil {

  val logger = LoggerFactory.getLogger(classOf[MongoUtil])

  /*
  * access mongodb to get the count of documents in the collection
  * with or without authentication
  * */
  def mongo_document_count(url: String, username: String, password: String, dbName: String, collectionName: String): Long = {

    var doc_count: Long = 0L
    val logger = LoggerFactory.getLogger(classOf[MongoUtil])
    logger.info(">>>>>> start accessing mongodb via MongoClientURI")
    if (username != null && (!username.equals(""))) {
      val user_Password = username.concat(":").concat(password).concat("@")
      val strBuilder = new StringBuilder(url)
      val mongodb_input_uri: String = strBuilder.insert(10, user_Password).toString()
      try {
        val mongoClientUri = new MongoClientURI(mongodb_input_uri)
        val mongoclient = new MongoClient(mongoClientUri)
        val collection = mongoclient.getDatabase(dbName).getCollection(collectionName)
        doc_count = collection.count()
      } catch {
        case e: Exception => {
          e.printStackTrace()
          logger.error(">>>>>> Error, exception occurs while accessing mongodb via MongoClientURI with user-password.")
        }
      }
    }
    else {
      try {
        val mongoClientUri = new MongoClientURI(url)
        val mongoclient = new MongoClient(mongoClientUri)
        val collection = mongoclient.getDatabase(dbName).getCollection(collectionName)
        doc_count = collection.count()
      } catch {
        case e: Exception => {
          e.printStackTrace()
          logger.error(">>>>>> Error, exception occurs while accessing mongodb via MongoClientURI without user.")
        }
      }
    }

    logger.info("\n>>>>>> Mongo collection's documents count is: " + doc_count)
    doc_count

  }


  /*
  *
  * construct the url in spark mongo readconfig options
  *
  * */
  def getMongoInputArgs(url: String, schema_table: String, userName: String, password: String): String = {

    var mongodb_input_uri: String = url
    if (userName != null && (!userName.equals(""))) {
      val user_Password = userName.concat(":").concat(password).concat("@")
      val strBuilder = new StringBuilder(url)
      mongodb_input_uri = strBuilder.insert(10, user_Password).toString()
    }

    mongodb_input_uri

  }


}
