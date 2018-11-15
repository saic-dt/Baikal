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

package org.datatech.baikal.web.entity.bean;

/**
 * SourceDb表实例 包含部分前端字段
 */

public class SourceDbBean {

    private String rowKey;
    private String dbType;
    private String osUser;
    private String basePath;
    private String ggsHome;
    private String dbHome;
    private String ggUser;
    private String mgrPort;
    private String dbLog;
    private String dbPort;
    private String adminUser;
    private String rmtIp;
    private String rmtPort;

    public SourceDbBean() {
        super();
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getOsUser() {
        return osUser;
    }

    public void setOsUser(String osUser) {
        this.osUser = osUser;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getGgsHome() {
        return ggsHome;
    }

    public void setGgsHome(String ggsHome) {
        this.ggsHome = ggsHome;
    }

    public String getDbHome() {
        return dbHome;
    }

    public void setDbHome(String dbHome) {
        this.dbHome = dbHome;
    }

    public String getGgUser() {
        return ggUser;
    }

    public void setGgUser(String ggUser) {
        this.ggUser = ggUser;
    }

    public String getMgrPort() {
        return mgrPort;
    }

    public void setMgrPort(String mgrPort) {
        this.mgrPort = mgrPort;
    }

    public String getDbLog() {
        return dbLog;
    }

    public void setDbLog(String dbLog) {
        this.dbLog = dbLog;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getRmtIp() {
        return rmtIp;
    }

    public void setRmtIp(String rmtIp) {
        this.rmtIp = rmtIp;
    }

    public String getRmtPort() {
        return rmtPort;
    }

    public void setRmtPort(String rmtPort) {
        this.rmtPort = rmtPort;
    }
}
