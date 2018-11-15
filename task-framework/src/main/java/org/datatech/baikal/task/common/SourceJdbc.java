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
package org.datatech.baikal.task.common;

import com.google.gson.Gson;

/**
 * Source database JDBC configuration.
 */
public class SourceJdbc {
    private String INSTANCE_NAME;
    private String SCHEMA_NAME;
    private DbType DB_TYPE;
    private String JDBC_URL;
    private String USER;
    private String PASSWORD;
    private String OS_IP;
    private String RMT_IP;
    private boolean USE_CANAL;

    public SourceJdbc(String instanceName, String schemaName, DbType dbType, String jdbcUrl, String user,
            String password, String osIp, String rmtIp, boolean useCanal) {
        this.INSTANCE_NAME = instanceName;
        this.SCHEMA_NAME = schemaName;
        this.DB_TYPE = dbType;
        this.JDBC_URL = jdbcUrl;
        this.USER = user;
        this.PASSWORD = password;
        this.OS_IP = osIp;
        this.RMT_IP = rmtIp;
        this.USE_CANAL = useCanal;
    }

    public SourceJdbc() {

    }

    public boolean getUSE_CANAL() {
        return USE_CANAL;
    }

    public void setUSE_CANAL(boolean USE_CANAL) {
        this.USE_CANAL = USE_CANAL;
    }

    public String getINSTANCE_NAME() {
        return checkIsNull(INSTANCE_NAME);
    }

    public void setINSTANCE_NAME(String INSTANCE_NAME) {
        this.INSTANCE_NAME = INSTANCE_NAME;
    }

    public String getSCHEMA_NAME() {
        return checkIsNull(SCHEMA_NAME);
    }

    public void setSCHEMA_NAME(String SCHEMA_NAME) {
        this.SCHEMA_NAME = SCHEMA_NAME;
    }

    public DbType getDB_TYPE() {
        return DB_TYPE;
    }

    public void setDB_TYPE(DbType DB_TYPE) {
        this.DB_TYPE = DB_TYPE;
    }

    public String getJDBC_URL() {
        return checkIsNull(JDBC_URL);
    }

    public void setJDBC_URL(String JDBC_URL) {
        this.JDBC_URL = JDBC_URL;
    }

    public String getUSER() {
        return checkIsNull(USER);
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPASSWORD() {
        return checkIsNull(PASSWORD);
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getOS_IP() {
        return checkIsNull(OS_IP);
    }

    public void setOS_IP(String OS_IP) {
        this.OS_IP = OS_IP;
    }

    public String getRMT_IP() {
        return checkIsNull(RMT_IP);
    }

    public void setRMT_IP(String RMT_IP) {
        this.RMT_IP = RMT_IP;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    //replace "" from zookeeper Json value.
    private String checkIsNull(String str) {
        return ((str == null) ? "" : str.replaceAll("\"", ""));
    }
}
