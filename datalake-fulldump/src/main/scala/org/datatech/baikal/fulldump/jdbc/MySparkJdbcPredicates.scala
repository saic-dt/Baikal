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

import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

class MySparkJdbcPredicates {

}


/**
  * This class is used to determine the pks column range for each SparkJdbc thread,
  * customized the Parallelism numbers include 4/8/16/32/64
  */

object MySparkJdbcPredicates {

  val logger = LoggerFactory.getLogger(classOf[MySparkJdbcPredicates])

  def getPredicatesArray(pkBoundList: Array[String], pks: String, parallelismNum: Int): Array[String] = {

    var predicates = new ArrayBuffer[String]()
    var realParallelismNum = parallelismNum

    // parallelism number must be an even digit
    if (parallelismNum % 2 != 0 || parallelismNum < 4 || parallelismNum > 64) {
      realParallelismNum = 16
      logger.error(">>>>>> parallelismNum must be an even digit which at range of [4,64]")
      logger.info(">>>>>> reset parallelismNum as 16")
    }

    if (realParallelismNum == 4) {
      predicates =
        ArrayBuffer(
          pkBoundList.apply(0) -> pkBoundList.apply(1),
          pkBoundList.apply(1) -> pkBoundList.apply(2),
          pkBoundList.apply(2) -> pkBoundList.apply(3),
          pkBoundList.apply(3) -> pkBoundList.apply(pkBoundList.length - 1)
        ).map {
          case (start, end) =>
            s"${pks} >= '$start'" + s" AND ${pks} < '$end'"
        }
    }


    if (realParallelismNum == 8) {
      predicates =
        ArrayBuffer(
          pkBoundList.apply(0) -> pkBoundList.apply(1),
          pkBoundList.apply(1) -> pkBoundList.apply(2),
          pkBoundList.apply(2) -> pkBoundList.apply(3),
          pkBoundList.apply(3) -> pkBoundList.apply(4),
          pkBoundList.apply(4) -> pkBoundList.apply(5),
          pkBoundList.apply(5) -> pkBoundList.apply(6),
          pkBoundList.apply(6) -> pkBoundList.apply(7),
          pkBoundList.apply(7) -> pkBoundList.apply(pkBoundList.length - 1)
        ).map {
          case (start, end) =>
            s"${pks} >= '$start'" + s" AND ${pks} < '$end'"
        }
    }


    if (realParallelismNum == 16) {
      predicates =
        ArrayBuffer(
          pkBoundList.apply(0) -> pkBoundList.apply(1),
          pkBoundList.apply(1) -> pkBoundList.apply(2),
          pkBoundList.apply(2) -> pkBoundList.apply(3),
          pkBoundList.apply(3) -> pkBoundList.apply(4),
          pkBoundList.apply(4) -> pkBoundList.apply(5),
          pkBoundList.apply(5) -> pkBoundList.apply(6),
          pkBoundList.apply(6) -> pkBoundList.apply(7),
          pkBoundList.apply(7) -> pkBoundList.apply(8),
          pkBoundList.apply(8) -> pkBoundList.apply(9),
          pkBoundList.apply(9) -> pkBoundList.apply(10),
          pkBoundList.apply(10) -> pkBoundList.apply(11),
          pkBoundList.apply(11) -> pkBoundList.apply(12),
          pkBoundList.apply(12) -> pkBoundList.apply(13),
          pkBoundList.apply(13) -> pkBoundList.apply(14),
          pkBoundList.apply(14) -> pkBoundList.apply(15),
          pkBoundList.apply(15) -> pkBoundList.apply(pkBoundList.length - 1)
        ).map {
          case (start, end) =>
            s"${pks} >= '$start'" + s" AND ${pks} < '$end'"
        }
    }


    if (realParallelismNum == 32) {
      predicates =
        ArrayBuffer(
          pkBoundList.apply(0) -> pkBoundList.apply(1),
          pkBoundList.apply(1) -> pkBoundList.apply(2),
          pkBoundList.apply(2) -> pkBoundList.apply(3),
          pkBoundList.apply(3) -> pkBoundList.apply(4),
          pkBoundList.apply(4) -> pkBoundList.apply(5),
          pkBoundList.apply(5) -> pkBoundList.apply(6),
          pkBoundList.apply(6) -> pkBoundList.apply(7),
          pkBoundList.apply(7) -> pkBoundList.apply(8),
          pkBoundList.apply(8) -> pkBoundList.apply(9),
          pkBoundList.apply(9) -> pkBoundList.apply(10),
          pkBoundList.apply(10) -> pkBoundList.apply(11),
          pkBoundList.apply(11) -> pkBoundList.apply(12),
          pkBoundList.apply(12) -> pkBoundList.apply(13),
          pkBoundList.apply(13) -> pkBoundList.apply(14),
          pkBoundList.apply(14) -> pkBoundList.apply(15),
          pkBoundList.apply(15) -> pkBoundList.apply(16),
          pkBoundList.apply(16) -> pkBoundList.apply(17),
          pkBoundList.apply(17) -> pkBoundList.apply(18),
          pkBoundList.apply(18) -> pkBoundList.apply(19),
          pkBoundList.apply(19) -> pkBoundList.apply(20),
          pkBoundList.apply(20) -> pkBoundList.apply(21),
          pkBoundList.apply(21) -> pkBoundList.apply(22),
          pkBoundList.apply(22) -> pkBoundList.apply(23),
          pkBoundList.apply(23) -> pkBoundList.apply(24),
          pkBoundList.apply(24) -> pkBoundList.apply(25),
          pkBoundList.apply(25) -> pkBoundList.apply(26),
          pkBoundList.apply(26) -> pkBoundList.apply(27),
          pkBoundList.apply(27) -> pkBoundList.apply(28),
          pkBoundList.apply(28) -> pkBoundList.apply(29),
          pkBoundList.apply(29) -> pkBoundList.apply(30),
          pkBoundList.apply(30) -> pkBoundList.apply(31),
          pkBoundList.apply(31) -> pkBoundList.apply(pkBoundList.length - 1)
        ).map {
          case (start, end) =>
            s"${pks} >= '$start'" + s" AND ${pks} < '$end'"
        }
    }


    if (realParallelismNum == 64) {
      predicates =
        ArrayBuffer(
          pkBoundList.apply(0) -> pkBoundList.apply(1),
          pkBoundList.apply(1) -> pkBoundList.apply(2),
          pkBoundList.apply(2) -> pkBoundList.apply(3),
          pkBoundList.apply(3) -> pkBoundList.apply(4),
          pkBoundList.apply(4) -> pkBoundList.apply(5),
          pkBoundList.apply(5) -> pkBoundList.apply(6),
          pkBoundList.apply(6) -> pkBoundList.apply(7),
          pkBoundList.apply(7) -> pkBoundList.apply(8),
          pkBoundList.apply(8) -> pkBoundList.apply(9),
          pkBoundList.apply(9) -> pkBoundList.apply(10),
          pkBoundList.apply(10) -> pkBoundList.apply(11),
          pkBoundList.apply(11) -> pkBoundList.apply(12),
          pkBoundList.apply(12) -> pkBoundList.apply(13),
          pkBoundList.apply(13) -> pkBoundList.apply(14),
          pkBoundList.apply(14) -> pkBoundList.apply(15),
          pkBoundList.apply(15) -> pkBoundList.apply(16),
          pkBoundList.apply(16) -> pkBoundList.apply(17),
          pkBoundList.apply(17) -> pkBoundList.apply(18),
          pkBoundList.apply(18) -> pkBoundList.apply(19),
          pkBoundList.apply(19) -> pkBoundList.apply(20),
          pkBoundList.apply(20) -> pkBoundList.apply(21),
          pkBoundList.apply(21) -> pkBoundList.apply(22),
          pkBoundList.apply(22) -> pkBoundList.apply(23),
          pkBoundList.apply(23) -> pkBoundList.apply(24),
          pkBoundList.apply(24) -> pkBoundList.apply(25),
          pkBoundList.apply(25) -> pkBoundList.apply(26),
          pkBoundList.apply(26) -> pkBoundList.apply(27),
          pkBoundList.apply(27) -> pkBoundList.apply(28),
          pkBoundList.apply(28) -> pkBoundList.apply(29),
          pkBoundList.apply(29) -> pkBoundList.apply(30),
          pkBoundList.apply(30) -> pkBoundList.apply(31),
          pkBoundList.apply(31) -> pkBoundList.apply(32),
          pkBoundList.apply(32) -> pkBoundList.apply(33),
          pkBoundList.apply(33) -> pkBoundList.apply(34),
          pkBoundList.apply(34) -> pkBoundList.apply(35),
          pkBoundList.apply(35) -> pkBoundList.apply(36),
          pkBoundList.apply(36) -> pkBoundList.apply(37),
          pkBoundList.apply(37) -> pkBoundList.apply(38),
          pkBoundList.apply(38) -> pkBoundList.apply(39),
          pkBoundList.apply(39) -> pkBoundList.apply(40),
          pkBoundList.apply(40) -> pkBoundList.apply(41),
          pkBoundList.apply(41) -> pkBoundList.apply(42),
          pkBoundList.apply(42) -> pkBoundList.apply(43),
          pkBoundList.apply(43) -> pkBoundList.apply(44),
          pkBoundList.apply(44) -> pkBoundList.apply(45),
          pkBoundList.apply(45) -> pkBoundList.apply(46),
          pkBoundList.apply(46) -> pkBoundList.apply(47),
          pkBoundList.apply(47) -> pkBoundList.apply(48),
          pkBoundList.apply(48) -> pkBoundList.apply(49),
          pkBoundList.apply(49) -> pkBoundList.apply(50),
          pkBoundList.apply(50) -> pkBoundList.apply(51),
          pkBoundList.apply(51) -> pkBoundList.apply(52),
          pkBoundList.apply(52) -> pkBoundList.apply(53),
          pkBoundList.apply(53) -> pkBoundList.apply(54),
          pkBoundList.apply(54) -> pkBoundList.apply(55),
          pkBoundList.apply(55) -> pkBoundList.apply(56),
          pkBoundList.apply(56) -> pkBoundList.apply(57),
          pkBoundList.apply(57) -> pkBoundList.apply(58),
          pkBoundList.apply(58) -> pkBoundList.apply(59),
          pkBoundList.apply(59) -> pkBoundList.apply(60),
          pkBoundList.apply(60) -> pkBoundList.apply(61),
          pkBoundList.apply(61) -> pkBoundList.apply(62),
          pkBoundList.apply(62) -> pkBoundList.apply(63),
          pkBoundList.apply(63) -> pkBoundList.apply(pkBoundList.length - 1)

        ).map {
          case (start, end) =>
            s"${pks} >= '$start'" + s" AND ${pks} < '$end'"
        }
    }


    val upperBound = pkBoundList.apply(pkBoundList.length - 1)
    predicates.append(s"${pks} >= '${upperBound}'" + s" AND ${pks} <= '${upperBound}'")

    predicates.toArray
  }


}
