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

package org.datatech.baikal.fulldump.partitioner

import org.apache.spark.Partitioner
import org.slf4j.LoggerFactory

/**
  * This class is used to realize the customizable partitioning of rdd based on the first three characters of the rowkey
  **/
class DtPartitioner(numParts: Int) extends Partitioner {


  val logger = LoggerFactory.getLogger("DataFrameOperations")

  override def numPartitions: Int = numParts


  override def getPartition(key: Any): Int = {
    //return the order of partition and it must be at the range of 0 ~ numParts-1

    val newRowkey = key.asInstanceOf[String]
    val first2TwoChar = newRowkey.substring(0, 3)
    val partitionOrder = first2TwoChar match {
      case "000" => 0
      case "001" => 1
      case "002" => 2
      case "003" => 3
      case "004" => 4
      case "005" => 5
      case "006" => 6
      case "007" => 7
      case "008" => 8
      case "009" => 9
      case "010" => 10
      case "011" => 11
      case "012" => 12
      case "013" => 13
      case "014" => 14
      case "015" => 15
      case "016" => 16
      case "017" => 17
      case "018" => 18
      case "019" => 19
      case "020" => 20
      case "021" => 21
      case "022" => 22
      case "023" => 23
      case "024" => 24
      case "025" => 25
      case "026" => 26
      case "027" => 27
      case "028" => 28
      case "029" => 29
      case "030" => 30
      case "031" => 31
      case "032" => 32
      case "033" => 33
      case "034" => 34
      case "035" => 35
      case "036" => 36
      case "037" => 37
      case "038" => 38
      case "039" => 39
      case "040" => 40
      case "041" => 41
      case "042" => 42
      case "043" => 43
      case "044" => 44
      case "045" => 45
      case "046" => 46
      case "047" => 47
      case "048" => 48
      case "049" => 49
      case "050" => 50
      case "051" => 51
      case "052" => 52
      case "053" => 53
      case "054" => 54
      case "055" => 55
      case "056" => 56
      case "057" => 57
      case "058" => 58
      case "059" => 59
      case "060" => 60
      case "061" => 61
      case "062" => 62
      case "063" => 63
      case "064" => 64
      case "065" => 65
      case "066" => 66
      case "067" => 67
      case "068" => 68
      case "069" => 69
      case "070" => 70
      case "071" => 71
      case "072" => 72
      case "073" => 73
      case "074" => 74
      case "075" => 75
      case "076" => 76
      case "077" => 77
      case "078" => 78
      case "079" => 79
      case "080" => 80
      case "081" => 81
      case "082" => 82
      case "083" => 83
      case "084" => 84
      case "085" => 85
      case "086" => 86
      case "087" => 87
      case "088" => 88
      case "089" => 89
      case "090" => 90
      case "091" => 91
      case "092" => 92
      case "093" => 93
      case "094" => 94
      case "095" => 95
      case "096" => 96
      case "097" => 97
      case "098" => 98
      case "099" => 99
      case "100" => 100
      case "101" => 101
      case "102" => 102
      case "103" => 103
      case "104" => 104
      case "105" => 105
      case "106" => 106
      case "107" => 107
      case "108" => 108
      case "109" => 109
      case "110" => 110
      case "111" => 111
      case "112" => 112
      case "113" => 113
      case "114" => 114
      case "115" => 115
      case "116" => 116
      case "117" => 117
      case "118" => 118
      case "119" => 119
      case "120" => 120
      case "121" => 121
      case "122" => 122
      case "123" => 123
      case "124" => 124
      case "125" => 125
      case "126" => 126
      case "127" => 127
    }

    partitionOrder

  }

  override def hashCode(): Int = super.hashCode()

  override def equals(obj: scala.Any): Boolean = super.equals(obj)


}
