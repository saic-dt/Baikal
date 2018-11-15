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

import java.math.BigDecimal
import java.sql.{Connection, DriverManager, SQLException, Timestamp, Types}
import java.text.SimpleDateFormat

import org.apache.commons.codec.binary.Base64
import org.apache.hadoop.conf.Configuration
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import org.datatech.baikal.fulldump.jdbc.{ChangeParallelismNumStrategy, CheckPrimaryKeys, MySparkJdbcDialect, MySparkJdbcPredicates}
import org.datatech.baikal.fulldump.listener.MySparkListener
import org.datatech.baikal.fulldump.meta.MetaConfigDao
import org.datatech.baikal.fulldump.partitioner.DtPartitioner
import org.datatech.baikal.fulldump.utils.{CreateNotify_freshNode, Md5Util, MongoUtil, SplitRegionAlgorithm}
import org.slf4j.LoggerFactory

import scala.collection.mutable.{ArrayBuffer, ListBuffer, StringBuilder}


/**
  * The spark driver program for full-dumping data from databases(RDBMS & NoSQL) to HDFS in parquet format
  * Four databases are supported, i.e. ORACLE, DB2, MYSQL and MSSQL
  */
class FulldumpMain {

}

object FulldumpMain {

  val logger = LoggerFactory.getLogger(classOf[FulldumpMain])
  var keyList = new ArrayBuffer[String]()

