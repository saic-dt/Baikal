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
 * Source database metadata for configuration and deployment.
 */
public class SourceDb {
    private String DB_SID;
    private DbType DB_TYPE;
    private String OS_IP;
    private String OS_USER;
    private String BASE_PATH;
    private String DB_HOME;
    private String GG_USER;
    private String GG_PASS;
    private String MGR_PORT;
    private String DB_LOG;
    private String DB_PORT;
    private String ADMIN_USER;
    private String ADMIN_PASS;
    private String RMT_IP;
    private String RMT_PORT;
    private String kerberos;
    private String kerberosKeytabFile;
    private String kerberosPrinciple;

    public SourceDb() {

    }

    public SourceDb(String dbSid, DbType dbType, String osIp, String osUser, String basePath, String dbHome,
            String ggUser, String mgrPort, String dbLog, String dbPort, String adminUser, String rmtIp,
            String rmtPort) {
        this.DB_SID = dbSid;
        this.DB_TYPE = dbType;
        this.OS_IP = osIp;
        this.OS_USER = osUser;
        this.BASE_PATH = basePath;
        this.DB_HOME = dbHome;
        this.GG_USER = ggUser;
        this.MGR_PORT = mgrPort;
        this.DB_LOG = dbLog;
        this.DB_PORT = dbPort;
        this.ADMIN_USER = adminUser;
        this.RMT_IP = rmtIp;
        this.RMT_PORT = rmtPort;
    }

    public String getDB_SID() {
        return checkIsNull(DB_SID);
    }

    public void setDB_SID(String DB_SID) {
        this.DB_SID = DB_SID;
    }

    public DbType getDB_TYPE() {
        return DB_TYPE;
    }

    public void setDB_TYPE(DbType DB_TYPE) {
        this.DB_TYPE = DB_TYPE;
    }

    public String getOS_IP() {
        return checkIsNull(OS_IP);
    }

    public void setOS_IP(String OS_IP) {
        this.OS_IP = OS_IP;
    }

    public String getOS_USER() {
        return checkIsNull(OS_USER);
    }

    public void setOS_USER(String OS_USER) {
        this.OS_USER = OS_USER;
    }

    public String getBASE_PATH() {
        return checkIsNull(BASE_PATH);
    }

    public void setBASE_PATH(String BASE_PATH) {
        this.BASE_PATH = BASE_PATH;
    }

    public String getDB_HOME() {
        return checkIsNull(DB_HOME);
    }

    public void setDB_HOME(String DB_HOME) {
        this.DB_HOME = DB_HOME;
    }

    public String getGG_USER() {
        return checkIsNull(GG_USER);
    }

    public void setGG_USER(String GG_USER) {
        this.GG_USER = GG_USER;
    }

    public String getGG_PASS() {
        return checkIsNull(GG_PASS);
    }

    public void setGG_PASS(String GG_PASS) {
        this.GG_PASS = GG_PASS;
    }

    public String getMGR_PORT() {
        return checkIsNull(MGR_PORT);
    }

    public void setMGR_PORT(String MGR_PORT) {
        this.MGR_PORT = MGR_PORT;
    }

    public String getDB_LOG() {
        return checkIsNull(DB_LOG);
    }

    public void setDB_LOG(String DB_LOG) {
        this.DB_LOG = DB_LOG;
    }

    public String getDB_PORT() {
        return checkIsNull(DB_PORT);
    }

    public void setDB_PORT(String DB_PORT) {
        this.DB_PORT = DB_PORT;
    }

    public String getADMIN_USER() {
        return checkIsNull(ADMIN_USER);
    }

    public void setADMIN_USER(String ADMIN_USER) {
        this.ADMIN_USER = ADMIN_USER;
    }

    public String getADMIN_PASS() {
        return checkIsNull(ADMIN_PASS);
    }

    public void setADMIN_PASS(String ADMIN_PASS) {
        this.ADMIN_PASS = ADMIN_PASS;
    }

    public String getRMT_IP() {
        return checkIsNull(RMT_IP);
    }

    public void setRMT_IP(String RMT_IP) {
        this.RMT_IP = RMT_IP;
    }

    public String getRMT_PORT() {
        return checkIsNull(RMT_PORT);
    }

    public void setRMT_PORT(String RMT_PORT) {
        this.RMT_PORT = RMT_PORT;
    }

    public String getKerberos() {
        return kerberos;
    }

    public void setKerberos(String kerberos) {
        this.kerberos = kerberos;
    }

    public String getKerberosKeytabFile() {
        return kerberosKeytabFile;
    }

    public void setKerberosKeytabFile(String kerberosKeytabFile) {
        this.kerberosKeytabFile = kerberosKeytabFile;
    }

    public String getKerberosPrinciple() {
        return kerberosPrinciple;
    }

    public void setKerberosPrinciple(String kerberosPrinciple) {
        this.kerberosPrinciple = kerberosPrinciple;
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
