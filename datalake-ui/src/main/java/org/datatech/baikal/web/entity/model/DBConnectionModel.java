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

package org.datatech.baikal.web.entity.model;

import java.util.HashMap;

import org.datatech.baikal.web.utils.DataBaseUtil;

public class DBConnectionModel {
    private String sourceSchema;
    private String sourceInstance;
    private String cln;
    private String jdbcUrl;
    private String password;
    private String user;
    private String tableName;

    public DBConnectionModel() {

    }

    public DBConnectionModel(HashMap<String, String> vlu) {
        sourceSchema = vlu.get("SOURCE_SCHEMA");
        sourceInstance = vlu.get("SOURCE_INSTANCE");
        cln = DataBaseUtil.DRIVE_CLASS_MAP.get(vlu.get("DB_TYPE") == null ? null : vlu.get("DB_TYPE").toUpperCase());
        jdbcUrl = vlu.get("JDBC_URL");
        password = vlu.get("PASSWORD");
        user = vlu.get("USER");
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSourceSchema() {
        return sourceSchema;
    }

    public void setSourceSchema(String sourceSchema) {
        this.sourceSchema = sourceSchema;
    }

    public String getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(String sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    public String getCln() {
        return cln;
    }

    public void setCln(String cln) {
        this.cln = cln;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
