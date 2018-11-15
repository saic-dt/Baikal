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

package org.datatech.baikal.web.modules.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置文件paralog
 */
@Component
public class Constants {

    public static String ZOOKEEPER_QUORUM;

    public static String KERBEROS_ENABLE;
    public static String HBASE_PRINCIPAL;
    public static String KEYTAB_FILE;
    public static String HOST_SETTINGS_PATH;
    public static Integer INTERVAL_TIME;
    public static Integer OVERTIME_TIME;
    public static Boolean TEST_MODE;
    public static String ES_URL;

    public static String SQLITE_CLASSNAME;
    public static String SQLITE_DBPATH;
    public static String SQLITE_USERNAME;
    public static String SQLITE_PASSWORD;
    public static String SQLITE_EVENT_DBFILE;

    public static String SQLITE_OPERATION_LOG_DBFILE;

    public static String getZookeeperQuorum() {
        return ZOOKEEPER_QUORUM;
    }

    @Value("${datalake-ui.zookeeper.quorum}")
    public void setZookeeperQuorum(String zookeeperQuorum) {
        Constants.ZOOKEEPER_QUORUM = zookeeperQuorum;
    }

    @Value("${datalake-ui.sqlite.operationlog.dbfile}")
    public void setSqliteOperationLogDbfile(String sqliteOperationLogDbfile) {
        Constants.SQLITE_OPERATION_LOG_DBFILE = sqliteOperationLogDbfile;
    }

    @Value("${datalake-ui.sqlite.event.dbfile}")
    public void setSqliteEventDbfile(String sqliteEventDbfile) {
        Constants.SQLITE_EVENT_DBFILE = sqliteEventDbfile;
    }

    @Value("${datalake-ui.sqlite.class-name}")
    public void setSqliteClassname(String sqliteClassname) {
        Constants.SQLITE_CLASSNAME = sqliteClassname;
    }

    @Value("${datalake-ui.sqlite.dbpath.dbfile}")
    public void setSqliteDbpath(String sqliteDbpath) {
        Constants.SQLITE_DBPATH = sqliteDbpath;
    }

    @Value("${datalake-ui.sqlite.username}")
    public void setSqliteUsername(String sqliteUsername) {
        Constants.SQLITE_USERNAME = sqliteUsername;
    }

    @Value("${datalake-ui.sqlite.password}")
    public void setSqlitePassword(String sqlitePassword) {
        Constants.SQLITE_PASSWORD = sqlitePassword;
    }

    @Value("${datalake-ui.elasticsearch.url}")
    public void setEsURL(String esUrl) {
        Constants.ES_URL = esUrl.replaceAll("\\s*", "");
    }

    @Value("${datalake-ui.test.mode}")
    public void setTestMode(Boolean testMode) {
        Constants.TEST_MODE = testMode;
    }

    @Value("${datalake-ui.interval.time}")
    public void setIntervalTime(Integer intervalTime) {
        Constants.INTERVAL_TIME = intervalTime;
    }

    @Value("${datalake-ui.overtime.time}")
    public void setOvertimeTime(Integer overtimeTime) {
        Constants.OVERTIME_TIME = overtimeTime;
    }

    @Value("${datalake-ui.krb-enable}")
    public void setKerberosEnable(String kerberosEnable) {
        KERBEROS_ENABLE = kerberosEnable;
    }

    @Value("${datalake-ui.hbase-principal}")
    public void setHbasePrincipal(String hbasePrincipal) {
        HBASE_PRINCIPAL = hbasePrincipal;
    }

    @Value("${datalake-ui.keytab-file}")
    public void setKeytabFile(String keytabFile) {
        KEYTAB_FILE = keytabFile;
    }

    @Value("${datalake-ui.hostSettings-path}")
    public void setHostSettingsPath(String hostSettingsPath) {
        HOST_SETTINGS_PATH = hostSettingsPath;
    }
}
