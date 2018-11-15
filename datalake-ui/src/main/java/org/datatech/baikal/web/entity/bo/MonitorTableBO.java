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

public class MonitorTableBO implements Serializable {

    private static final long serialVersionUID = 1422550369303210574L;
    // todo 与接口参数对应
    @JsonProperty
    private String RowKey;
    @JsonProperty
    private Long INSERT_ROWS;
    @JsonProperty
    private Long UPDATE_ROWS;
    @JsonProperty
    private Long DELETE_ROWS;
    @JsonProperty
    private Long DISCARD_ROWS;
    @JsonProperty
    private Long TOTAL_ROWS;

    private String tenantName;

    public String getRowKey() {
        return RowKey;
    }

    public void setRowKey(String rowKey) {
        RowKey = rowKey;
    }

    public Long getINSERT_ROWS() {
        return INSERT_ROWS;
    }

    public void setINSERT_ROWS(Long INSERT_ROWS) {
        this.INSERT_ROWS = INSERT_ROWS;
    }

    public Long getUPDATE_ROWS() {
        return UPDATE_ROWS;
    }

    public void setUPDATE_ROWS(Long UPDATE_ROWS) {
        this.UPDATE_ROWS = UPDATE_ROWS;
    }

    public Long getDELETE_ROWS() {
        return DELETE_ROWS;
    }

    public void setDELETE_ROWS(Long DELETE_ROWS) {
        this.DELETE_ROWS = DELETE_ROWS;
    }

    public Long getDISCARD_ROWS() {
        return DISCARD_ROWS;
    }

    public void setDISCARD_ROWS(Long DISCARD_ROWS) {
        this.DISCARD_ROWS = DISCARD_ROWS;
    }

    public Long getTOTAL_ROWS() {
        return TOTAL_ROWS;
    }

    public void setTOTAL_ROWS(Long TOTAL_ROWS) {
        this.TOTAL_ROWS = TOTAL_ROWS;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
