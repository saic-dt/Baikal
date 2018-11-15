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

package org.datatech.baikal.partitioner

import org.apache.spark.Partitioner
import org.slf4j.LoggerFactory

class DatalakePartitioner(numParts: Int) extends Partitioner {


  val logger = LoggerFactory.getLogger("DataFrameOperations")

  override def numPartitions: Int = numParts

  // return the count of partitions
  //logger.info(">>>>>> the count of RDD partitions is:" + numParts)


  override def getPartition(key: Any): Int = {
    //return the order of partition and it must be at the range of 0 ~ numParts-1

    //println(">>new row in get partition:"+newRow.toString())


    val newRowkey = key.asInstanceOf[String]
    //logger.info(">>>>>> key/rowkey are respectively:" + key+"/"+newRowkey)

    //val firstChar = newRowkey.charAt(0)
    val first2TwoChar = newRowkey.substring(0, 2)
    //logger.info(">>>>>> firstChar is:" + firstChar)
    val partitionOrder = first2TwoChar match {
      case "00" => 0
      case "01" => 1
      case "02" => 2
      case "03" => 3
      case "04" => 4
      case "05" => 5
      case "06" => 6
      case "07" => 7
      case "08" => 8
      case "09" => 9
      case "10" => 10
      case "11" => 11
      case "12" => 12
      case "13" => 13
      case "14" => 14
      case "15" => 15
      case "16" => 16
      case "17" => 17
      case "18" => 18
      case "19" => 19
      case "20" => 20
      case "21" => 21
      case "22" => 22
      case "23" => 23
      case "24" => 24
      case "25" => 25
      case "26" => 26
      case "27" => 27
      case "28" => 28
      case "29" => 29
      case "30" => 30
      case "31" => 31
      case "32" => 32
      case "33" => 33
      case "34" => 34
      case "35" => 35
      case "36" => 36
      case "37" => 37
      case "38" => 38
      case "39" => 39
      case "40" => 40
      case "41" => 41
      case "42" => 42
      case "43" => 43
      case "44" => 44
      case "45" => 45
      case "46" => 46
      case "47" => 47
      case "48" => 48
      case "49" => 49
      case "50" => 50
      case "51" => 51
      case "52" => 52
      case "53" => 53
      case "54" => 54
      case "55" => 55
      case "56" => 56
      case "57" => 57
      case "58" => 58
      case "59" => 59
      case "60" => 60
      case "61" => 61
      case "62" => 62
      case "63" => 63

    }

    //logger.info(">>>>>> partitionOrder is:" + partitionOrder)
    partitionOrder

  }

  override def hashCode(): Int = super.hashCode()

  override def equals(obj: scala.Any): Boolean = super.equals(obj)


}
