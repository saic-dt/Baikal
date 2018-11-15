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

package org.datatech.baikal.reader

import org.apache.spark.sql.SparkSession

class TestIncrementalRDDFileReaderSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  var spark: SparkSession = _

  override protected def beforeAll(): Unit = {
    spark = SparkSession.builder().master("local").config("num-executors", 4).config("executor-memory", "1g").getOrCreate()
  }

  //  test("readEasyData") {
  //    val fileList = "/Users/chenyijie/Documents/a.txt,/Users/chenyijie/Documents/b.txt"
  //    val schema = StructType(Seq(StructField("id", IntegerType, nullable = true)))
  //    val keydb = "password".getBytes("UTF-8")
  //    val broadcastKeydb = spark.sparkContext.broadcast(keydb)
  //    val sourcePK = Array("id")
  //    val updateData = new mutable.HashMap[String, String]()
  //    val deletedData = new mutable.HashMap[String, String]()
  //    val insertData = new ArrayBuffer[Row]()
  //    IncrementalRDDFileReader.readIncrementalData(spark.sparkContext, fileList, schema,
  //      updateData, deletedData, insertData, broadcastKeydb, sourcePK, "test1")
  //    println(">> insertData:" + insertData)
  //  }

  test("rddCollect") {
    val fileList = "/Users/chenyijie/a.txt"
    val rdd = spark.sparkContext.textFile(fileList, 64)
    rdd.saveAsTextFile("/Users/chenyijie/collect.dat")
  }

  //  test("readComplicatedData") {
  //    val fileList = "/Users/chenyijie/Documents/incremental/ts1532929280808," +
  //      "/Users/chenyijie/Documents/incremental/ts1532929682030," +
  //      "/Users/chenyijie/Documents/incremental/ts1532931974895," +
  //      "/Users/chenyijie/Documents/incremental/ts1532932361023"
  //    val schema = StructType(
  //      Seq(
  //        StructField("ID1", IntegerType, nullable = true),
  //        StructField("ID2", IntegerType, nullable = true),
  //        StructField("ID3", IntegerType, nullable = true),
  //        StructField("NAME1", StringType, nullable = true),
  //        StructField("NAME2", StringType, nullable = true)))
  //    val keydb = "password".getBytes("UTF-8")
  //    val broadcastKeydb = spark.sparkContext.broadcast(keydb)
  //    val sourcePK = Array("ID1", "ID2", "ID3")
  //    val updateData = new mutable.HashMap[String, String]()
  //    val deletedData = new mutable.HashMap[String, String]()
  //    val insertData = new ArrayBuffer[Row]()
  //    IncrementalRDDFileReader.readIncrementalData(spark.sparkContext, fileList, schema,
  //      updateData, deletedData, insertData, broadcastKeydb, sourcePK, "test2")
  //    println(">> insertData size:" + insertData.size)
  //  }


}
