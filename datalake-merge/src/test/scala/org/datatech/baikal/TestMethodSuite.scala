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

import java.util

class TestMethodSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  test("randomArray") {
    val array = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println(util.Arrays.toString(Main.randomArray(array)))
  }

  test("splitTablePath") {
    val tablePath = "/datalake/tenant/instance/schema/table"
    val elements = tablePath.split("/")
    val root = elements(0).concat("/").concat(elements(1))
    val tenant = root.concat("/").concat(elements(2))
    Main.logging(s"Specific Tenant Path is: $tenant")
    val instance = tenant.concat("/").concat(elements(3))
    Main.logging(s"Specific Instance Path is: $instance")
    val schema = instance.concat("/").concat(elements(4))
    Main.logging(s"Specific Schema Path is: $schema")
    val table = schema.concat("/").concat(elements(5))
    Main.logging(s"Specific Table Path is: $table")
  }

  test("parseLong") {
    val str = "1"
    val l = java.lang.Long.valueOf(str)
    println(l)
  }

  test("transferSeq") {
    val str = "a,b"
    val a = str.split(",")
    val b: Seq[String] = a
    println(b)
    println(b(1))
  }


}
