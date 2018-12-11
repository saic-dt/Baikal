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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class Configuration {

    public static String DELIMITER = "\n"; // char 10 used for delimiter
    //源数据库为关系型数据库生成的节点
    public static String PATH_SCHEMA = "schema";
    public static String PATH_MONGO_SCHEMA = "mongo-" + PATH_SCHEMA;
    public static String PATH_MYSQL_SCHEMA = "mysql-" + PATH_SCHEMA;

    //zk 队列中节点序号前缀
    public static String PATH_SEQ_PREFIX = "seq-";
    //zk 配置文件路径
    public static String PATH_CONFIG = "/config";
    //zk queue
    public static String PATH_QUEUE = "/queue";
    //zk默认空间
    public static String ZK_NAMESPACE = "datalake";
    /**
     *  关系型数据库驱动类
     */
    public static String JDBC_CLASS_NAME_ORACLE = "oracle.jdbc.driver.OracleDriver";
    public static String JDBC_CLASS_NAME_DB2 = "com.ibm.db2.jcc.DB2Driver";
    public static String JDBC_CLASS_NAME_MYSQL = "com.mysql.cj.jdbc.Driver";
    public static String JDBC_CLASS_NAME_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static String JDBC_CLASS_NAME_MONGO = "mongo-java-driver";
    public static String JDBC_CLASS_NAME_IGNITE = "org.apache.ignite.IgniteJdbcDriver";

    //默认租户
    public static String DEFAULT_TBL_NAMESPACE = "datalake";

    /**
     * 根据给定的步长输出时刻集合
     *
     * @param step 步长
     * @return map集合
     */
    public static Map<String, Long> stepTimeMap(int step) {
        if (0 >= step || step > 24 * 60) {
            return null;
        }

        int count = 24 * 60 / step;
        if (24 * 60 % step > 0) {
            count = count + 1;
        }

        Map<String, Long> treeMap = new TreeMap<String, Long>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < count; i++) {
            treeMap.put(sdf.format(calendar.getTime()), 0L);
            calendar.add(Calendar.MINUTE, step);
        }
        return treeMap;
    }
}