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

package org.datatech.baikal.mongosync.bean;

import java.io.Serializable;

/**
 * Source Jdbc bean.
 */
public class SourceJdbcBO implements Serializable {

    private String INSTANCE_NAME;
    private String SCHEMA_NAME;
    private String DB_TYPE;
    private String JDBC_URL;
    private String USER;
    private String PASSWORD;
    private String OS_IP;
    private String RMT_IP;
    private String tenantName;

    public String getINSTANCE_NAME() {
        return INSTANCE_NAME;
    }

    public void setINSTANCE_NAME(String INSTANCE_NAME) {
        this.INSTANCE_NAME = INSTANCE_NAME;
    }

    public String getSCHEMA_NAME() {
        return SCHEMA_NAME;
    }

    public void setSCHEMA_NAME(String SCHEMA_NAME) {
        this.SCHEMA_NAME = SCHEMA_NAME;
    }

    public String getDB_TYPE() {
        return DB_TYPE;
    }

    public void setDB_TYPE(String DB_TYPE) {
        this.DB_TYPE = DB_TYPE;
    }

    public String getJDBC_URL() {
        return JDBC_URL;
    }

    public void setJDBC_URL(String JDBC_URL) {
        this.JDBC_URL = JDBC_URL;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getOS_IP() {
        return OS_IP;
    }

    public void setOS_IP(String OS_IP) {
        this.OS_IP = OS_IP;
    }

    public String getRMT_IP() {
        return RMT_IP;
    }

    public void setRMT_IP(String RMT_IP) {
        this.RMT_IP = RMT_IP;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String toString() {
        return "SourceJdbcBO{" + "INSTANCE_NAME='" + INSTANCE_NAME + '\'' + ", SCHEMA_NAME='" + SCHEMA_NAME + '\''
                + ", DB_TYPE='" + DB_TYPE + '\'' + ", JDBC_URL='" + JDBC_URL + '\'' + ", USER='" + USER + '\''
                + ", PASSWORD='" + PASSWORD + '\'' + ", OS_IP='" + OS_IP + '\'' + ", RMT_IP='" + RMT_IP + '\''
                + ", tenantName='" + tenantName + '\'' + '}';
    }
}
