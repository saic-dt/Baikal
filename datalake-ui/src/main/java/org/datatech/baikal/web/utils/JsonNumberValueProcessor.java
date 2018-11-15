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

package org.datatech.baikal.web.utils;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * JsonNumberValueProcessor处理
 */
public class JsonNumberValueProcessor implements JsonValueProcessor {

    private String format = "yyyy-MM-dd HH:mm:ss";

    public JsonNumberValueProcessor() {

    }

    public JsonNumberValueProcessor(String format) {
        this.format = format;
    }

    @Override
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        String[] obj = {};
        if (value instanceof Long[]) {

        }
        return obj;
    }

    @Override
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        if (value == null) {
            return "null";
        }
        // return value == null ? null : value.toString();
        if (value == "null") {
            return "null";
        }
        return value.toString();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
