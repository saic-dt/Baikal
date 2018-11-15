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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>
 * 用户实体类
 * <p>
 *
 * @version 2.0
 */

public class User implements Serializable {

    private static final long serialVersionUID = -2234039732195524515L;
    private String username;
    private String password;
    private String tenantName;
    private String esurl;
    private String model;
    private String fromClient;
    private String failureNumber;
    private String loginDate;
    private long lastMotifyPasswordTime;
    private boolean lockFlag;
    private long lockTime;
    private ArrayList<String> updatePassword;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String tenantName) {
        super();
        this.username = username;
        this.tenantName = tenantName;
    }

    public String getFromClient() {
        return fromClient;
    }

    public void setFromClient(String fromClient) {
        this.fromClient = fromClient;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEsurl() {
        return esurl;
    }

    public void setEsurl(String esurl) {
        this.esurl = esurl;
    }

    public String getFailureNumber() {
        return failureNumber;
    }

    public void setFailureNumber(String failureNumber) {
        this.failureNumber = failureNumber;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public long getLastMotifyPasswordTime() {
        return lastMotifyPasswordTime;
    }

    public void setLastMotifyPasswordTime(long lastMotifyPasswordTime) {
        this.lastMotifyPasswordTime = lastMotifyPasswordTime;
    }

    public ArrayList<String> getUpdatePassword() {
        return updatePassword;
    }

    public void setUpdatePassword(ArrayList<String> updatePassword) {
        this.updatePassword = updatePassword;
    }
}
