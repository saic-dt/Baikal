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
package org.datatech.baikal.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.netflix.config.DynamicPropertyFactory;

/**
 * Default configurations and constants of task management module.
 */
@Configuration
@ComponentScan(basePackages = { "org.datatech.baikal.task" })
public class Config {
    public static String HBASE_TABLENAME_DELIMITER = "___";
    public static String PATH_TASK = "/task";
    public static String PATH_BEGIN = "begin";
    public static String PATH_PROCESSING = "processing";
    public static String PATH_METASTORE = "metastore";
    public static String PATH_META = "meta";
    public static String PATH_SOURCEDB = "SourceDb";
    public static String PATH_SOURCEJDBC = "SourceJdbc";
    public static String PATH_NOTIFY = "notify";
    public static String PATH_PREFIX_SEQ = "prefix_seq";
    public static String PATH_NOTIFY_END = "notify_end";
    public static boolean ZK_TX_SUPPORT = false;
    public static String PATH_QUEUE_MAIN_TASK = "main_task";
    public static String PATH_QUEUE_TASK_PREFIX = "task_";
    public static String PATH_QUEUE_TASK_BACK_OUT = "task_back_out";
    public static String CFG_MAX_TASK = "max.task";
    public static String CFG_MAX_TASK_PER_HOST = "max.task.per.host";
    public static String CFG_MAX_TASK_RETRY = "max.task.retry";
    public static String CFG_TASK_WAIT_TIMEOUT = "task.wait.timeout";
    public static String CFG_DT_CMD_PATH = "dt.cmd.path";
    public static String CFG_DT_CMD_MONITOR = "dt.cmd.monitor";
    public static String CFG_DT_CMD_CONSOLE = "dt.cmd.console";
    public static String CFG_DT_CMD_INSTALL = "dt.cmd.install";
    public static String DEFAULT_DT_CMD_MONITOR = "dbmonitor";
    public static String DEFAULT_DT_CMD_CONSOLE = "dbconsole";
    public static String DEFAULT_DT_CMD_INSTALL = "dbinstall";
    public static int DEFAULT_TASK_WAIT_TIMEOUT = 60;
    public static int META_FLAG_FULLDUMP_BEGIN = 0;
    public static int META_FLAG_FULLDUMP_END = 3;
    public static int META_FLAG_FULLDUMP_FAILED = 5;
    public static int META_FLAG_REFRESH_TABLE_FAILED = 6;
    public static int META_FLAG_TASK_FAILED = 7;
    public static int META_FLAG_SECONDARY_TASK_TIMEOUT = 8;
    public static int DEFAULT_MAX_TASK = 20;
    public static int DEFAULT_MAX_TASK_PER_HOST = 10;
    public static int DEFAULT_MAX_TASK_RETRY = 5;
    public static String DB_TYPE_MONGO = "MONGO";
    public static String DB_TYPE_MYSQL = "MYSQL";
    public static String FIELD_SEPARATOR = "___";
    public static String NOTIFY_PATH = "/sandbox/.notifier_task";

    /**
     * Get string property from zookeeper config node.
     *
     * @param propName     name of the zookeeper config node
     * @param defaultValue default value if node not found
     * @return value from zookeeper config node
     */
    public static String getStringProperty(String propName, String defaultValue) {
        return DynamicPropertyFactory.getInstance().getStringProperty(propName, defaultValue).get();
    }

    /**
     * Get int property from zookeeper config node.
     *
     * @param propName     name of the zookeeper config node
     * @param defaultValue default value if node not found
     * @return value from zookeeper config node
     */
    public static int getIntProperty(String propName, int defaultValue) {
        return DynamicPropertyFactory.getInstance().getIntProperty(propName, defaultValue).get();
    }

    /**
     * Set null as default value for a value in spring.
     * see <a href="https://stackoverflow.com/questions/11991194/can-i-set-null-as-the-default-value-for-a-value-in-spring">
     * https://stackoverflow.com/questions/11991194/can-i-set-null-as-the-default-value-for-a-value-in-spring</a>
     * and <a href="https://stackoverflow.com/questions/41861870/set-default-value-to-null-in-spring-value-on-a-java-util-set-variable">
     * https://stackoverflow.com/questions/41861870/set-default-value-to-null-in-spring-value-on-a-java-util-set-variable</a>
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setNullValue("@null");
        return c;
    }
}
