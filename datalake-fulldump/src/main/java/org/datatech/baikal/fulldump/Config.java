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

/**
 * Default configurations and constants of full dump module.
 */
public class Config {
    public static int SPARK_JDBC_FETCHSIZE = 100000;
    public static int SPARK_RDD_PARTITION_NUM = 64;
    public static int SPARK_RDD_PARALLELISM_Num = 16;
    public static int META_FLAG_FULLDUMP_END = 3;
    public static String PATH_METASTORE = "metastore";
    public static String PATH_META = "meta";
    public static String ZK_NAMESPACE = "datalake";
    public static String PATH_QUEUE = "queue";
    public static String PATH_QUEUE_MAIN_TASK = "main_task";
    public static String PATH_QUEUE_TASK_PREFIX = "task_";
    public static String NOTIFY_FRESH_NODE = "notify_refresh";
    public static String NS_SCHEMA_NODE = "/datalake/schema";
    public static String NS_SCHEMA_MONGO_NODE = "/datalake/mongo-schema";
    public static String mongoTimestampSchema = "StructType(StructField(time,IntegerType,false), StructField(inc,IntegerType,false))";
    public static String mongoRegexSchema = "StructType(StructField(regex,StringType,true), StructField(options,StringType,true))";
    public static String mongoBinaryStrSchema = "StructType(StructField(subType,ByteType,false), StructField(data,BinaryType,true))";
    public static String mongoOIDSchema = "StructType(StructField(oid,StringType,true))";
    public static String mongoUndefinedSchema = "StructType(StructField(undefined,BooleanType,false))";
    public static String mongoCodeSchema = "StructType(StructField(code,StringType,true))";
    public static String mongoJsScopeSchema = "StructType(StructField(function,StructType(StructField(code,StringType,true)),true), StructField(scope,ArrayType(StringType,true),true))";

}
