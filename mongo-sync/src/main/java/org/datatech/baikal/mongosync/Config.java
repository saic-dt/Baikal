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

package org.datatech.baikal.mongosync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.netflix.config.DynamicPropertyFactory;

/**
 * Default configurations and constants of MongoDB sync module.
 */
@Configuration
public class Config {
    /**
     * Char 10 used for delimiter
     */
    public final static String DELIMITER = "\n";
    public final static String UNDERLINE_3 = "___";
    public final static String PATH_TASK = "/task";
    public final static String PATH_SCHEMA = "/mongo-schema";
    public final static String PATH_NOTIFY = "notify";
    public final static String PATH_SEQ_PREFIX = "seq-";
    public final static String ZK_NAMESPACE = "datalake";
    public final static boolean ZK_TX_SUPPORT = false;
    public final static String PATH_CONFIG = "/config";
    public final static String PATH_SYNC = "/mongo/.sync";
    public final static String PATHFORMAT = "/metastore/%s/SourceJdbc/%s/%s";
    public final static String PATTERN_SYNC = "\\" + PATH_SCHEMA + "\\/.*?\\/.*?\\/.*?\\/notify\\/seq-.*";
    public final static String ACL_PATTERN_SYNC = "\\" + PATH_SCHEMA + "\\/.*?\\/.*?\\/.*?\\/notify_acl\\/seq-.*";
    public final static String LOADER_PATH = "loader.path";
    public static String CFG_KEY_DB = "key.db";
    public static String CFG_KEY_DB_BACKUP = "key.db.backup";
    public static String CFG_CIPHER_KEY_VERSION = "cipher.key.version";

    /**
     * get string property from zookeeper config node.
     * @param propName name of the zookeeper config node
     * @param defaultValue default value if node not found
     * @return value from zookeeper config node
     */
    public static String getStringProperty(String propName, String defaultValue) {
        return DynamicPropertyFactory.getInstance().getStringProperty(propName, defaultValue).get();
    }

    /**
     * get int property from zookeeper config node.
     * @param propName name of the zookeeper config node
     * @param defaultValue default value if node not found
     * @return value from zookeeper config node
     */
    public static int getIntProperty(String propName, int defaultValue) {
        return DynamicPropertyFactory.getInstance().getIntProperty(propName, defaultValue).get();
    }

    /**
     * set null as default value for a value in spring.
     * see <a href="https://stackoverflow.com/questions/11991194/can-i-set-null-as-the-default-value-for-a-value-in-spring">
     *   https://stackoverflow.com/questions/11991194/can-i-set-null-as-the-default-value-for-a-value-in-spring</a>
     * and <a href="https://stackoverflow.com/questions/41861870/set-default-value-to-null-in-spring-value-on-a-java-util-set-variable">
     * https://stackoverflow.com/questions/41861870/set-default-value-to-null-in-spring-value-on-a-java-util-set-variable</a>
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setNullValue("@null");
        return c;
    }
}
