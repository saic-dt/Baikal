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

package org.datatech.baikal.web.entity;

import java.util.List;

/**
 * 数据源管理页面实体类
 */
public class SourceDb implements Comparable<SourceDb> {
    // 列表
    private String dbType;
    private String dbName;
    private String ipAddress;
    private String mainProcPort;
    private String basePath;
    private String remoteCluster;
    private List<String> targetClusterList;
    private String tenantName;

    // 编辑
    private String rowKey;

    // 服务器
    private String osUser;

    // 数据库
    private String dbHome;
    private String dbPort;
    private String dbLogPath;
    private String rootUser;
    private String rootPasswd;

    // 链路
    private String linkUser;
    private String linkpassWord;

    @Override
    public int compareTo(SourceDb o) {
        int i = this.ipAddress.compareTo(o.getIpAddress());
        if (0 == i) {
            return this.dbName.compareTo(o.getDbName());
        }
        return i;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMainProcPort() {
        return mainProcPort;
    }

    public void setMainProcPort(String mainProcPort) {
        this.mainProcPort = mainProcPort;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getRemoteCluster() {
        return remoteCluster;
    }

    public void setRemoteCluster(String remoteCluster) {
        this.remoteCluster = remoteCluster;
    }

    public List<String> getTargetClusterList() {
        return targetClusterList;
    }

    public void setTargetClusterList(List<String> targetClusterList) {
        this.targetClusterList = targetClusterList;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getOsUser() {
        return osUser;
    }

    public void setOsUser(String osUser) {
        this.osUser = osUser;
    }

    public String getDbHome() {
        return dbHome;
    }

    public void setDbHome(String dbHome) {
        this.dbHome = dbHome;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbLogPath() {
        return dbLogPath;
    }

    public void setDbLogPath(String dbLogPath) {
        this.dbLogPath = dbLogPath;
    }

    public String getRootUser() {
        return rootUser;
    }

    public void setRootUser(String rootUser) {
        this.rootUser = rootUser;
    }

    public String getRootPasswd() {
        return rootPasswd;
    }

    public void setRootPasswd(String rootPasswd) {
        this.rootPasswd = rootPasswd;
    }

    public String getLinkUser() {
        return linkUser;
    }

    public void setLinkUser(String linkUser) {
        this.linkUser = linkUser;
    }

    public String getLinkpassWord() {
        return linkpassWord;
    }

    public void setLinkpassWord(String linkpassWord) {
        this.linkpassWord = linkpassWord;
    }
}