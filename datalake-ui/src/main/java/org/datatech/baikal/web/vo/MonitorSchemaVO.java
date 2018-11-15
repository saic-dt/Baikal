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

package org.datatech.baikal.web.vo;

import java.io.Serializable;

public class MonitorSchemaVO implements Serializable {
    private String RowKey;
    private String PROC_TYPE;
    private String PROC_STATUS;
    private String PROC_NAME;
    private Integer PROC_LAG;
    private Integer PROC_CHECKPOINT;
    private Integer PROC_ROWS;

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

    public String getPROC_NAME() {
        return PROC_NAME;
    }

    public void setPROC_NAME(String PROC_NAME) {
        this.PROC_NAME = PROC_NAME;
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
}
