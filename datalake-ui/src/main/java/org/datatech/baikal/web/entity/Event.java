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

public class Event implements Serializable {

    private static final long serialVersionUID = -8095522120169185748L;
    private String row_key;
    private String event_Id;
    private String parent_event_id;
    private String source_instance;
    private String source_schema;
    private String source_table;
    private String message;
    private String createTime;

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getEvent_Id() {
        return event_Id;
    }

    public void setEvent_Id(String event_Id) {
        this.event_Id = event_Id;
    }

    public String getParent_event_id() {
        return parent_event_id;
    }

    public void setParent_event_id(String parent_event_id) {
        this.parent_event_id = parent_event_id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
