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

import java.sql.{Connection, DriverManager, SQLException}

import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * Used to get the table's row count and primary keys, and to determine begin-end boundaries for each thread in parallel spark jdbc
  **/

class CheckPrimaryKeys(dbType: String, schemaName: String, tableName: String, jdbcDriver: String, jdbcUrl: String, userName: String, password: String) {

  val logger = LoggerFactory.getLogger(classOf[CheckPrimaryKeys])
  val connection = getConnection(jdbcDriver, jdbcUrl, userName, password)
  val stat = connection.createStatement

  def getColumnTypes: Tuple2[ArrayBuffer[Tuple4[Int, Int, Int, String]], Long] = {

    val colTypeList: ArrayBuffer[Tuple4[Int, Int, Int, String]] = new ArrayBuffer[(Int, Int, Int, String)]()
    var rowCount: Long = 0
    // reserves column meta info, Tuple._1: columnType,Tuple._2: precision, Tuple._3: scale, used for fixing data consistency
    // get column meta info--used for fixing data consistency
    var res = stat.executeQuery(String.format("SELECT * FROM %s.%s WHERE 1=0", schemaName, tableName))
    val tableMetaData = res.getMetaData
    for (i <- 1 to tableMetaData.getColumnCount) {
      val columnType = tableMetaData.getColumnType(i)
      val columnTypeName = tableMetaData.getColumnTypeName(i)
      val precision = tableMetaData.getPrecision(i)
      val scale = tableMetaData.getScale(i)
      colTypeList.append((columnType, precision, scale, columnTypeName))
    }
    res = stat.executeQuery(String.format("SELECT COUNT(*) FROM %s.%s", schemaName, tableName))
    while (res.next()) {
      rowCount = res.getLong(1)
    }

    (colTypeList, rowCount)

  }


  def getPrimaryKeys: ArrayBuffer[String] = {

    // determine if PKs exist or not in the current table
    val keyList = new ArrayBuffer[String]()
    var res = stat.executeQuery(String.format("SELECT * FROM %s.%s WHERE 1=0", schemaName, tableName))
    val metaData = connection.getMetaData
    if ((dbType == "ORACLE") || (dbType == "DB2")) {
      logger.info(">>>>>> this table belongs to database:  ORACLE or DB2.")
      res = metaData.getPrimaryKeys(null, schemaName.toUpperCase, tableName.toUpperCase)
    }
    else if (dbType == "MYSQL") {
      res = metaData.getPrimaryKeys("", schemaName, tableName)
      logger.info(">>>>>> this table belongs to database:  MYSQL.")
    }
    else {
      res = metaData.getPrimaryKeys(null, schemaName, tableName)
      logger.info(">>>>>> this table belongs to database: SQL SERVER.")
    }
    while (res.next) {
      val pkey = res.getString("COLUMN_NAME")
      if (pkey != null) {
        keyList.append(pkey)
      }
    }
    if (keyList.nonEmpty) {
      logger.info(">>>>>> primarykeys is: " + keyList.addString(new StringBuilder, "|"))
    }
    keyList

  }


