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

public class Meta implements Serializable {

    private String row_key;
    private Long table_version;
    private String source_instance;
    private String source_schema;
    private String source_table;
    private String hbase_namespace;
    private String hbase_schema;
    private String hbase_table;
    private String hive_schema;
    private String hive_table;
    private Long sofar;
    private Long totalwork;
    private String meta_flag;
    private String ddl_changed;
    private Long totalwork_timestamp;

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public Long getTable_version() {
        return table_version;
    }

    public void setTable_version(Long table_version) {
        this.table_version = table_version;
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

    public String getSource_table() {
        return source_table;
    }

    public void setSource_table(String source_table) {
        this.source_table = source_table;
    }

    public String getHbase_namespace() {
        return hbase_namespace;
    }

    public void setHbase_namespace(String hbase_namespace) {
        this.hbase_namespace = hbase_namespace;
    }

    public String getHbase_schema() {
        return hbase_schema;
    }

    public void setHbase_schema(String hbase_schema) {
        this.hbase_schema = hbase_schema;
    }

    public String getHbase_table() {
        return hbase_table;
    }

    public void setHbase_table(String hbase_table) {
        this.hbase_table = hbase_table;
    }

    public String getHive_schema() {
        return hive_schema;
    }

    public void setHive_schema(String hive_schema) {
        this.hive_schema = hive_schema;
    }

    public String getHive_table() {
        return hive_table;
    }

    public void setHive_table(String hive_table) {
        this.hive_table = hive_table;
    }

    public Long getSofar() {
        return sofar;
    }

    public void setSofar(Long sofar) {
        this.sofar = sofar;
    }

    public Long getTotalwork() {
        return totalwork;
    }

    public void setTotalwork(Long totalwork) {
        this.totalwork = totalwork;
    }

    public String getMeta_flag() {
        return meta_flag;
    }

    public void setMeta_flag(String meta_flag) {
        this.meta_flag = meta_flag;
    }

    public String getDdl_changed() {
        return ddl_changed;
    }

    public void setDdl_changed(String ddl_changed) {
        this.ddl_changed = ddl_changed;
    }

    public Long getTotalwork_timestamp() {
        return totalwork_timestamp;
    }

    public void setTotalwork_timestamp(Long totalwork_timestamp) {
        this.totalwork_timestamp = totalwork_timestamp;
    }
}
