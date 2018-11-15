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

package org.datatech.baikal.task.common;

import com.google.gson.Gson;

/**
 * Meta configuration data of table.
 */
public class MetaConfig {
    private long TABLE_VERSION;
    private String SOURCE_INSTANCE;
    private String SOURCE_SCHEMA;
    private String SOURCE_TABLE;
    private String PREFIX;
    private long SOFAR;
    private long TOTAL_WORK;
    private String SOURCE_PK;
    private int META_FLAG;

    /**
     * Default constructor.
     */
    public MetaConfig() {
    }

    /**
     * Constructor with parameters.
     *
     * @param tableVersion   Table version.
     * @param sourceInstance Source instance name.
     * @param sourceSchema   Source schema name.
     * @param sourceTable    Source table name.
     * @param prefix         Prefix string.
     * @param metaFlag       Meta flag.
     */
    public MetaConfig(long tableVersion, String sourceInstance, String sourceSchema, String sourceTable, String prefix,
            int metaFlag) {
        this.TABLE_VERSION = tableVersion;
        this.SOURCE_INSTANCE = sourceInstance;
        this.SOURCE_SCHEMA = sourceSchema;
        this.SOURCE_TABLE = sourceTable;
        this.PREFIX = prefix;
        this.META_FLAG = metaFlag;
    }

    /**
     * Get table version.
     *
     * @return Table version.
     */
    public long getTABLE_VERSION() {
        return TABLE_VERSION;
    }

    /**
     * Set table version
     *
     * @param TABLE_VERSION Table version.
     */
    public void setTABLE_VERSION(long TABLE_VERSION) {
        this.TABLE_VERSION = TABLE_VERSION;
    }

    /**
     * Get source instance name.
     *
     * @return Source instance name.
     */
    public String getSOURCE_INSTANCE() {
        return SOURCE_INSTANCE;
    }

    /**
     * Set source instance name.
     *
     * @param SOURCE_INSTANCE Source instance name.
     */
    public void setSOURCE_INSTANCE(String SOURCE_INSTANCE) {
        this.SOURCE_INSTANCE = SOURCE_INSTANCE;
    }

    /**
     * Get source schema name.
     *
     * @return Source schema name.
     */
    public String getSOURCE_SCHEMA() {
        return SOURCE_SCHEMA;
    }

    /**
     * Set source schema name.
     *
     * @param SOURCE_SCHEMA Source schema name.
     */
    public void setSOURCE_SCHEMA(String SOURCE_SCHEMA) {
        this.SOURCE_SCHEMA = SOURCE_SCHEMA;
    }

    /**
     * Get source table name.
     *
     * @return Source table name.
     */
    public String getSOURCE_TABLE() {
        return SOURCE_TABLE;
    }

    /**
     * Set source table name.
     *
     * @param SOURCE_TABLE Source table name.
     */
    public void setSOURCE_TABLE(String SOURCE_TABLE) {
        this.SOURCE_TABLE = SOURCE_TABLE;
    }

    /**
     * Get prefix string.
     *
     * @return Prefix string.
     */
    public String getPREFIX() {
        return PREFIX;
    }

    /**
     * Set prefix string.
     *
     * @param PREFIX Prefix string.
     */
    public void setPREFIX(String PREFIX) {
        this.PREFIX = PREFIX;
    }

    /**
     * Get number of records synchronized so far.
     *
     * @return Number of records synchronized so far.
     */
    public long getSOFAR() {
        return SOFAR;
    }

    /**
     * Set number of records synchronized so far.
     *
     * @param SOFAR Number of records synchronized so far.
     */
    public void setSOFAR(long SOFAR) {
        this.SOFAR = SOFAR;
    }

    /**
     * Get total number of records need to be synchronized.
     *
     * @return Total number of records need to be synchronized.
     */
    public long getTOTAL_WORK() {
        return TOTAL_WORK;
    }

    /**
     * Set total number of records need to be synchronized.
     *
     * @param TOTAL_WORK Total number of records need to be synchronized.
     */
    public void setTOTAL_WORK(long TOTAL_WORK) {
        this.TOTAL_WORK = TOTAL_WORK;
    }

    /**
     * Get primary key column list string of source table.
     *
     * @return Primary key column list string of source table.
     */
    public String getSOURCE_PK() {
        return SOURCE_PK;
    }

    /**
     * Set primary key column list string of source table.
     *
     * @param SOURCE_PK Primary key column list string of source table.
     */
    public void setSOURCE_PK(String SOURCE_PK) {
        this.SOURCE_PK = SOURCE_PK;
    }

    /**
     * Get value of meta flag.
     *
     * @return Value of meta flag.
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_BEGIN META_FLAG_FULLDUMP_BEGIN
     * @see org.datatech.baikal.task.Config#META_FLAG_DELETED META_FLAG_DELETED
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_SUBMIT META_FLAG_FULLDUMP_SUBMIT
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_END META_FLAG_FULLDUMP_END
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_FAILED META_FLAG_FULLDUMP_FAILED
     * @see org.datatech.baikal.task.Config#META_FLAG_REFRESH_TABLE_FAILED META_FLAG_REFRESH_TABLE_FAILED
     * @see org.datatech.baikal.task.Config#META_FLAG_TASK_FAILED META_FLAG_TASK_FAILED
     * @see org.datatech.baikal.task.Config#META_FLAG_SECONDARY_TASK_TIMEOUT META_FLAG_SECONDARY_TASK_TIMEOUT
     */
    public int getMETA_FLAG() {
        return META_FLAG;
    }

    /**
     * Set value of meta flag，
     *
     * @param META_FLAG Value of meta flag.
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_BEGIN META_FLAG_FULLDUMP_BEGIN
     * @see org.datatech.baikal.task.Config#META_FLAG_DELETED META_FLAG_DELETED
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_SUBMIT META_FLAG_FULLDUMP_SUBMIT
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_END META_FLAG_FULLDUMP_END
     * @see org.datatech.baikal.task.Config#META_FLAG_FULLDUMP_FAILED META_FLAG_FULLDUMP_FAILED
     * @see org.datatech.baikal.task.Config#META_FLAG_REFRESH_TABLE_FAILED META_FLAG_REFRESH_TABLE_FAILED
     * @see org.datatech.baikal.task.Config#META_FLAG_TASK_FAILED META_FLAG_TASK_FAILED
     * @see org.datatech.baikal.task.Config#META_FLAG_SECONDARY_TASK_TIMEOUT META_FLAG_SECONDARY_TASK_TIMEOUT
     */
    public void setMETA_FLAG(int META_FLAG) {
        this.META_FLAG = META_FLAG;
    }

    /**
     * Get json string of meta configuration object.
     *
     * @return Json string of meta configuration object.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}