  def getPksBoundList(rowCount: Long, intervalLen: Long, keys: ArrayBuffer[String]): Tuple2[ArrayBuffer[String], String] = {

    // if pk exists and rowCount is big enough, figure out the pks
    val pkBoundList = ArrayBuffer[String]()
    var keyList = new ArrayBuffer[String]()
    var pks: String = ""
    val schema_table = schemaName.concat(".").concat(tableName)
    var res = stat.executeQuery(String.format("SELECT * FROM %s.%s WHERE 1=0", schemaName, tableName))

    // query PKs start-end values for each sparkJdbc thread
    if (dbType == "ORACLE") {
      keyList = keys.map(element => String.format("CAST(%s AS VARCHAR(100))", element))
      import scala.collection.JavaConversions._
      pks = String.join(" ||'-'|| ", keyList)
      logger.info(">>>>>> pks is: " + pks)
      val ts2 = System.currentTimeMillis()
      res = stat.executeQuery(
        s"select a.rowkey from(select row_number() over (order by ${pks}) as rn, ${pks} as rowkey from ${schema_table}) a where a.rn=1 or a.rn=${rowCount} or mod(a.rn, ${intervalLen})=0")
      val ts3 = System.currentTimeMillis()
      while (res.next()) {
        val newPk = res.getString(1)
        pkBoundList.append(newPk)
      }

    }

    else if (dbType == "DB2") {
      import scala.collection.JavaConversions._
      keyList = keys.map(element => String.format("CAST(%s AS VARCHAR(100))", element))
      pks = String.join(" ||'-'|| ", keyList)
      logger.info(">>>>>> pks is: " + pks)
      res = stat.executeQuery(
        s"select ${pks} as rowkey from ${schema_table} order by rowkey limit 1")
      while (res.next()) {
        val newPk = res.getString(1)
        pkBoundList.append(newPk)
      }
      val range = new ArrayBuffer[Long]()
      for (i <- intervalLen to rowCount - 1) {
        if (i % intervalLen == 0) {
          range.append(i)
        }
      }

      import scala.util.control.Breaks._
      breakable {
        for (i <- range) {
          if (i == rowCount - 1) {
            break()
          }
          res = stat.executeQuery(
            s"select a.rowkey from(select ${pks} as rowkey from ${schema_table} order by rowkey) a limit ${i},1")
          while (res.next()) {
            val newPk = res.getString(1)
            pkBoundList.append(newPk)
          }
        }
      }
      res = stat.executeQuery(
        s"select ${pks} as rowkey from ${schema_table} order by rowkey limit ${rowCount - 1},1")
      while (res.next()) {
        val newPk = res.getString(1)
        pkBoundList.append(newPk)
      }
    }

    else if (dbType == "MYSQL") {
      keyList = keys.map(element => String.format("CAST(%s AS CHAR(100))", element))
      if (keyList.length == 1) {
        pks = keyList.apply(0)
      }
      if (keyList.length > 1) {
        // pk fields concat：concat_ws('-','field1','field2')
        import scala.collection.JavaConversions._
        pks = "concat_ws('-',".concat(String.join(",", keyList)).concat(")")
      }
      logger.info(">>>>>> pks is: " + pks)
      res = stat.executeQuery(
        s"select ${pks} as rowkey from ${schema_table} order by rowkey limit 1")
      while (res.next()) {
        var newPk = res.getString(1)
        pkBoundList.append(newPk)
      }
      val range = new ArrayBuffer[Long]()
      for (i <- intervalLen to rowCount - 1) {
        if (i % intervalLen == 0) {
          range.append(i)
        }
      }


      import scala.util.control.Breaks._
      breakable {
        for (i <- range) {
          if (i == rowCount - 1) {
            break()
          }
          res = stat.executeQuery(
            s"select a.rowkey from(select ${pks} as rowkey from ${schema_table} order by rowkey) a limit ${i},1")
          while (res.next()) {
            val newPk = res.getString(1)
            pkBoundList.append(newPk)
          }
        }
      }
      res = stat.executeQuery(
        s"select ${pks} as rowkey from ${schema_table} order by rowkey limit ${rowCount - 1},1")
      while (res.next()) {
        val newPk = res.getString(1)
        pkBoundList.append(newPk)
      }
    }

    else if (dbType == "MSSQL") {
      keyList = keys.map(key => String.format("CAST(%s AS VARCHAR(100))", key))
      import scala.collection.JavaConversions._
      pks = String.join(" +'-'+ ", keyList)
      logger.info(">>>>>> pks is: " + pks)
      res = stat.executeQuery(
        s"select ${pks} from (select *,row_number() over (order by ${pks}) as row from ${schema_table}) a where a.row=1 or a.row=${rowCount} or a.row%${intervalLen}=0")
      while (res.next()) {
        var newPk = res.getString(1)
        pkBoundList.append(newPk)
      }
    }

    (pkBoundList, pks)

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
