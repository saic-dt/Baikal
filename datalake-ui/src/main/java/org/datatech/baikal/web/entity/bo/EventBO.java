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

/**
 */
public class EventBO implements Serializable {

    private static final long serialVersionUID = 9111888700007950594L;
    // todo 与接口参数对应
    @JsonProperty
    private Long EVENT_ID;
    @JsonProperty
    private Long PARENT_EVENT_ID;
    @JsonProperty
    private String SOURCE_INSTANCE;
    @JsonProperty
    private String SOURCE_SCHEMA;
    @JsonProperty
    private String SOURCE_TABLE;
    @JsonProperty
    private String MESSAGE;
    private String tenantName;

    public Long getEVENT_ID() {
        return EVENT_ID;
    }

    public void setEVENT_ID(Long EVENT_ID) {
        this.EVENT_ID = EVENT_ID;
    }

    public Long getPARENT_EVENT_ID() {
        return PARENT_EVENT_ID;
    }

    public void setPARENT_EVENT_ID(Long PARENT_EVENT_ID) {
        this.PARENT_EVENT_ID = PARENT_EVENT_ID;
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

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
