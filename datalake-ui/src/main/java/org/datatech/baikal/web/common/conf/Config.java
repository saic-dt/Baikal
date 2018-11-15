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

package org.datatech.baikal.web.common.conf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class Config {
    /**
     *  关系型数据库驱动类
     */
    public static final String JDBC_CLASS_NAME_ORACLE = "oracle.jdbc.driver.OracleDriver";
    public static final String JDBC_CLASS_NAME_DB2 = "com.ibm.db2.jcc.DB2Driver";
    public static final String JDBC_CLASS_NAME_MYSQL = "com.mysql.cj.jdbc.Driver";
    public static final String JDBC_CLASS_NAME_SQL_SERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /**
     *  默认租户
     */

    public static final String DEFAULT_TBL_NAMESPACE = "datalake";
    public static final String DEFAULT_WILDCARD = "%";
    //metastore node path
    public static final String ZK_NODE_METASTORE = "/metastore";
    public static final String ZK_NODE_USER = "/users";
    public static final String ZK_NODE_TENANT = "/tenant";
    //ACL node path
    public static final String ZK_ACL_FORMATE = "/metastore/%s/meta/%s/%s/%s/ACL";
    //table node path
    public static final String ZK_META_FORMATE = "/metastore/%s/meta/%s/%s/%s";
    public static final int REMINDER_TIME = 7;
    public static final int FAILURE_NUMBER = 5;
    public static final int PASSWORD_GROUP_SIZE = 6;
    //sandbox node path
    public static final String SANDBOX_BASIS_PATH = "/sandbox";
    //sandbox sync table node path
    public static final String SANDBOX_INFO_PATH_TEMP = "/sandbox/%s/%s___%s___%s___%s";
    //sandbox name node path
    public static final String SANDBOX_PATH_TEMP = "/sandbox/%s";
    //sandbox table name format
    public static final String SANDBOX_MATCH_TEMP = "%s___%s___%s___%s";
    public static final String LINE_3 = "---";
    //task prefix
    public static final String TASK_PREFIX = "/task_";
    //table 表结构发送改变 通知zk节点
    public static final String PATH_NOTIFY_ACL = "notify_acl";
    public static String BACKSLASH = "/"; // char 10 used for delimiter
    public static String DELIMITER = "\n"; // char 10 used for delimiter
    //源数据库为关系型数据库生成的节点
    public static String PATH_SCHEMA = "schema";
    //源数据库为mongo数据库时生成的mongo节点
    public static String PATH_MONGO_SCHEMA = "mongo-schema";
    //zk 队列中节点序号前缀
    public static String PATH_SEQ_PREFIX = "seq-";
    //zk 配置文件路径
    public static String PATH_CONFIG = "/config";
    //zk queue
    public static String PATH_QUEUE = "/queue";
    //zk默认空间
    public static String ZK_NAMESPACE = "datalake";
    //zk main task队列路径
    public static String PATH_QUEUE_MAIN_TASK = "/queue/main_task";
    //初始化标记整合类 true 表示需要初始化
    public static Boolean startIntoFlg = true;
    public final String UNDERLINE_3 = "___";

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
