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

package org.datatech.baikal.common;

/**
 * Datalake constants.
 */
public class Configuration {

    /**
     * char 10 used for delimiter
     */
    public static String DELIMITER = "\n";

    /**
     * ZK schema paths for source databases
     */
    public static String PATH_SCHEMA = "schema";
    public static String PATH_MONGO_SCHEMA = "mongo-" + PATH_SCHEMA;
    public static String PATH_MYSQL_SCHEMA = "mysql-" + PATH_SCHEMA;

    /**
     * Prefix for nodes in ZK queue
     */
    public static String PATH_SEQ_PREFIX = "seq-";

    /**
     * ZK configuration
     */
    public static String PATH_CONFIG = "/config";
    /**
     * ZK queue
     */
    public static String PATH_QUEUE = "/queue";

    /**
     * Default ZK namespace
     */
    public static String ZK_NAMESPACE = "datalake";

    /**
     *  JDBC Driver class for various database types
     */
    public static String JDBC_CLASS_NAME_ORACLE = "oracle.jdbc.driver.OracleDriver";
    public static String JDBC_CLASS_NAME_DB2 = "com.ibm.db2.jcc.DB2Driver";
    public static String JDBC_CLASS_NAME_MYSQL = "com.mysql.cj.jdbc.Driver";
    public static String JDBC_CLASS_NAME_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static String JDBC_CLASS_NAME_MONGO = "mongo-java-driver";
    public static String JDBC_CLASS_NAME_IGNITE = "org.apache.ignite.IgniteJdbcDriver";

    /**
     * Default tenant name
     */
    public static String DEFAULT_TBL_NAMESPACE = "datalake";
}