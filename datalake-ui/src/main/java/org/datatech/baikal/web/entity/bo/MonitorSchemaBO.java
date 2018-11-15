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

public class MonitorSchemaBO implements Serializable {

    private static final long serialVersionUID = 2427166350364573168L;
    // todo 与接口参数对应
    @JsonProperty
    private String RowKey;
    @JsonProperty
    private String PROC_TYPE;
    @JsonProperty
    private String PROC_STATUS;
    @JsonProperty
    private Integer PROC_LAG;
    @JsonProperty
    private Integer PROC_CHECKPOINT;
    @JsonProperty
    private Integer PROC_ROWS;

    private String tenantName;

    public String getRowKey() {
        return RowKey;
    }

    public void setRowKey(String rowKey) {
        RowKey = rowKey;
    }

    public String getPROC_TYPE() {
        return PROC_TYPE;
    }

    public void setPROC_TYPE(String PROC_TYPE) {
        this.PROC_TYPE = PROC_TYPE;
    }

    public String getPROC_STATUS() {
        return PROC_STATUS;
    }

    public void setPROC_STATUS(String PROC_STATUS) {
        this.PROC_STATUS = PROC_STATUS;
    }

    public Integer getPROC_LAG() {
        return PROC_LAG;
    }

    public void setPROC_LAG(Integer PROC_LAG) {
        this.PROC_LAG = PROC_LAG;
    }

    public Integer getPROC_CHECKPOINT() {
        return PROC_CHECKPOINT;
    }

    public void setPROC_CHECKPOINT(Integer PROC_CHECKPOINT) {
        this.PROC_CHECKPOINT = PROC_CHECKPOINT;
    }

    public Integer getPROC_ROWS() {
        return PROC_ROWS;
    }

    public void setPROC_ROWS(Integer PROC_ROWS) {
        this.PROC_ROWS = PROC_ROWS;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