  def main(args: Array[String]): Unit = {

    logger.info(">>>>>> This is the parquet jar for release-success 66666666.")
    logger.info(">>>>>> start receiving args")

    // receive args from task-project jar
    val instanceName = args(0)
    val schemaName = args(1)
    val tableName = args(2)
    val tablePrefix = args(3)
    val dbType = args(4)
    val jdbcUrl = args(5)
    val userName = args(6)
    val password = args(7)
    val zk_quorum = args(8)
    val task_queueip = args(9)
    val tenantName = args(10)
    val ts = args(11)

    val ts_star = System.currentTimeMillis()
    var rowCount: Long = 0
    // row count of the whole table
    val parallelThreshold = 100000
    // premise of parallel sparkJdbc: table has over 100000 rows
    var pkExists: Boolean = false
    // table has Primary keys or not
    var pks: String = ""
    // primary keys concatenating
    var pkBoundList = ArrayBuffer[String]()
    // reserves the ranges of pks which will be given to each thread in parallel sparkJdbc fetching data
    var colTypeList: ArrayBuffer[Tuple4[Int, Int, Int, String]] = new ArrayBuffer[(Int, Int, Int, String)]()
    // reserves column meta info, Tuple._1: columnType,Tuple._2: precision, Tuple._3: scale, Tuple._1: columnTypeName, used for fixing data consistency
    val fetchsizeNode = "sparkJdbc.fetchsize"
    // jdbc fetch size which given in zookeeper
    val partitionNumNode = "sparkJdbc.partitionNum"
    // rdd partitions number which given in zookeeper
    val parallelismNumNode = "sparkJdbc.parallelismNum"
    // sparkjdbc parallelism number which given in zookeeper

    // differ mongodb from other RDBMS
    var isMongo = false
    if (dbType.toUpperCase == "MONGO") {
      isMongo = true
    }

    val confi = new Configuration()
    confi.set("ha.zookeeper.quorum", zk_quorum)
    val nameservice1 = confi.get("fs.defaultFS")
    val schema_table = schemaName.concat(".").concat(tableName)
    val structFields = ListBuffer[StructField]()
    // structFields for constructing schema for new DataFrame
    val pathStr =
      s"${nameservice1}/datalake/${tenantName}/${instanceName}/${schemaName}/${tableName}/${tablePrefix}/"
    val hdfsPath = pathStr.concat(ts)
    // path of writing dataframe as parquet fromat to hdfs
    val jdbcDriver = dbType.toUpperCase match {
      case "ORACLE" => "oracle.jdbc.driver.OracleDriver"
      case "DB2" => "com.ibm.db2.jcc.DB2Driver"
      case "MYSQL" => "com.mysql.jdbc.Driver"
      case "MSSQL" => "com.microsoft.sqlserver.jdbc.SQLServerDriver"
      case "MONGO" => null
    }

    // get column meta info--used for fixing data consistency
    logger.info(">>>>>> start getting the count of rows in the table")
    var checkPrimaryKeys: CheckPrimaryKeys = null
    if (!isMongo) {
      checkPrimaryKeys = new CheckPrimaryKeys(dbType, schemaName, tableName, jdbcDriver, jdbcUrl, userName, password)
      val colTypesTuple = checkPrimaryKeys.getColumnTypes
      colTypeList = colTypesTuple._1 // column type info list, prepared for fixing data consistency in getNewrow
      rowCount = colTypesTuple._2 // count of rows in table
    }
    if (isMongo) {
      // count of documents in mongodb
      rowCount = MongoUtil.mongo_document_count(jdbcUrl, userName, password, schemaName, tableName)
    }

    //update meta info before implementing fulldump
    logger.info(">>>>>> start updating meta before full dumping data.")
    val metaConfigDao = new MetaConfigDao(zk_quorum, task_queueip, tenantName)
    metaConfigDao.updateSoFar(instanceName, schemaName, tableName, 0)
    metaConfigDao.updateTotalWork(instanceName, schemaName, tableName, rowCount)


    // read customized parameters from zookeeper with path: /datalake/config/NODE-NAME
    var fetchsize = Config.SPARK_JDBC_FETCHSIZE
    try {
      fetchsize = Integer.valueOf(metaConfigDao.getFulldumpCustomizables(fetchsizeNode))
    } catch {
      case _: Throwable => logger.error(">>>>>> fetchsize in zk must be a integer,fetchsize is set as default value: " + Config.SPARK_JDBC_FETCHSIZE)
    }
    // fetch size for jdbc
    var partitionNum = Config.SPARK_RDD_PARTITION_NUM
    try {
      partitionNum = Integer.valueOf(metaConfigDao.getFulldumpCustomizables(partitionNumNode))
    } catch {
      case _: Throwable => logger.error(">>>>>> partitionNum in zk must be a integer,partitionNum is set as default value: " + Config.SPARK_RDD_PARTITION_NUM)
    }
    // rdd partitions number
    var parallelismNum = Config.SPARK_RDD_PARALLELISM_Num
    try {
      parallelismNum = Integer.valueOf(metaConfigDao.getFulldumpCustomizables(parallelismNumNode))
    } catch {
      case _: Throwable => logger.error(">>>>>> parallelismNum in zk must be a integer,parallelismNum is set as default value: " + Config.SPARK_RDD_PARALLELISM_Num)
    }
    // parallelism number of SparkJdbc

    val argNamesArray = new ArrayBuffer[(String, String)]()
    argNamesArray.append(("instanceName", instanceName))
    argNamesArray.append(("schemaName", schemaName))
    argNamesArray.append(("tableName", tableName))
    argNamesArray.append(("tablePrefix", tablePrefix))
    argNamesArray.append(("dbType", dbType))
    argNamesArray.append(("tenantName", tenantName))
    argNamesArray.append(("ts", ts))
    argNamesArray.append(("fetchsize", fetchsize.toString))
    argNamesArray.append(("parallelismNum", parallelismNum.toString))
    argNamesArray.append(("partitionNum", partitionNum.toString))
    argNamesArray.append(("parallelThreshold", parallelThreshold.toString))

    logger.info(">>>>>> received args from task-jar are respectively: ")
    argNamesArray.foreach(arg => logger.info(s">>>>>> ${arg._1} is: " + arg._2))

    // different parallelism number for different row number of the table
    logger.info(">>>>>> start optimizing the values of parallelismNum and partitionNum")
    val changedParallel_partition = ChangeParallelismNumStrategy.changeParallelismNum(rowCount, parallelismNum, partitionNum, dbType)
    parallelismNum = changedParallel_partition._1
    partitionNum = changedParallel_partition._2

    // initialize spark context
    val sparkSession = SparkSession.builder().getOrCreate()
    sparkSession.sparkContext.getConf.setAppName("Datalake-3.0 Fulldump")
    val sqlContext = sparkSession.sqlContext
    val sc = sparkSession.sparkContext
    logger.info(">>>>>> start getting jdbcDF via Spark jdbc")

    /*
    *
    * if dbType is Mongo, jump to execute FulldumpMongo.scala and exit program,
    * otherwise continue to executing the following code
    *
    * */

    if (isMongo) {
      // Mongodb fulldump
      // initlize a zookeeper client
      val zkClient = initZooKeeper(confi)
      // create notify_fresh node in zk
      val mongo_notify_freshNodePath = s"${Config.NS_SCHEMA_MONGO_NODE}/${tenantName}/${instanceName}/${schemaName}/${Config.NOTIFY_FRESH_NODE}"
      logger.info(">>>>> notify_freshNodePath is: " + mongo_notify_freshNodePath)
      logger.info(">>>>>> start loading data from MongoDB")
      FulldumpMongo.doMongoFulldump(jdbcUrl, userName, password, tenantName, instanceName, schemaName, tableName, tablePrefix, sc, sparkSession,
        metaConfigDao, partitionNum, parallelismNum, hdfsPath, rowCount, ts_star,
        zkClient, mongo_notify_freshNodePath)
      // exit the whole program
      logger.info(">>>>>> Mongodb fulldump finished.")
      sys.exit()

    }

    var intervalLen: Long = 1L
    if (rowCount >= parallelThreshold) {
      intervalLen = Math.floor((rowCount / parallelismNum).toDouble).toLong
    }
    logger.info(">>>>>> row count is: " + rowCount)
    logger.info(">>>>>> intervalLen is: " + intervalLen)

    // determine if PKs exist or not in the current table
    keyList = checkPrimaryKeys.getPrimaryKeys
    val pkColumnCount = keyList.length
    pkExists = keyList.nonEmpty
    logger.info(">>>>>> pkColumnCount is: " + pkColumnCount)
    logger.info(">>>>>> pk exists ? -> " + pkExists)
    metaConfigDao.updateSoFar(instanceName, schemaName, tableName, Math.floor(rowCount * 0.03).toLong)

    val ts_pkboundries_start = System.currentTimeMillis()

    // if pk exists and rowCount>=100000, figure out the pks and pkBoundList, prepare for parallel sparkJdbc
    if (pkExists && rowCount >= parallelThreshold) {
      val pks_pkBoundList = checkPrimaryKeys.getPksBoundList(rowCount, intervalLen, keyList)
      pkBoundList = pks_pkBoundList._1
      pks = pks_pkBoundList._2
    }
    // end of querying PKs start-end values, i.e. pkBoundList, for each sparkJdbc thread
    val ts_pkboundries_end = System.currentTimeMillis()
    logger.info(">>>>>> ts_pkboundries_query_time is (second): " + (ts_pkboundries_end - ts_pkboundries_start) / 1000)

    // update sofarwork when queries before spark jdbc finished
    metaConfigDao.updateSoFar(instanceName, schemaName, tableName, Math.floor(rowCount * 0.1).toLong)


    /*
     * Two premises of parallel sparkJdbc:
     * 1. rowCount >= parallelThreshold;
     * 2. primary keys exist
     * */

    // setup SparkListener to listen the progress of spark job
    logger.info(">>>>>> start spark listener")
    val sparkListener = new MySparkListener(metaConfigDao, instanceName, schemaName, tableName, rowCount, parallelismNum, partitionNum, pkExists)
    sc.addSparkListener(sparkListener)

    // introduce self-defined Spark JdbcDialect for fixing unsupported data type and data consistency issues
    MySparkJdbcDialect.useMyJdbcDIalect(jdbcUrl, dbType)

    // fetching data from database by using parallel sparkJdbc
    if (pkExists && rowCount >= parallelThreshold) {
      logger.info(">>>>>> pk exists, with pks as index, pull data by using parallel sparkJdbc.")
      val prop = new java.util.Properties
      prop.setProperty("driver", s"${jdbcDriver}")
      prop.setProperty("user", s"${userName}")
      prop.setProperty("password", s"${password}")
      prop.setProperty("fetchsize", s"${fetchsize}")
      logger.info(">>>>> pkBoundList.length-1 is: " + (pkBoundList.length - 1))

      // get sparkJdbc predicates array
      logger.info(">>>>>> start getting predicates array for parallel sparkJdbc")
      val predicates = MySparkJdbcPredicates.getPredicatesArray(pkBoundList.toArray, pks, parallelismNum)
      //predicates.foreach(element => logger.debug(">>>>>> predicates include: " + element))

      // initlize a zookeeper client
      val zkClient = initZooKeeper(confi)
      // create notify_fresh node in zk
      val notify_freshNodePath = s"${Config.NS_SCHEMA_NODE}/${tenantName}/${instanceName}/${schemaName}/${Config.NOTIFY_FRESH_NODE}"
      logger.info(">>>>> notify_freshNodePath is: " + notify_freshNodePath)
      CreateNotify_freshNode.createSeqNode(zkClient, notify_freshNodePath, false, "")

      // fetched data reserved as spark dataframe
      logger.info(">>>>>> start generating spark dataframe")
      val jdbcDF = sqlContext.read.jdbc(jdbcUrl, schema_table, predicates, prop)

      // get cipher index from ACL in zookeeper
      val DF_Schema = jdbcDF.schema
      val fieldNames = DF_Schema.fieldNames

      logger.info(">>>>>> DF_schema is:" + DF_Schema)


      // get new RDD, including new Rowkey, fixing data consistency(compare with incremental data obtained)
      val rdd = jdbcDF.rdd
      val dbTypeB = sc.broadcast(dbType)
      val partitionNumB = sc.broadcast(partitionNum)
      val colTypeListB = sc.broadcast(colTypeList)
      val keyListB = sc.broadcast(keyList.toArray)
      val fieldNamesB = sc.broadcast(fieldNames)
      val rddMap: RDD[(String, Row)] = rdd.mapPartitions { partition => {
        val dbTypeBV = dbTypeB.value
        val partitionNumBV = partitionNumB.value
        val colTypeListBV = colTypeListB.value
        val keylistBV = keyListB.value
        val fieldNamesBV = fieldNamesB.value
        partition.map(row => (getNewRowkey(row, partitionNumBV, keylistBV, fieldNamesBV), getNewRow(row, dbTypeBV, colTypeListBV.toArray)))
      }
      }

      val ts_mapPartitionFinished = System.currentTimeMillis()

      // get new RDD partitions
      logger.info(">>>>>> start getting RDD partitions")
      val rddPartitions = rddMap.partitionBy(new DtPartitioner(partitionNum))
      val newRDD = rddPartitions.values

      // construct schema for new RDD, and write the DataFrame to HDFS in parquet format
      for (i <- 0 until DF_Schema.length) {
        structFields.append(StructField(DF_Schema.apply(i).name, BinaryType, nullable = true))
      }
      val schema = StructType(structFields.toList)
      logger.info(">>>>>> new schema is: " + schema)
      logger.info(">>>>>> start creating new dataframe")
      val newDF = sparkSession.sqlContext.createDataFrame(newRDD, schema)
      logger.info(">>>>>> start writing dataframe to hdfs in parquet format")
      newDF.write.mode(SaveMode.Overwrite).parquet(hdfsPath)
    }


    // fetching data fro database by using SparkJdbc without parallelism
    if (!pkExists || rowCount < parallelThreshold) {

      metaConfigDao.updateSoFar(instanceName, schemaName, tableName, Math.floor(rowCount * 0.1).toLong)

      // initlize a zookeeper client
      val zkClient = initZooKeeper(confi)
      // create notify_fresh node in zk
      val notify_freshNodePath = s"${Config.NS_SCHEMA_NODE}/${tenantName}/${instanceName}/${schemaName}/${Config.NOTIFY_FRESH_NODE}"
      logger.info(">>>>> notify_freshNodePath is: " + notify_freshNodePath)
      CreateNotify_freshNode.createSeqNode(zkClient, notify_freshNodePath, false, "")

      logger.info(">>>>>> start general spark jdbc fetching data\n\n\n")
      val jdbcDF = sqlContext.read.format("jdbc").options(
        Map("url" -> jdbcUrl,
          "user" -> userName,
          "dbtable" -> schema_table,
          "password" -> password,
          "driver" -> jdbcDriver,
          "fetchsize" -> s"${fetchsize}")).load()

      val rdd = jdbcDF.rdd
      val DF_Schema = jdbcDF.schema
      logger.info(">>>>>> DF_schema is:" + DF_Schema)
      val fieldNames = DF_Schema.fieldNames

      // get new RDD: new Rowkey, data consistency
      val dbTypeB = sc.broadcast(dbType)
      val partitionNumB = sc.broadcast(partitionNum)
      val colTypeListB = sc.broadcast(colTypeList)
      val keyListB = sc.broadcast(keyList.toArray)
      val fieldsNamesB = sc.broadcast(fieldNames)
      val rddMap: RDD[(String, Row)] = rdd.mapPartitions { partition => {
        val dbTypeBV = dbTypeB.value
        val partitionNumBV = partitionNumB.value
        val colTypeListBV = colTypeListB.value
        val keyListBV = keyListB.value
        val fieldNamesBV = fieldsNamesB.value
        partition.map(row => (getNewRowkey(row, partitionNumBV, keyListBV, fieldNamesBV), getNewRow(row, dbTypeBV, colTypeListBV.toArray)))
      }
      }

      // get RDD partitions
      logger.info(">>>>>> get RDD partitions")
      val rddPartitions = rddMap.partitionBy(new DtPartitioner(partitionNum))
      val newRDD = rddPartitions.values

      //  construct schema for new RDD
      for (i <- 0 until DF_Schema.length) {
        structFields.append(StructField(DF_Schema.apply(i).name, BinaryType, nullable = true))
      }
      val schema = StructType(structFields.toList)
      logger.info(">>>>>> new schema is: " + schema)

      // write the DataFrame to HDFS in parquet format
      logger.info(">>>>>> start creating new dataframe")
      val newDF = sparkSession.sqlContext.createDataFrame(newRDD, schema)
      logger.info(">>>>>> start writing dataframe to hdfs in parquet format")
      newDF.write.mode(SaveMode.Overwrite).parquet(hdfsPath)
    }

    //update meta info after fulldump
    logger.info(">>>>>> path of writing parquet is:" + hdfsPath)
    logger.info(">>>>>> start updating meta after full dumping data.")
    metaConfigDao.updateSoFar(instanceName, schemaName, tableName, rowCount)
    logger.info(">>>>>>> fulldump successed, update META_FLAG as 3")
    metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, Config.META_FLAG_FULLDUMP_END)
    logger.info(">>>>>> updating meta finished")
    val ts_end = System.currentTimeMillis()
    logger.info(">>>>>> The whole consumed time (ms) is: " + (ts_end - ts_star).toString)

  }


  // initlize zookeeper client
  def initZooKeeper(conf: Configuration): ZooKeeper = {
    new ZooKeeper(conf.get("ha.zookeeper.quorum"), 30000, new Watcher {
      override def process(watchedEvent: WatchedEvent): Unit = {
        //println(">>>>>> zkWatcher, state:" + watchedEvent.getState + ":" + watchedEvent.getType + ":" + watchedEvent.getWrapper() + ":" + watchedEvent.getPath())
      }
    })
  }

  /*
   * get a new rowkey from the row of DataFrame
   */
  def getNewRowkey(row: Row, partitionNum: Int, keyList: Array[String], fieldNames: Array[String]): String = {

    var rowkeyValue = ""
    val seq = row.toSeq
    val newSeq = seq.map(element => String.valueOf(element))

    if (keyList.nonEmpty) {
      // if pks exist, use pks as as rowkey
      val fieldsIndexes = keyList.map(element => fieldNames.indexOf(element))
      val pkColumnValues = fieldsIndexes.map(element => newSeq.apply((element)))
      rowkeyValue = pkColumnValues.addString(new StringBuilder(), "|").toString()

    }
    else {
      // if pks exist not, use all fields concat as rowkey
      rowkeyValue = newSeq.addString(new StringBuilder(), "|").toString()

    }
    val rowkey = Md5Util.getMd5(rowkeyValue)
    val rowkey2Bytes = rowkey.getBytes("UTF-8")
    val splitNum = SplitRegionAlgorithm.getMod(rowkey2Bytes, partitionNum)
    // splitNum: partition order
    var newRowkey = ""
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

  /*
   * get new row, fix data consistency issue(should has same format with incremental data) and convert all fields into binary
   */
  def getNewRow(row: Row, dbType: String, colTypeList: Array[Tuple4[Int, Int, Int, String]]): Row = {

    val tzFormat = new SimpleDateFormat("XXX")
    val db2DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val time_Format = new SimpleDateFormat("HH:mm:ss")
    val ymd_format = new SimpleDateFormat("yyyy-MM-dd")

    var order = 0
    val seq = row.toSeq

    val newSeq = seq.map(element => {

      val colType = colTypeList(order)._1
      val precision = colTypeList(order)._2
      val scale = colTypeList(order)._3
      var value = "NULL"

      if (colType == Types.BLOB) {
        value = Base64.encodeBase64String(element.asInstanceOf[Array[Byte]])
      } else {
        if ((dbType == "ORACLE") && (colType == Types.TIMESTAMP || colType == -101 || colType == -102)) { // format Oracle DATE and TIMESTAMP column type
          val tsValue: Timestamp = element.asInstanceOf[Timestamp]
          if (tsValue != null) {
            value = db2DateFormat.format(tsValue) //format timeStamp
            if (scale != 0) { // Oracle TIMESTAMP column type, append nano second
              value = "%s.".format(value).concat("%09d".format(tsValue.getNanos))
              if (colType == -101) { // Oracle TIMESTAMP WITH TIME ZONE column type, append timezone
                value = String.format("%s %s", value, tzFormat.format(tsValue))
              }
            }
          }
        } else {
          if ((dbType == "ORACLE") && colType == Types.NUMERIC && scale > 0) { // format Oracle NUMBER(X,Y) column type
            val strValue = element.asInstanceOf[String]
            if (strValue != null) {
              val decValue = new BigDecimal(element.asInstanceOf[String])
              if (decValue != null) {
                value = decValue.setScale(scale).toPlainString
              }
            }
          } else {
            if ((dbType == "ORACLE") && colType == Types.NUMERIC && scale == -127) { // format Oracle FLOAT column type
              val strValue = element.asInstanceOf[String]
              if (strValue != null) {
                val floatValue = new BigDecimal(element.asInstanceOf[String])
                if (floatValue != null) {
                  value = floatValue.toPlainString
                }
              }
            } else {
              if ((((dbType == "MYSQL") || (dbType == "MSSQL")) && (colType == Types.REAL || colType == Types.DOUBLE)) || ((dbType == "DB2") && colType == Types.DOUBLE)) { // format MySQL/SQL Server FLOAT/DOUBLE and DB2 DOUBLE column type
                val strValue = element.asInstanceOf[String]
                if (strValue != null) {
                  val decValue = new BigDecimal(element.asInstanceOf[String])
                  if (decValue != null) {
                    value = decValue.stripTrailingZeros.toPlainString
                  }
                }

              } else {
                if ((dbType == "MYSQL") && (colType == Types.TIMESTAMP || colType == Types.TIME || colType == Types.DATE)) { // format MySQL TIME, DATETIME, TIMESTAMP column type
                  if (colType == Types.TIMESTAMP || colType == Types.TIME) {
                    val tsValue: Timestamp = element.asInstanceOf[Timestamp]
                    if (tsValue != null && colType == Types.TIMESTAMP) {
                      value = db2DateFormat.format(tsValue)
                      // Oracle TIMESTAMP column type, append nano second
                      value = value.concat(".000000")
                    }
                    if (colType == Types.TIME && tsValue != null) { //time
                      value = time_Format.format(tsValue).concat(".000000")
                    }
                  }
                  else if (colType == Types.DATE) {
                    val tsValue = element.asInstanceOf[String]
                    if (tsValue != null) {
                      value = tsValue.substring(0, precision)
                    }
                  }
                }
                else {
                  if ((dbType == "DB2") && (colType == Types.TIMESTAMP || colType == Types.TIME || colType == Types.DATE)) { // format DB2 TIMESTAMP column type
                    val tsValue = element.asInstanceOf[Timestamp]
                    if (colType == Types.TIME && tsValue != null) {
                      value = time_Format.format(tsValue)
                    }
                    if (colType == Types.TIMESTAMP && tsValue != null) {
                      value = db2DateFormat.format(tsValue)
                      value = "%s.".format(value).concat("%09d".format(tsValue.getNanos))
                    }
                    if (colType == Types.DATE && tsValue != null) {
                      value = ymd_format.format(tsValue)
                    }
                  } else {
                    if ((dbType == "MSSQL") && (colType == Types.TIMESTAMP || colType == -155)) { // format SQL Server DATETIME2, DATETIME, DATETIMEOFFSET and SMALLDATETIME column type
                      if (element.asInstanceOf[String] != null) {
                        val tsValue = Timestamp.valueOf(element.asInstanceOf[String].substring(0, 19))
                        if (tsValue != null) {
                          if (precision == 16) { // format DATETIMEOFFSET to local time
                            value = db2DateFormat.format(new Timestamp(tsValue.getTime))
                          } else {
                            value = db2DateFormat.format(tsValue)
                          }
                          var endZeros = ""
                          val realScale = element.asInstanceOf[String].length - 20
                          var scaleChars = ""
                          if (realScale > 9) {
                            scaleChars = element.asInstanceOf[String].substring(20, element.asInstanceOf[String].length - 7)
                          }
                          else {
                            scaleChars = element.asInstanceOf[String].substring(20)
                          }
                          for (i <- 1 to (9 - scaleChars.length)) {
                            endZeros = endZeros.concat("0")
                          }
                          value = value.concat(".").concat(scaleChars).concat(endZeros)
                          if (colType == -155) { // SQL Server DATETIMEOFFSET column type, append timezone
                            value = String.format("%s %s", value, tzFormat.format(tsValue))
                          }
                        }
                      }

                    } else {
                      if (dbType == "MSSQL" && colType == Types.TIME) {
                        if (element.asInstanceOf[String] != null) {
                          val tsValue = element.asInstanceOf[String].substring(0, 8)
                          if (tsValue != null) {
                            var endZeros = ""
                            val realScale = element.asInstanceOf[String].length - 9
                            var scaleChars = ""
                            if (realScale > 9) {
                              scaleChars = element.asInstanceOf[String].substring(9, element.asInstanceOf[String].length - 7)
                            }
                            else {
                              scaleChars = element.asInstanceOf[String].substring(9)
                            }
                            for (i <- 1 to (9 - scaleChars.length)) {
                              endZeros = endZeros.concat("0")
                            }
                            value = tsValue.concat(".").concat(scaleChars).concat(endZeros)

                          }
                        }
                      } else {
                        value = String.valueOf(element)
                        if (element == null) {
                          value = "NULL"
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      order = order + 1
      value.getBytes("UTF-8")
    })
    val newRow = Row.fromSeq(newSeq)
    newRow

  }

  /*
   * get a jdbc connection
   */
  def getConnection(driver: String, url: String, userName: String, password: String): Connection = {

    var connection: Connection = null
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, userName, password)
    } catch {
      case e: ClassNotFoundException => e.printStackTrace()
      case e: SQLException => e.printStackTrace()
    } finally {

    }

    connection

  }


}

