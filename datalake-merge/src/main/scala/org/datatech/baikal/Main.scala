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

package org.datatech.baikal

import java.net.URI
import java.sql.DriverManager
import java.util.Random

import com.google.gson.{Gson, JsonParser}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}
import org.apache.hadoop.security.UserGroupInformation
import org.apache.spark.sql.types.{BinaryType, StructField, StructType}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.zookeeper.KeeperException.NoNodeException
import org.apache.zookeeper.ZooKeeper
import org.datatech.baikal.merge.constants.{DatalakeConstant, ZooKeeperConstants}
import org.datatech.baikal.merge.model.{DatalakeHDFSNode, SourceJdbcVO}
import org.datatech.baikal.merge.reader.IncrementalFileReader
import org.datatech.baikal.merge.util._
import org.datatech.baikal.partitioner.DatalakePartitioner
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.reflect.ClassTag
import scala.util.control.Breaks.{break, breakable}

object Main {
  val log: Logger = LoggerFactory.getLogger("Main")

  import java.util.UUID

  val uuid: String = UUID.randomUUID.toString

  val hdfsRoot: String = ZooKeeperConstants.DATALAKE_ROOT

  var sourcePK: Array[String] = _

  def main(args: Array[String]): Unit = {
    logging("")
    logging("******************* Datalake Merge Start. *********************")
    val sparkSession = SparkSession.builder()
      .config("spark.kryoserializer.buffer.max", 1920)
      .config("spark.driver.maxResultSize", "8g")
      .getOrCreate()
    val sqlContext = sparkSession.sqlContext
    val conf = sparkSession.sparkContext.hadoopConfiguration
    logging(">> Process UUID:" + uuid)
    // init ZK
    val zooKeeperClient: ZooKeeper = initZooKeeper(conf)
    val skipTblList = getSkipTBLList(zooKeeperClient)
    logging(s">> skip table list:$skipTblList")
    val skipDBList = getSkipDBList(zooKeeperClient)
    logging(s">> skip table list:$skipDBList")
    // scan HDFS
    val fileSystem = FileSystem.get(URI.create(hdfsRoot), conf)
    val nodeShouldbeMerged =
      if (args.length > 0)
        scanSpecifiedTable(zooKeeperClient, args(0), fileSystem)
      else
        scan(zooKeeperClient, fileSystem, skipDBList, skipTblList)
    if (nodeShouldbeMerged != null) {
      // init keydb in driver and broadcast
      log.info(">> SourcePK:" + sourcePK)

      // clean "___temp"
      cleanTemp(FileSystem.get(URI.create(ZooKeeperConstants.DATALAKE_ROOT), conf), nodeShouldbeMerged)
      val broadcastValues = sparkSession.sparkContext.broadcast(sourcePK)
      log.info(">> NodeShouldbeMerged.getAbsolutePath:" + nodeShouldbeMerged.absolutePath)
      val updateData = new mutable.HashMap[String, String]()
      val deletedData = new mutable.HashMap[String, String]()
      val insertData = new ArrayBuffer[Row]()
      val columnMap = new mutable.HashMap[String, String]()
      val originalDF = sqlContext.read.parquet(nodeShouldbeMerged.absolutePath)
      //originalDF.show(20)
      val rdd = originalDF.rdd

      var originalSchema = originalDF.schema
      log.info(">>: originalSchema : " + originalSchema)

      // consider there are more columns in incremental files
      originalSchema = updateSchema(zooKeeperClient, nodeShouldbeMerged, originalSchema, columnMap)
      log.info(">> new update schema:" + originalSchema)

      // read incremental data
      val incrementalDir = ZooKeeperConstants.DATALAKE_ROOT.concat("/")
        .concat(nodeShouldbeMerged.tenantName).concat("/")
        .concat(nodeShouldbeMerged.instanceName).concat("/")
        .concat(nodeShouldbeMerged.schemaName).concat("/")
        .concat(nodeShouldbeMerged.tableName).concat("/")
        .concat(nodeShouldbeMerged.prefix).concat("/incremental")
      logging(">> incrementalDir:" + incrementalDir)
      val incrementalFiles = getIncrementalFilePaths(conf, incrementalDir, nodeShouldbeMerged)
      val filterFiles = incrementalFiles.filter(checkTimestampByString(nodeShouldbeMerged.lastTs, _))
      logging(">> Should be merged incrementalFiles:")
      for (incrementalFile <- filterFiles) {
        logging(incrementalFile)
      }
      IncrementalFileReader.readIncrementalData(
        filterFiles, originalSchema, updateData, deletedData, insertData,
        getSourcePKArray(nodeShouldbeMerged.meta.SOURCE_PK),
        FileSystem.get(URI.create(ZooKeeperConstants.DATALAKE_ROOT), conf))

      // merge
      val insertRdd = sqlContext.sparkContext.parallelize(insertData)
      // rdd1 is Row[String]
      val rdd1 = if (deletedData.isEmpty) {
        rdd
      } else {
        rdd.filter(row => checkDelete(broadcastValues.value, row, deletedData, originalSchema))
      }
      val rdd2 = if (updateData.isEmpty) {
        rdd1
      } else {
        rdd1.map(row => merge(broadcastValues.value, row, updateData, originalSchema))
      }
      val rdd3 = if (insertData.isEmpty) {
        rdd2
      } else {
        rdd2 ++ insertRdd
      }

      // schema broadcast
      val broadcastSchema = sparkSession.sparkContext.broadcast(originalSchema)

      // rdd add new column
      val rdd4 = rdd3.map(x => add(x, broadcastSchema.value))

      //construct full rdd
      //val fullRdd = sqlContext.createDataFrame(rdd3, originalSchema).rdd
      val originalRdd = rdd4.map { row => (getNewRowkey(row), getNewRow(row)) }
      // re-partition
      val rddPartitions = originalRdd.partitionBy(new DatalakePartitioner(64))
      val newRDD = rddPartitions.values
      // new hdfs Path
      val hdfsNewTempPath = nodeShouldbeMerged.getNewHDFSTempPath
      /** write to parquet **/
      val newDF = sparkSession.sqlContext.createDataFrame(newRDD, originalSchema)
      newDF.write.mode(SaveMode.Overwrite).parquet(hdfsNewTempPath)

      /** modify "\_\_\_temp" filename **/
      val hdfsRoot = ZooKeeperConstants.DATALAKE_ROOT
      val fileSystem = FileSystem.get(URI.create(hdfsRoot), conf)
      fileSystem.rename(new Path(hdfsNewTempPath), new Path(nodeShouldbeMerged.getNewHDFSPath))
      logging(s">> Table ${nodeShouldbeMerged.getTableAbsolutePath} merge successful!")

      /** delete zk lock **/
      logging(">> Remove zk seq lock node..")
      ZooKeeperUtil.removeSeqNode(zooKeeperClient, nodeShouldbeMerged, uuid)

      /** update zk last successful merge node **/
      logging(">> Update ZooKeeper Last-Successful-Merge node..")
      ZooKeeperUtil.updateLastSuccessNode(zooKeeperClient, nodeShouldbeMerged)

      /** re-create hive table **/
      val hiveDbName = nodeShouldbeMerged.tenantName.concat("___")
        .concat(nodeShouldbeMerged.instanceName).concat("___")
        .concat(nodeShouldbeMerged.schemaName)
      val hiveTableName = nodeShouldbeMerged.tableName
      val newPathUri = fileSystem.getFileStatus(new Path(nodeShouldbeMerged.getNewHDFSPath)).getPath.toUri
      val urlHead = newPathUri.getScheme + "://" + newPathUri.getAuthority
      //get hive table schema
      val jdbcUrl = ZooKeeperUtil.getNodeData(zooKeeperClient, ZooKeeperConstants.DATALAKE_HIVE_JDBC_URL)
      val jdbcUser = ZooKeeperUtil.getNodeData(zooKeeperClient, ZooKeeperConstants.DATALAKE_HIVE_JDBC_USER)
      val jdbcPassword = ZooKeeperUtil.getNodeData(zooKeeperClient, ZooKeeperConstants.DATALAKE_HIVE_JDBC_PASSWORD)
      Class.forName(DatalakeConstant.HIVE_JDBC_DRIVER)
      val conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)
      val statement = conn.createStatement()
      val hiveDDL = s"alter table ${hiveDbName + "." + hiveTableName} set location '"
        .concat(s"${urlHead + nodeShouldbeMerged.getNewHDFSPath}'")
      logging(">> Update hive table:" + hiveDDL)
      statement.execute(hiveDDL)
      statement.close()
      conn.close()
      statement.close()
      conn.close()
    }
    logging("******************** Datalake Merge End. **********************")
  }

  // kerberos authentication
  def login(keytabPath: String, principal: String): Unit = {
    if (keytabPath != null && principal != null) {
      UserGroupInformation.loginUserFromKeytab(principal, keytabPath)
    }
  }

  def scanSpecifiedTable(zooKeeperClient: ZooKeeper, tablePath: String, fileSystem: FileSystem): DatalakeHDFSNode = {
    logging(s">> Specify scanning table path: $tablePath")
    val elements = tablePath.split("/")
    if (elements.length != 6) {
      logging(s">> Input Table Absolute Path not incorrect!")
      return null
    }
    val rootPath = elements(0).concat("/").concat(elements(1))
    if (!rootPath.equals(hdfsRoot)) {
      logging(s">> Input Table Absolute Path not incorrect!")
      return null
    }
    val tenantName = elements(2)
    val tenantPath = rootPath.concat("/").concat(tenantName)
    logging(s"Specific Tenant Absolute Path is: $tenantPath")
    val instanceName = elements(3)
    val instancePath = tenantPath.concat("/").concat(instanceName)
    logging(s"Specific Instance Absolute Path is: $instancePath")
    val schemaName = elements(4)
    val schemaPath = instancePath.concat("/").concat(schemaName)
    logging(s"Specific Schema Absolute Path is: $schemaPath")
    val tableName = elements(5)
    judgeOneTable(zooKeeperClient, fileSystem, tablePath, tenantName, instanceName, schemaName, tableName)
  }

  /*
   * get a new rowkey from the row of DataFrame
   */
  def getNewRowkey(row: Row): String = {
    val seq = row.toSeq
    val strBuilder = new StringBuilder()
    val rowkeyValue = seq.addString(strBuilder, "|").toString()
    val rowkey = Md5Util.getMD5(rowkeyValue)
    val rowkey2Bytes = rowkey.getBytes("UTF-8")
    // log.info(">> new rowkey:" + rowkey)
    val splitNum = SplitRegionAlgorithm.getMod(rowkey2Bytes)
    // log.info(">> splitNum:" + splitNum)
    //val newRowkey = splitNum.toString.concat(rowkey)
    var newRowkey = ""
    if (splitNum < 10) {
      newRowkey = "0".concat(splitNum.toString).concat(rowkey)
    }
    else {
      newRowkey = splitNum.toString.concat(rowkey)
    }
    newRowkey
  }

  /*
   * get new row with new rowkey
   */
  def getNewRow(row: Row): Row = {
    // encrypt the values in the row

    val seq = row.toSeq
    val newSeq = seq.map(element => String.valueOf(element))
    //val Seq2Bytes = newSeq.map(element => CipherUtil.encrypt(element.getBytes("UTF-8"), 1, 1))
    //val Seq2Bytes = newSeq.map(element => element.getBytes("UTF-8"))
    val newRow = Row.fromSeq(newSeq)
    newRow
  }

  /**
    * get incremental files except the newest one
    *
    * @param conf        hadoop conf
    * @param incremental incremental path
    * @return array
    */
  def getIncrementalFilePaths(conf: Configuration, incremental: String, nodeShouldbeMerged: DatalakeHDFSNode): Array[String] = {
    val path = new Path(incremental)
    val fs = FileSystem.get(URI.create(incremental), conf)
    if (fs.exists(path)) {
      val dirs = fs.listStatus(path)
      val result = new Array[String](dirs.length - 1)
      for (i <- result.indices) {
        result(i) = dirs(i).getPath.toString
      }
      result

    } else {
      Array[String]()
    }
  }

  def getSourcePKArray(sourcepk: String): Array[String] = {
    if (sourcepk == null)
      Array[String]()
    if (sourcepk.equals("")) {
      Array[String]()
    } else {
      sourcepk.split(",").map(_.trim)
    }
  }

  def checkFiles(filelist: Array[FileStatus]): Boolean = {
    val left = filelist(filelist.length - 2)
    val right = filelist(filelist.length - 1)
    val leftTs = java.lang.Long.valueOf(left.getPath.getName.substring(2))
    val rightTs = java.lang.Long.valueOf(right.getPath.getName.substring(2))
    logging(s">> The last 2 incremental ts are: $leftTs and $rightTs")
    val b = (System.currentTimeMillis() - rightTs > 1000 * 60 * 10) || (rightTs - leftTs > 1000 * 60 * 10)
    if (!b)
      logging(">> Because the newest incremental file was created inside 10 minutes, and last 2 incremental files creating time differenct inside 10 minutes, so we judge it is in peak and it shouldn't be merged.")
    b
  }

  def checkLastLen(filelist: Array[FileStatus]): Boolean = {
    val file = filelist(filelist.length - 2)
    filelist.length > 2 || (filelist.length == 2 && file.getLen > 1024 * 1024 * 40)
  }

  // scan the origin parquet file and merge the origin row & row from inc data
  def merge(sourcePK: Array[String], originRow: Row, changeData: mutable.HashMap[String, String], schema: StructType): Row = {
    val jp = new JsonParser
    //val newRow = CommonUtils.convert2SparkDataType(originRow, schema, dateFormat, timestampFormat)
    val pkIndices = sourcePK.map(schema.fieldNames.indexOf(_))
    val key = IncrementalFileReader.generateKey(sourcePK, pkIndices, originRow, schema)
    val targetValue = changeData.get(key)
    // origin row not exist in inc data
    if (targetValue.isEmpty) {
      originRow
    } else {
      //origin row is 'u' or 'uk' in inc data
      val jsonOb = jp.parse(targetValue.get).getAsJsonObject
      val rowFromIncData = Utils.convertJson2Row(jsonOb, schema)
      //log.info(" >> rowFromIncData: " + rowFromIncData.toString())
      val arr = new ArrayBuffer[Any]()
      for (i <- 0 until rowFromIncData.size) {
        if (rowFromIncData(i) == null) {
          if (i <= originRow.length - 1)
            arr += originRow(i)
          else
            arr += "null"
        } else {
          arr += rowFromIncData(i)
        }
      }
      Row.fromSeq(arr)
    }
  }

  def checkDelete(sourcePK: Array[String], row: Row, deletedKeys: mutable.HashMap[String, String], schema: StructType): Boolean = {
    val pkIndices = sourcePK.map(schema.fieldNames.indexOf(_))
    val key = IncrementalFileReader.generateKey(sourcePK, pkIndices, row, schema)
    if (deletedKeys.get(key).isEmpty) {
      true
    } else {
      false
    }
  }

  def checkTimestampByString(tableName: String, incrementalFilePath: String): Boolean = {
    val incrementalFileName = new Path(incrementalFilePath).getName
    if (incrementalFileName.compareTo(tableName) >= 0) {
      logging(s">> $incrementalFileName is allowed to be merged.")
      return true
    }
    logging(s">> $incrementalFileName is not allowed to be merged.")
    false
  }

  def checkTimestampByFS(tableName: String, incrementalFile: FileStatus): Boolean = {
    val incrementalFileName = incrementalFile.getPath.getName
    if (incrementalFileName.compareTo(tableName) >= 0) {
      logging(s">> $incrementalFileName bigger than or equal with table timestamp.")
      return true
    }
    logging(s">> $incrementalFileName small than table timestamp.")
    false
  }

  def cleanTemp(fileSystem: FileSystem, node: DatalakeHDFSNode): Unit = {
    val prefixPath = node.getPrefixAbsolutePath
    val list = fileSystem.listStatus(new Path(prefixPath))
    for (file <- list) {
      val name = file.getPath.getName
      if (name.contains("temp")) {
        val fullname = prefixPath.concat("/").concat(name)
        log.info(" delete tmp:" + fullname)
        fileSystem.delete(new Path(fullname), true)
      }
    }
  }

  def getSourceJdbc(zooKeeperClient: ZooKeeper, node: DatalakeHDFSNode): SourceJdbcVO = {
    val path = ZooKeeperConstants.DATALAKE_ROOT
      .concat(ZooKeeperConstants.METASTORE_NODE)
      .concat("/").concat(node.tenantName)
      .concat(ZooKeeperConstants.SOURCEJDBC_NODE)
      .concat("/").concat(node.instanceName)
      .concat("/").concat(node.schemaName)
    var realPath = path
    if (path.startsWith("hdfs://")) {
      realPath = realPath.substring(7)
      val firstCh = realPath.indexOf('/')
      realPath = realPath.substring(firstCh)
    }
    val data = zooKeeperClient.getData(realPath, false, null)
    val gson = new Gson()
    gson.fromJson(new String(data), classOf[SourceJdbcVO])
  }

  def getSkipTBLList(zooKeeperClient: ZooKeeper): Seq[String] = {
    val data = zooKeeperClient.getData(ZooKeeperConstants.DATALAKE_MERGE_SKIP_TBL_LIST, false, null)
    new String(data).split(",")
  }

  def getSkipDBList(zooKeeperClient: ZooKeeper): Seq[String] = {
    val data = zooKeeperClient.getData(ZooKeeperConstants.DATALAKE_MERGE_SKIP_DB_LIST, false, null)
    new String(data).split(",")
  }

  def updateSchema(client: ZooKeeper, node: DatalakeHDFSNode,
                   originalSchema: StructType, columnMap: mutable.HashMap[String, String]): StructType = {
    // get jdbc from sourcejdbc
    val sourceJdbcVO = getSourceJdbc(client, node)
    sourceJdbcVO.DB_TYPE match {
      case "ORACLE" =>
        Class.forName(DatalakeConstant.JDBC_CLASS_NAME_ORACLE)
      case "MYSQL" =>
        Class.forName(DatalakeConstant.JDBC_CLASS_NAME_MYSQL)
      case "MSSQL" =>
        Class.forName(DatalakeConstant.JDBC_CLASS_NAME_MSSQL)
      case "DB2" =>
        Class.forName(DatalakeConstant.JDBC_CLASS_NAME_DB2)
    }

    // connect jdbc get table meta
    val connection = DriverManager.getConnection(sourceJdbcVO.JDBC_URL, sourceJdbcVO.USER, sourceJdbcVO.PASSWORD)
    val sql = "select * from " + node.schemaName + "." + node.tableName + " where 0 = 1"
    val statement = connection.createStatement()
    val res = statement.executeQuery(sql)
    val metadata = res.getMetaData

    val columnCount = metadata.getColumnCount

    if (columnCount <= originalSchema.fields.length)
      return originalSchema

    // substract
    val newStructFieldList = new ListBuffer[StructField]

    for (i <- 1 to columnCount) {
      val columnName = metadata.getColumnName(i)
      var flag = false
      breakable {
        for (field <- originalSchema.fields) {
          if (field.name.equalsIgnoreCase(columnName)) {
            flag = true
            break
          }
        }
      }
      if (!flag)
        newStructFieldList.append(StructField(columnName, BinaryType, nullable = true))
    }

    //log.info(">> newStructFieldList: " + newStructFieldList)

    val originalFields = originalSchema.fields
    val fields = new ListBuffer[StructField]
    fields.appendAll(originalFields)
    fields.appendAll(newStructFieldList)
    StructType(fields)
  }

  def add(originalRow: Row, updateSchema: StructType): Row = {
    val originalSeq = originalRow.toSeq
    val newSeq = new ListBuffer[Any]
    newSeq.appendAll(originalSeq)
    for (_ <- originalSeq.length until updateSchema.length) {
      newSeq.append("null")
    }
    Row.fromSeq(newSeq)
  }

  def logging(text: String): Unit = {
    println(text)
    log.info(text)
  }

  def randomArray[T: ClassTag](array: Array[T]): Array[T] = {
    if (array.length == 0)
      return array
    val arr2 = new Array[T](array.length)
    val count = array.length
    var cbRandCount = 0
    // index
    var cbPosition = 0
    // position
    var k = 0
    do {
      val rand = new Random()
      val r = count - cbRandCount
      cbPosition = rand.nextInt(r)
      arr2(k) = array(cbPosition)
      k = k + 1
      cbRandCount = cbRandCount + 1

      // assign last member to cbPosition
      array(cbPosition) = array(r - 1)
    } while (cbRandCount < count)
    arr2
  }

  private def initZooKeeper(conf: Configuration): ZooKeeper = {
    new ZooKeeper(conf.get("ha.zookeeper.quorum"), 30000, null)
  }

  /**
    * scan hdfs to find a table which needs merge
    *
    * @return pair, left is table path, right is list of incremental files to be merged.
    */
  private def scan(zooKeeperClient: ZooKeeper, fileSystem: FileSystem, skipDBList: Seq[String], skipTblList: Seq[String]): DatalakeHDFSNode = {
    logging(">>>>>>> SCAN TABLE <<<<<<<")
    val tenantList = fileSystem.listStatus(new Path(hdfsRoot))
    var hdfsnode: DatalakeHDFSNode = null
    breakable {
      for (tenantFS <- randomArray(tenantList)) {
        val tenantName = tenantFS.getPath.getName
        logging(s">> scan tenantFS.getPath.getName:$tenantName")
        val tenantAbsolutePath = hdfsRoot.concat("/").concat(tenantName)
        val instanceList = fileSystem.listStatus(tenantFS.getPath)
        for (instanceFS <- randomArray(instanceList)) {
          val instanceName = instanceFS.getPath.getName
          logging(s">>   scan instanceFS.getPath.getName:$instanceName")
          val instanceAbsolutePath = tenantAbsolutePath.concat("/").concat(instanceName)
          val schemaList = fileSystem.listStatus(instanceFS.getPath)
          for (schemaFS <- randomArray(schemaList)) {
            val schemaName = schemaFS.getPath.getName
            logging(s">>     scan schemaFS.getPath.getName:$schemaName")
            val schemaAbsolutePath = instanceAbsolutePath.concat("/").concat(schemaName)
            if (skipDBList.contains(schemaAbsolutePath)) {
              logging(">>     In SkipList, so skip this schema.")
            } else {
              val tableList = fileSystem.listStatus(schemaFS.getPath)
              for (tableFS <- randomArray(tableList)) {
                val tableName = tableFS.getPath.getName
                logging(s">>       scan tableFS.getPath.getName:$tableName")
                val tableAbsolutePath = schemaAbsolutePath.concat("/").concat(tableName)
                if (skipTblList.contains(tableAbsolutePath)) {
                  logging(">>       In SkipList, so skip this table.")
                } else {
                  /** judge last successful merge time **/
                  val lastTimeStr = new String(
                    ZooKeeperUtil.getNodeData(zooKeeperClient, ZooKeeperUtil.convertDatalakeHDFSPathToZKLastMergePath(tableAbsolutePath)))
                  if (!lastTimeStr.equals("")) {
                    val lastTimeLong = java.lang.Long.valueOf(lastTimeStr)
                    if (System.currentTimeMillis() - lastTimeLong > 1000 * 60 * 15) {
                      logging(">> scan tableAbsolutePath path:" + tableAbsolutePath)
                      hdfsnode = judgeOneTable(zooKeeperClient, fileSystem, tableAbsolutePath, tenantName, instanceName, schemaName, tableName)
                      if (hdfsnode != null)
                        break
                    } else
                      logging(s">> Because it's less than 10 minutes from last successful merge, so skip this table:$tableName")
                  } else {
                    hdfsnode = judgeOneTable(zooKeeperClient, fileSystem, tableAbsolutePath, tenantName, instanceName, schemaName, tableName)
                    if (hdfsnode != null)
                      break
                  }
                }
              }
            }
          }
        }
      }
    }
    hdfsnode
  }

  private def judgeOneTable(zooKeeperClient: ZooKeeper, fileSystem: FileSystem, tableAbsolutePath: String,
                            tenantName: String, instanceName: String, schemaName: String, tableName: String): DatalakeHDFSNode = {
    try {
      val zkMetaPath = ZooKeeperUtil.convertDatalakeHDFSPathToZKMetaPath(tableAbsolutePath)
      logging(">> meta path:" + zkMetaPath)
      val metaJson = ZooKeeperUtil.getNodeData(zooKeeperClient, zkMetaPath)
      val meta = MetaUtil.getMetaInstance(metaJson)
      // NULL META
      if (meta != null)
        if (meta.META_FLAG != null)
        // META_FLAG==3 means full_dump complete and table is useful
          if (meta.META_FLAG.equals("3")) {
            sourcePK = getSourcePKArray(meta.SOURCE_PK)
            val prefix = meta.PREFIX
            val preFixAbsolutePath = tableAbsolutePath.concat("/").concat(prefix)
            /** judge useful incremental file size > 1 **/
            val incrementalPath = new Path(preFixAbsolutePath + "/incremental")
            if (fileSystem.exists(incrementalPath)) {
              /** judge zkLock **/
              if (!ZooKeeperUtil.checkLockNode(
                zooKeeperClient, ZooKeeperUtil.convertDatalakeHDFSPathToZKLockPath(tableAbsolutePath))) {
                // get newest timestamp
                val timestampList = fileSystem.listStatus(new Path(preFixAbsolutePath))
                if (timestampList.nonEmpty) {
                  var newestTimestamp: FileStatus = null
                  breakable {
                    for (i <- (0 until timestampList.length).reverse) {
                      val ts = timestampList(i)
                      if (!ts.getPath.getName.contains("temp")) {
                        newestTimestamp = ts
                        break
                      }
                    }
                  }
                  val incrementalFiles = fileSystem.listStatus(incrementalPath)
                  logging(s"Check table: $tableAbsolutePath")
                  val filterFiles = incrementalFiles.filter(checkTimestampByFS(newestTimestamp.getPath.getName, _))
                  if (filterFiles.length > 1 && checkFiles(filterFiles) && checkLastLen(filterFiles)) {
                    logging(s">>>>> Table $tableAbsolutePath should merge and ready to preempt lock")
                    val newestTableAbsolutePath = preFixAbsolutePath.concat("/").concat(newestTimestamp.getPath.getName)
                    val newestIncrementalFileName = incrementalFiles(incrementalFiles.length - 1).getPath.getName
                    val zkLockPath = ZooKeeperUtil.convertDatalakeHDFSPathToZKLockPath(tableAbsolutePath)
                    // create zk lock
                    logging(">>>zk lock path:" + zkLockPath)
                    ZooKeeperUtil.createSeqNode(zooKeeperClient, zkLockPath, uuid)
                    // check zk lock to realize fully preemptible
                    val children = ZooKeeperUtil.getChildren(
                      zooKeeperClient, zkLockPath
                    )
                    if (!children.isEmpty)
                      if (ZooKeeperUtil.getNodeData(zooKeeperClient, zkLockPath.concat("/").concat(children.get(0)))
                        .equals(uuid)) {
                        logging(s">> Table $tableAbsolutePath preempted successful! ready to merge...")
                        return DatalakeHDFSNode(newestTableAbsolutePath,
                          tenantName, instanceName, schemaName,
                          tableName, prefix, meta, newestTimestamp.getPath.getName, newestIncrementalFileName)
                      } else {
                        logging(s">> Table $tableAbsolutePath has been preempted by other merge process and continue to scan")
                        return null
                      }
                  }
                }
              }
              else {
                val children = ZooKeeperUtil.getChildren(
                  zooKeeperClient, ZooKeeperUtil.convertDatalakeHDFSPathToZKLockPath(tableAbsolutePath)
                )
                logging(s">> Table $tableAbsolutePath has locked by other process, and process seqNode is:${children.get(0)} ")
                return null
              }
            }
          }
      null
    }
    catch {
      case _: NoNodeException =>
        log.error("No ZK Meta Node")
        null
    }
  }


}
