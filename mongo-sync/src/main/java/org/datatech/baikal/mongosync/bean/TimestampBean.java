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

package org.datatech.baikal.mongosync.bean;

/**
 * Timestamp bean.
 */
public class TimestampBean {
    private Integer time_t;
    private Integer ordinal;

    public TimestampBean() {
    }

    public TimestampBean(Integer time_t, Integer ordinal) {
        this.time_t = time_t;
        this.ordinal = ordinal;
    }

    public Integer getTime_t() {
        return time_t;
    }

    public void setTime_t(Integer time_t) {
        this.time_t = time_t;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }
}
