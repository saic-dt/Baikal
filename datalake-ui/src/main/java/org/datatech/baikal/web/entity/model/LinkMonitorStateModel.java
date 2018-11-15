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

import org.datatech.baikal.web.entity.MonitorSchema;

/**
 * 链路监控页面进程状态类
 */

public class LinkMonitorStateModel {
    private String eprocName;
    private String eprocState;
    private int eprocRows;

    private String dprocName;
    private String dprocState;
    private int dprocRows;

    private String rprocName;
    private String rprocState;
    private int rprocRows;

    public LinkMonitorStateModel(String eprocName, String eprocState, int eprocRows, String dprocName,
            String dprocState, int dprocRows, String rprocName, String rprocState, int rprocRows) {
        this.eprocName = eprocName;
        this.eprocState = eprocState;
        this.eprocRows = eprocRows;
        this.dprocName = dprocName;
        this.dprocState = dprocState;
        this.dprocRows = dprocRows;
        this.rprocName = rprocName;
        this.rprocState = rprocState;
        this.rprocRows = rprocRows;
    }

    public LinkMonitorStateModel(MonitorSchema e, MonitorSchema d, MonitorSchema r) {
        super();
        eprocName = e.getProc_name();
        dprocName = d.getProc_name();
        rprocName = r.getProc_name();

        eprocState = e.getProc_status();
        dprocState = d.getProc_status();
        rprocState = r.getProc_status();

        eprocRows = e.getProc_rows();
        dprocRows = d.getProc_rows();
        rprocRows = r.getProc_rows();
    }

    public String getEprocName() {
        return eprocName;
    }

    public void setEprocName(String eprocName) {
        this.eprocName = eprocName;
    }

    public String getEprocState() {
        return eprocState;
    }

    public void setEprocState(String eprocState) {
        this.eprocState = eprocState;
    }

    public int getEprocRows() {
        return eprocRows;
    }

    public void setEprocRows(int eprocRows) {
        this.eprocRows = eprocRows;
    }

    public String getDprocName() {
        return dprocName;
    }

    public void setDprocName(String dprocName) {
        this.dprocName = dprocName;
    }

    public String getDprocState() {
        return dprocState;
    }

    public void setDprocState(String dprocState) {
        this.dprocState = dprocState;
    }

    public int getDprocRows() {
        return dprocRows;
    }

    public void setDprocRows(int dprocRows) {
        this.dprocRows = dprocRows;
    }

    public String getRprocName() {
        return rprocName;
    }

    public void setRprocName(String rprocName) {
        this.rprocName = rprocName;
    }

    public String getRprocState() {
        return rprocState;
    }

    public void setRprocState(String rprocState) {
        this.rprocState = rprocState;
    }

    public int getRprocRows() {
        return rprocRows;
    }

    public void setRprocRows(int rprocRows) {
        this.rprocRows = rprocRows;
    }
}
