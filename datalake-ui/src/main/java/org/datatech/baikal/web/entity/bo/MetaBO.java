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

/**
 */

public class MetaBO implements Serializable {

    private static final long serialVersionUID = 3299104064762641345L;
    // todo 与接口参数对应
    private Long TABLE_VERSION;
    private String SOURCE_INSTANCE;
    private String SOURCE_SCHEMA;
    private String SOURCE_TABLE;
    private Long SOFAR;
    private Long TOTAL_WORK;
    private String META_FLAG;
    private String DDL_CHANGED;
    private String PREFIX;
    private String SOURCE_PK;

    public Long getTABLE_VERSION() {
        return TABLE_VERSION;
    }

    public void setTABLE_VERSION(Long TABLE_VERSION) {
        this.TABLE_VERSION = TABLE_VERSION;
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

    public Long getSOFAR() {
        return SOFAR;
    }

    public void setSOFAR(Long SOFAR) {
        this.SOFAR = SOFAR;
    }

    public Long getTOTAL_WORK() {
        return TOTAL_WORK;
    }

    public void setTOTAL_WORK(Long TOTAL_WORK) {
        this.TOTAL_WORK = TOTAL_WORK;
    }

    public String getMETA_FLAG() {
        return META_FLAG;
    }

    public void setMETA_FLAG(String META_FLAG) {
        this.META_FLAG = META_FLAG;
    }

    public String getDDL_CHANGED() {
        return DDL_CHANGED;
    }

    public void setDDL_CHANGED(String DDL_CHANGED) {
        this.DDL_CHANGED = DDL_CHANGED;
    }

    public String getPREFIX() {
        return PREFIX;
    }

    public void setPREFIX(String PREFIX) {
        this.PREFIX = PREFIX;
    }

    public String getSOURCE_PK() {
        return SOURCE_PK;
    }

    public void setSOURCE_PK(String SOURCE_PK) {
        this.SOURCE_PK = SOURCE_PK;
    }

    @Override
    public String toString() {
        return "ConfigBO{" + "TABLE_VERSION=" + TABLE_VERSION + ", SOURCE_INSTANCE='" + SOURCE_INSTANCE + '\''
                + ", SOURCE_SCHEMA='" + SOURCE_SCHEMA + '\'' + ", SOURCE_TABLE='" + SOURCE_TABLE + '\'' + ", SOFAR="
                + SOFAR + ", TOTAL_WORK=" + TOTAL_WORK + ", META_FLAG='" + META_FLAG + '\'' + ", DDL_CHANGED='"
                + DDL_CHANGED + '\'' + '}';
    }
}
