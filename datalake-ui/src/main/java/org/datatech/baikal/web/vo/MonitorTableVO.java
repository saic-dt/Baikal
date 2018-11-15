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

public class MonitorTableVO implements Serializable {
    private String RowKey;
    private Long insert_rows;
    private Long update_rows;
    private Long delete_rows;
    private Long discard_rows;
    private Long total_rows;
    private String createTime;
    private String source_table;
    private String source_instance;
    private String source_schema;

    public String getRowKey() {
        return RowKey;
    }

    public void setRowKey(String rowKey) {
        RowKey = rowKey;
    }

    public Long getInsert_rows() {
        return insert_rows;
    }

    public void setInsert_rows(Long insert_rows) {
        this.insert_rows = insert_rows;
    }

    public Long getUpdate_rows() {
        return update_rows;
    }

    public void setUpdate_rows(Long update_rows) {
        this.update_rows = update_rows;
    }

    public Long getDelete_rows() {
        return delete_rows;
    }

    public void setDelete_rows(Long delete_rows) {
        this.delete_rows = delete_rows;
    }

    public Long getDiscard_rows() {
        return discard_rows;
    }

    public void setDiscard_rows(Long discard_rows) {
        this.discard_rows = discard_rows;
    }

    public Long getTotal_rows() {
        return total_rows;
    }

    public void setTotal_rows(Long total_rows) {
        this.total_rows = total_rows;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSource_table() {
        return source_table;
    }

    public void setSource_table(String source_table) {
        this.source_table = source_table;
    }

    public String getSource_instance() {
        return source_instance;
    }

    public void setSource_instance(String source_instance) {
        this.source_instance = source_instance;
    }

    public String getSource_schema() {
        return source_schema;
    }

    public void setSource_schema(String source_schema) {
        this.source_schema = source_schema;
    }
}
