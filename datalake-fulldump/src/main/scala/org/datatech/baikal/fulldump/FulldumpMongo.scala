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

package org.datatech.baikal.fulldump

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import com.google.gson.{Gson, JsonParser}
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.zookeeper.ZooKeeper
import org.bson._
import org.datatech.baikal.fulldump.listener.MySparkListener
import org.datatech.baikal.fulldump.meta.MetaConfigDao
import org.datatech.baikal.fulldump.partitioner.DtPartitioner
import org.datatech.baikal.fulldump.utils.{CreateNotify_freshNode, Md5Util, MongoUtil, SplitRegionAlgorithm}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  *
  * Full-dumping data from databases(RDBMS & NoSQL) to HDFS in parquet format
  * In the nested columns only the first layer will be extracted, the inner layers will be saved in json.
  **/

class FulldumpMongo {

}


object FulldumpMongo {

  val logger = LoggerFactory.getLogger(classOf[FulldumpMongo])

  def doMongoFulldump(jdbcUrl: String, userName: String, password: String,
                      tenantName: String, instanceName: String, schemaName: String, tableName: String, tablePrefix: String,
                      sc: SparkContext, spark: SparkSession, metaConfigDao: MetaConfigDao,
                      partitionNum: Int, parallelismNum: Int, hdfsPath: String, rowCount: Long, ts_start: Long,
                      zkClient: ZooKeeper, notify_freshNodePath: String): Unit = {

    val schema_table = schemaName.concat(".").concat(tableName)
    metaConfigDao.updateSoFar(instanceName, schemaName, tableName, Math.floor(rowCount * 0.03).toLong)
    val mongodb_input_uri = MongoUtil.getMongoInputArgs(jdbcUrl, schema_table, userName, password)

    // setup SparkListener to listen the progress of spark job
    logger.info(">>>>>> start spark listener")
    val sparkListener = new MySparkListener(metaConfigDao, instanceName, schemaName, tableName, rowCount, parallelismNum, partitionNum, true)
    sc.addSparkListener(sparkListener)

    logger.info(">>>>>> start fetching data from Mongodb")
    val readConfig = ReadConfig(Map(
      "spark.mongodb.input.uri" -> s"${mongodb_input_uri}",
      "spark.mongodb.input.database" -> s"${schemaName}",
      "spark.mongodb.input.collection" -> s"${tableName}",
      "spark.mongodb.input.partitioner" -> "MongoPaginateByCountPartitioner",
      "spark.mongodb.input.partitionerOptions.shardkey" -> "_id",
      "spark.mongodb.input.partitionerOptions.numberOfPartitions" -> s"${partitionNum}"))

    // create notify_fresh node in zk
    val mongoValue = getNotify_fresh_mongo_value(tenantName, instanceName, schemaName, tableName, tablePrefix)
    logger.info(">>>>>> getNotify_fresh_mongo_value is: " + mongoValue)
    CreateNotify_freshNode.createSeqNode(zkClient, notify_freshNodePath, true, mongoValue)

    val originalRDD = MongoSpark.load(sc, readConfig)
    val originalDF = MongoSpark.load(spark, readConfig)
    val originalSchema = originalDF.schema
    val fieldNames = originalSchema.fieldNames
    val fieldDatatypes = originalSchema.fields.map(element => {
      val dataType = element.dataType
      dataType
    })

    val partitionNumB = sc.broadcast(partitionNum)
    val fieldNamesB = sc.broadcast(fieldNames)
    val fieldDatatypesB = sc.broadcast(fieldDatatypes)

    logger.info(">>>>>> start getting Mongodb newRow and newRowkey")
    val rddMap: RDD[(String, Row)] = originalRDD.mapPartitions(partition => {
      val partitionNumBV = partitionNumB.value
      val fieldNamesBV = fieldNamesB.value
      val fieldDatatypesBV = fieldDatatypesB.value
      partition.map {
        row => (getNewRowkey_Mongo(row, partitionNumBV, fieldNamesBV), getNewRow_Mongo(row, fieldNamesBV, fieldDatatypesBV))
      }
    })

    val rddPartitions = rddMap.partitionBy(new DtPartitioner(partitionNum)).values

    logger.info(">>>>>> start constructing new Dataframe schema")
    val structFields = ArrayBuffer[StructField]()
    for (i <- 0 until originalSchema.length) {
      structFields.append(StructField(originalSchema.apply(i).name, BinaryType, nullable = true))
    }
    val newSchema = StructType(structFields.toList)
    val newDF = spark.sqlContext.createDataFrame(rddPartitions, newSchema)
    logger.info(">>>>>> new schema is: " + newSchema.toString())

    logger.info(">>>>>> start writing dataframe to hdfs in parquet format")
    newDF.write.mode(SaveMode.Overwrite).parquet(hdfsPath)
    logger.info(">>>>>> Mongo hdfspath is: " + hdfsPath)
    logger.info(">>>>>> start updating meta after full dumping data.")
    metaConfigDao.updateSoFar(instanceName, schemaName, tableName, rowCount)
    logger.info(">>>>>>> fulldump successed, update META_FLAG as 3")
    metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, Config.META_FLAG_FULLDUMP_END)
    logger.info(">>>>>> updating meta finished")
    val ts_end = System.currentTimeMillis()
    logger.info(">>>>>> The whole consumed time (ms) is: " + (ts_end - ts_start).toString)

  }


  def getNotify_fresh_mongo_value(tenantName: String, instanceName: String, schemaName: String, tableName: String, tablePrefix: String): String = {

    val mongoValue = "{\"taskType\":\"SECONDARY_TASK\",\""
      .concat("tenantName").concat("\":\"").concat(tenantName).concat("\",\"")
      .concat("instanceName").concat("\":\"").concat(instanceName).concat("\",\"")
      .concat("schemaName").concat("\":\"").concat(schemaName).concat("\",\"")
      .concat("tableName").concat("\":\"").concat(tableName).concat("\",\"")
      .concat("tablePrefix").concat("\":\"").concat(tablePrefix).concat("\"}")

    mongoValue
  }


  // get new rowkey for partition order
  def getNewRowkey_Mongo(row: Document, partitionNum: Int, fieldNames: Array[String]): String = {

    val idCol = row.get("_id")
    val rowkey = Md5Util.getMd5(String.valueOf(idCol))
    val rowkey2Bytes = rowkey.getBytes("UTF-8")
    val splitNum = SplitRegionAlgorithm.getMod(rowkey2Bytes, partitionNum)
    var newRowkey: String = null
    if (splitNum < 10) {
      newRowkey = "00".concat(splitNum.toString).concat(rowkey)
    }
    else if (splitNum < 100) {
      newRowkey = "0".concat(splitNum.toString).concat(rowkey)
    }
    else {
      newRowkey = splitNum.toString.concat(rowkey)
    }
    newRowkey

  }


  // get new row
  def getNewRow_Mongo(row: Document, fieldNames: Array[String], fieldDatatypes: Array[DataType]): Row = {

    val db2DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var order: Int = 0
    val row2Json = row.toJson
    val parser = new JsonParser
    val gson = new Gson()

    val newSeq = fieldNames.map(

      fieldName => {
        var value: String = "NULL"
        val fieldDataType = fieldDatatypes.apply(order)
        val fieldDataTypeStr = fieldDataType.toString
        val fieldDataTypeName = fieldDataType.typeName
        val element = row.get(fieldName)
        // get json element
        val jsonElement = parser.parse(row2Json).getAsJsonObject.get(fieldName)
        val jsonElement2Str = gson.toJson(jsonElement)

        if (element != null) {
          if (fieldDataTypeName == "struct") {
            // Situation 1: nested doc, min, max, _id, timestamp,undefined, etc.
            logger.debug("\n>>>> element != null, nested document")
            logger.debug(">>>>>> jsonElement2Str is: " + jsonElement2Str)
            if (parser.parse(jsonElement2Str).isJsonObject) {
              if (fieldDataTypeStr == Config.mongoTimestampSchema) {
                if (parser.parse(jsonElement2Str).getAsJsonObject.has("$timestamp")) {
                  // new Timestamp(), different from new Date()
                  val tsLong = parser.parse(jsonElement2Str).getAsJsonObject.get("$timestamp").getAsJsonObject.get("t").getAsLong
                  val tsType = parser.parse(jsonElement2Str).getAsJsonObject.get("$timestamp").getAsJsonObject.get("i").getAsInt
                  value = "Timestamp(".concat(String.valueOf(tsLong)).concat(",").concat(String.valueOf(tsType)).concat(")")
                }
              }
              else if (fieldDataTypeStr == Config.mongoBinaryStrSchema) {
                if (parser.parse(jsonElement2Str).getAsJsonObject.has("$binary")) {
                  val binaryStr = parser.parse(jsonElement2Str).getAsJsonObject.get("$binary").getAsString
                  val typeStr = parser.parse(jsonElement2Str).getAsJsonObject.get("$type").getAsString
                  val newTypeStr = typeStr match {
                    case "00" => "0"
                    case "01" => "1"
                    case "02" => "2"
                    case "03" => "3"
                    case "04" => "4"
                    case "05" => "5"
                    case "80" => "128"
                  }
                  value = "Bindata(".concat(newTypeStr).concat(",").concat(binaryStr).concat(")")
                }
              }
              else if (fieldDataTypeStr == Config.mongoUndefinedSchema) {
                value = "undefined"
              }
              else if (fieldDataTypeStr == Config.mongoRegexSchema) {
                // Regex
                if (parser.parse(jsonElement2Str).getAsJsonObject.has("$regex")) {
                  val regexStr = parser.parse(jsonElement2Str).getAsJsonObject.get("$regex").getAsString
                  val optionsStr = parser.parse(jsonElement2Str).getAsJsonObject.get("$options").getAsString
                  value = "/".concat(regexStr).concat("/").concat(optionsStr)
                }
              }
              else if (fieldDataTypeStr == Config.mongoCodeSchema) {
                // js code
                if (parser.parse(jsonElement2Str).getAsJsonObject.has("$code")) {
                  if (jsonElement2Str.startsWith("{\"$code\":")) {
                    value = "{\"code\":".concat(parser.parse(jsonElement2Str).getAsJsonObject.toString.substring("{\"$code\":".length))
                  }
                }
              }
              else if (fieldDataTypeStr == Config.mongoJsScopeSchema) {
                // JS-SCOPE
                value = parser.parse(jsonElement2Str).getAsJsonObject.toString.replace(":{\"$code\":", ":{\"code\":")
              }
              else if (fieldDataTypeStr == Config.mongoOIDSchema) {
                // _id
                value = String.valueOf(element)
              }
              else {
                value = parser.parse(jsonElement2Str).getAsJsonObject.toString
              }
            }
            else {
              value = parser.parse(jsonElement2Str).getAsJsonObject.toString
            }

          }

          else if (fieldDataTypeName == "array") {
            // Situation2: ArrayType
            val row2Json2JsonObj = parser.parse(row2Json).getAsJsonObject
            val jsonElement = row2Json2JsonObj.get(fieldName)
            value = gson.toJson(jsonElement)
          }

          else if (fieldDataTypeName == "binary") {
            // Situation3: BinaryType
            if (parser.parse(jsonElement2Str).getAsJsonObject.has("$binary")) {
              val binaryStr = parser.parse(jsonElement2Str).getAsJsonObject.get("$binary").toString
              val typeStr = parser.parse(jsonElement2Str).getAsJsonObject.get("$type").getAsString
              val newTypeStr = typeStr match {
                case "00" => "0"
                case "01" => "1"
                case "02" => "2"
                case "03" => "3"
                case "04" => "4"
                case "05" => "5"
                case "80" => "128"
              }
              value = "Bindata(".concat(newTypeStr).concat(",").concat(binaryStr).concat(")")
            }
          }

          else if (fieldDataTypeName == "timestamp") {
            // Date
            value = db2DateFormat.format(element)
            val tsValue = element.asInstanceOf[Date].getTime
            value = "%s.".format(value).concat("%09d".format(new Timestamp(tsValue).getNanos))
          }
          else if (fieldDataTypeName == "string") {
            value = jsonElement2Str
            if (jsonElement2Str.startsWith("\"") && jsonElement2Str.endsWith("\"")) {
              value = jsonElement2Str.substring(1, jsonElement2Str.length - 1)
            }
          }
          else {
            // such as: long, double, integer, decimal
            value = String.valueOf(element)
          }
        }
        else {
          // element is null or NullType
          value = "NULL"
        }
        order = order + 1
        value.getBytes("UTF-8")
      })
    val newRow = Row.fromSeq(newSeq)
    newRow

  }


}




