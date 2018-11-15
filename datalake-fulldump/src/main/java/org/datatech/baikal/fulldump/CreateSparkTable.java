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

package org.datatech.baikal.fulldump;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.spark.sql.SparkSession;

/**
 * Main Entry point class to launch Spark job to run Spark SQL DDL.
 */
public class CreateSparkTable {
    public static void main(String[] args) throws Exception {
        String ddl = args[0];

        try {
            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDD ddl is: " + ddl);
            SparkSession sparkSession = SparkSession.builder().appName("Java Spark SQL basic").master("local[*]")
                    .config("spark.some.config.option", "some-value").getOrCreate();
            sparkSession.sql(ddl);
            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDD Spark table created");

            Files.write(Paths.get("2.log"), "finished".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
