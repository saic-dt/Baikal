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

public class MonitorTable implements Serializable {
    private String row_key;
    private int insert_rows;
    private int update_rows;
    private int delete_rows;
    private int discard_rows;
    private int total_rows;

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public int getInsert_rows() {
        return insert_rows;
    }

    public void setInsert_rows(int insert_rows) {
        this.insert_rows = insert_rows;
    }

    public int getUpdate_rows() {
        return update_rows;
    }

    public void setUpdate_rows(int update_rows) {
        this.update_rows = update_rows;
    }

    public int getDelete_rows() {
        return delete_rows;
    }

    public void setDelete_rows(int delete_rows) {
        this.delete_rows = delete_rows;
    }

    public int getDiscard_rows() {
        return discard_rows;
    }

    public void setDiscard_rows(int discard_rows) {
        this.discard_rows = discard_rows;
    }

    public int getTotal_rows() {
        return total_rows;
    }

    public void setTotal_rows(int total_rows) {
        this.total_rows = total_rows;
    }
}
