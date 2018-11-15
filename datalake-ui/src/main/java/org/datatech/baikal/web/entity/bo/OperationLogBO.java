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

package org.datatech.baikal.web.entity.bo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OperationLogBO implements Serializable {

    private static final long serialVersionUID = 8434858210689532652L;
    // todo 与接口参数对应
    @JsonProperty
    private Long ID;
    @JsonProperty
    private String USER_NAME;
    @JsonProperty
    private String SOURCE_INSTANCE;
    @JsonProperty
    private String SOURCE_SCHEMA;
    @JsonProperty
    private String SOURCE_TABLE;
    @JsonProperty
    private String MESSAGE;

    @JsonProperty
    private String DB_TYPE;

    @JsonProperty
    private String OPTERATE_TYPE;

    @JsonProperty
    private String TENANT_NAME;

    @JsonProperty
    private Long OPERATION_TIME;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getSOURCE_INSTANCE() {
        return SOURCE_INSTANCE;
    }

    public void setSOURCE_INSTANCE(String SOURCE_INSTANCE) {
        this.SOURCE_INSTANCE = SOURCE_INSTANCE;
    }

    public String getSOURCE_SCHEMA() {
        return SOURCE_SCHEMA;
    }

    public void setSOURCE_SCHEMA(String SOURCE_SCHEMA) {
        this.SOURCE_SCHEMA = SOURCE_SCHEMA;
    }

    public String getSOURCE_TABLE() {
        return SOURCE_TABLE;
    }

    public void setSOURCE_TABLE(String SOURCE_TABLE) {
        this.SOURCE_TABLE = SOURCE_TABLE;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getDB_TYPE() {
        return DB_TYPE;
    }

    public void setDB_TYPE(String DB_TYPE) {
        this.DB_TYPE = DB_TYPE;
    }

    public String getOPTERATE_TYPE() {
        return OPTERATE_TYPE;
    }

    public void setOPTERATE_TYPE(String OPTERATE_TYPE) {
        this.OPTERATE_TYPE = OPTERATE_TYPE;
    }

    public String getTENANT_NAME() {
        return TENANT_NAME;
    }

    public void setTENANT_NAME(String TENANT_NAME) {
        this.TENANT_NAME = TENANT_NAME;
    }

    public Long getOPERATION_TIME() {
        return OPERATION_TIME;
    }

    public void setOPERATION_TIME(Long OPERATION_TIME) {
        this.OPERATION_TIME = OPERATION_TIME;
    }
}
