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

package org.datatech.baikal.mongosync.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing methods to process json.
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Convert json to object.
     * @param jsonData json string
     * @param beanType object type
     * @param <T> generic type
     * @return object of specified type
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }
        return null;
    }

    /**
     * Get object from json string.
     * 
     * @param jsonString json string
     * @param pojoCalss class
     * @return Object object
     */
    public static Object getObject4JsonString(String jsonString, Class pojoCalss) {
        Object pojo;
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        pojo = JSONObject.toBean(jsonObject, pojoCalss);
        return pojo;
    }

    /**
     * Convert object to json string
     * 
     * @param javaObj object
     * @return String json string
     */
    public static String getJsonString4JavaPOJO(Object javaObj) {

        JSONObject json;
        json = JSONObject.fromObject(javaObj);
        return json.toString();

    }
}
