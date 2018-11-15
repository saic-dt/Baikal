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

package org.apache.spark.sql.execution.datasources.sparquet

import java.text.SimpleDateFormat

import com.google.gson.JsonParser
import org.apache.hadoop.fs.Path
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.execution.datasources.common.util.{CommonUtils, DatalakeProperties}
import org.apache.spark.sql.execution.datasources.sparquet.util.Utils
import org.apache.spark.sql.execution.datasources.{DatalakeTableModelS, Logging}
import org.apache.spark.sql.sources.{BaseRelation, Filter, PrunedFilteredScan}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Column, Row, SQLContext}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class SParquetRelation(
                             parameters: Map[String, String],
                             userSpecifiedschema: Option[StructType]
                           )(val sqlContext: SQLContext)
  extends BaseRelation with PrunedFilteredScan with Logging {


  val datalakeProperties = DatalakeProperties.getInstance()
  val rootPath = datalakeProperties.getDefaultHDFSRootPath
  val dtm = DatalakeTableModelS(parameters("prefix"),
    getSourcePKArray(parameters("sourcepk")),
    datalakeProperties.getDateFormat,
    datalakeProperties.getTimestampFormat,
    parameters("tenant"),
    parameters("instance"),
    parameters("schema"),
    parameters("table")
  )
  val standardRootPath = if (rootPath.endsWith("/")) {
    rootPath.substring(0, rootPath.length - 1)
  } else {
    rootPath
  }
  @transient val conf = sqlContext.sparkContext.hadoopConfiguration
  @transient val tableHDFSPath = getTableHDFSPath()
  @transient val fs = tableHDFSPath.getFileSystem(conf)
  val tableMetaZKPath = s"/datalake/metastore/${dtm.tenantName}/meta/${dtm.oiName}/${dtm.osName}/${dtm.otName}"
  val userName = sqlContext.sparkContext.sparkUser
  var incrementalFiles: Array[String] = null

  override def buildScan(requiredColumns: Array[String], filters: Array[Filter]): RDD[Row] = {
    val lastUpdatePath = getLastUpdatePath(tableHDFSPath)
    incrementalFiles = getIncrementalFilePaths(lastUpdatePath)
    val dateFormat = new SimpleDateFormat(dtm.dateFormat)
    val timestampFormat = new SimpleDateFormat(dtm.timestampFormat)
    val finalSchema = if (dtm.sourcePK.size == 0) {
      schema
    } else {
      StructType(schema.filter(f => requiredColumns.contains(f.name) || dtm.sourcePK.contains(f.name)))
    }
    val reader = new IncrementalFileReader(this, finalSchema)
    reader.readIncrementalData()
    val cdb = sqlContext.sparkContext.broadcast(reader.getIncData())
    val dkb = sqlContext.sparkContext.broadcast(reader.getDeletedKeys())

    val insertRdd = sqlContext.sparkContext.parallelize(reader.getInsertRows()).mapPartitions { rows =>
      rows.map(CommonUtils.convert2SparkDataType(_, finalSchema, dateFormat, timestampFormat))
    }

    val df = sqlContext.read.parquet(lastUpdatePath)
    val dfSchema = df.schema
    val dfFinalSchema = if (dtm.sourcePK.size == 0) {
      dfSchema
    } else {
      StructType(dfSchema.filter(f => requiredColumns.contains(f.name) || dtm.sourcePK.contains(f.name)))
    }
    val selectedDf = df.select(dfFinalSchema.fieldNames.map(Column(_)): _*)

    val rdd = selectedDf.rdd.mapPartitions { rows =>
      val jp = new JsonParser
      val pkIndices = dtm.sourcePK.map(dfFinalSchema.fieldNames.indexOf(_))
      val changeData = cdb.value
      val deletedKeys = dkb.value

      def checkDelete(row: Row, deletedKeys: mutable.HashMap[String, String], pkIndices: Array[Int]): Boolean = {
        val key = generateKey(dtm.sourcePK, pkIndices, row)
        if (deletedKeys.get(key) == None) {
          true
        } else {
          false
        }
      }

      // scan the origin parquet file and merge the origin row & row from inc data
      def merge(row: Row, changeData: mutable.HashMap[String, String], pkIndices: Array[Int]): Row = {
        val key = generateKey(dtm.sourcePK, pkIndices, row)
        val targetValue = changeData.get(key)
        // origin row not exist in inc data
        if (targetValue == None) {
          row
        } else {
          //origin row is 'u' or 'uk' in inc data
          val jsonOb = jp.parse(targetValue.get).getAsJsonObject
          val incrow = Utils.convertJson2Row(jsonOb, finalSchema)
          val decryptedRow = CommonUtils.decryptRow(incrow)
          val arr = new ArrayBuffer[Any]()
          for (i <- 0 until decryptedRow.size) {
            if (decryptedRow(i) == null) {
              arr += row(i)
            } else {
              arr += decryptedRow(i)
            }
          }
          Row.fromSeq(arr)
        }
      }

      def convertRowSchema(row: Row, oldSchema: StructType, newSchema: StructType): Row = {
        val arr = new ArrayBuffer[Any]()
        newSchema.fieldNames.foreach { fName =>
          val idx = try {
            oldSchema.fieldIndex(fName)
          } catch {
            case e: IllegalArgumentException => null
          }
          val value = if (idx == null) {
            null
          } else {
            row(idx.asInstanceOf[Int])
          }
          arr += value
        }
        Row.fromSeq(arr)
      }

      def generateKey(sourcepk: Array[String], pkIndices: Array[Int], row: Row): String = {
        if (sourcepk.size == 0) {
          row.mkString("|")
        } else if (sourcepk.size == 1) {
          row(pkIndices(0)).toString
        } else {
          val arr = new ArrayBuffer[String]()
          pkIndices.foreach { index =>
            arr += row(index).toString
          }
          arr.mkString("|")
        }
      }

      new Iterator[Row] {
        var current_row: Row = null

        override def hasNext(): Boolean = {
          if (rows.hasNext) {
            current_row = CommonUtils.decryptRow(convertRowSchema(rows.next(), dfFinalSchema, finalSchema))
            if (!deletedKeys.isEmpty) {
              if (checkDelete(current_row, deletedKeys, pkIndices)) {
                if (!changeData.isEmpty) {
                  current_row = merge(current_row, changeData, pkIndices)
                }
                true
              } else {
                hasNext()
              }
            } else {
              if (!changeData.isEmpty) {
                current_row = merge(current_row, changeData, pkIndices)
              }
              true
            }
          } else {
            false
          }
        }

        override def next(): Row = {
          CommonUtils.convert2SparkDataType(current_row, finalSchema, dateFormat, timestampFormat)
        }
      }
    }

    val rdd1 = if (reader.getInsertRows().isEmpty) {
      rdd
    } else {
      rdd ++ insertRdd
    }
    sqlContext.createDataFrame(rdd1, finalSchema).select(requiredColumns.map(Column(_)): _*).rdd

  }

  override def schema: StructType = userSpecifiedschema.get

  /**
    * get the latest updated file paths
    * cause Datalake will keep the history version data file in a short period of time
    *
    * @param tableHDFSPath
    * @return
    */
  private def getLastUpdatePath(tableHDFSPath: Path): String = {
    val dirs = fs.listStatus(tableHDFSPath)
    var lastModifieTime = 0L
    var lastUpdatePath = ""
    dirs.foreach { dir =>
      val path = dir.getPath.toString
      if (!path.endsWith("incremental") && !path.endsWith("temp")) {
        val time = dir.getModificationTime
        if (lastModifieTime < time) {
          lastModifieTime = time
          lastUpdatePath = dir.getPath.toString
        }
      }
    }
    lastUpdatePath
  }

  /**
    * Incremental File timestamp should be larger than parquet timestamp
    *
    * @param lastUpdatePath the latest parquet file path
    * @return
    */
  private def getIncrementalFilePaths(lastUpdatePath: String): Array[String] = {
    val path = getIncreHDFSPath()
    val baseTime = lastUpdatePath.substring(lastUpdatePath.size - 13, lastUpdatePath.size).toLong
    if (fs.exists(path)) {
      val dirs = fs.listStatus(path)
      dirs.map(_.getPath.toString).filter(x => x.substring(x.size - 13, x.size).toLong >= baseTime)
    } else {
      Array[String]()
    }
  }

  private def getIncreHDFSPath(): Path = {
    new Path(tableHDFSPath.toString + "/incremental")
  }

  private def getTableHDFSPath(): Path = {
    new Path(s"${standardRootPath}/${dtm.tenantName}/${dtm.oiName}/${dtm.osName}/${dtm.otName}/${dtm.prefix}")
  }

  private def getSourcePKArray(sourcepk: String): Array[String] = {
    if (sourcepk == "") {
      Array[String]()
    } else {
      sourcepk.split(",").map(_.trim)
    }
  }

}
