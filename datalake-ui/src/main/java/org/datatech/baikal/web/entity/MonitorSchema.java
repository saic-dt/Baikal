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

public class MonitorSchema implements Serializable {
    private String row_key;
    private String proc_type;
    private String proc_name;
    private String proc_status;
    private int proc_lag;
    private int proc_checkpoint;
    private int proc_rows;

    public MonitorSchema(String proc_name, String proc_status, int proc_rows) {
        super();
        this.proc_name = proc_name;
        this.proc_status = proc_status;
        this.proc_rows = proc_rows;
    }

    public MonitorSchema() {
        super();
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getProc_type() {
        return proc_type;
    }

    public void setProc_type(String proc_type) {
        this.proc_type = proc_type;
    }

    public String getProc_name() {
        return proc_name;
    }

    public void setProc_name(String proc_name) {
        this.proc_name = proc_name;
    }

    public String getProc_status() {
        return proc_status;
    }

    public void setProc_status(String proc_status) {
        this.proc_status = proc_status;
    }

    public int getProc_lag() {
        return proc_lag;
    }

    public void setProc_lag(int proc_lag) {
        this.proc_lag = proc_lag;
    }

    public int getProc_checkpoint() {
        return proc_checkpoint;
    }

    public void setProc_checkpoint(int proc_checkpoint) {
        this.proc_checkpoint = proc_checkpoint;
    }

    public int getProc_rows() {
        return proc_rows;
    }

    public void setProc_rows(int proc_rows) {
        this.proc_rows = proc_rows;
    }
}
