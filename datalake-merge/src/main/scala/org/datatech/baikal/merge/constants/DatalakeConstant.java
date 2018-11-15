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

package org.datatech.baikal.merge.constants;

public class DatalakeConstant {
    public static final String CF_NAME = "c";
    public static final String FLAG_CL_NAME = "__zt_";
    public static final String ENCRYPTION_CL = "cl1";
    public static final String DEFAULT_TENANT = "datalake";
    public static final String CONFIG_TABLE = "Config";
    public static final String ROLE_TABLE = "DataAccessRole";
    public static final String USER_TABLE = "DataAccessUser";
    public static final String DATA_INVALID = "-1";
    public static final String REGION_NUM_DEFAULT = "64";

    public static final String DEFAULT_ZK_HOST = "localhost:2181";
    public static final String DATALAKE_PROPERTIES_FILE_PATH = "datalake.properties.filepath";

    public static final String DATALAKE_HDFS_ROOT_PATH_NAME = "datalake.hdfs.root.path";
    public static final String DATALAKE_DEFAULT_HDFS_ROOT_PATH = "hdfs://localhost:9000/datalake";

    public static final String DATALAKE_DATE_FORMAT_NAME = "datalake.date.format";
    public static final String DATALAKE_DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATALAKE_TIMESTAMP_FORMAT_NAME = "datalake.timestamp.format";
    public static final String DATALAKE_DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATALAKE_PROPERTIES_PATH = "/conf/datalake.properties";

    public static final String HIVE_JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    public static final String JDBC_CLASS_NAME_ORACLE = "oracle.jdbc.driver.OracleDriver";
    public static final String JDBC_CLASS_NAME_DB2 = "com.ibm.db2.jcc.DB2Driver";
    public static final String JDBC_CLASS_NAME_MYSQL = "com.mysql.cj.jdbc.Driver";
    public static final String JDBC_CLASS_NAME_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

}
