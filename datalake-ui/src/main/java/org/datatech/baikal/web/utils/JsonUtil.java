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

/**
 * <p>Title: JsonUtil.java</p>
 * <p>Package com.Para.util</p>
 * <p>Description: JSON工具类</p>
 */
package org.datatech.baikal.web.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.JSONUtils;

/**
 * <p>
 * 处理json的工具类，负责json数据转换成java对象和java对象转换成json
 * </p>
 */
public class JsonUtil {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>
     * Title: pojoToJson
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param data 目标队形
     * @return 字符串
     */
    public static String objectToJson(Object data) {
        try {
            String string = MAPPER.writeValueAsString(data);
            return string;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData
     *            json数据
     * @param beanType
     *            对象中的object类型
     * @param <T> 泛型
     * @return 泛型实体
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>
     * Title: jsonToList
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param jsonData json字符串
     * @param beanType class类
     * @param <T> 泛型
     * @return List泛型集合
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从一个JSON 对象字符格式中得到一个java对象
     * 
     * @param jsonString json字符串
     * @param pojoCalss class类
     * @return Object 指定的对象
     */
    public static Object getObject4JsonString(String jsonString, Class pojoCalss) {
        Object pojo;
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        pojo = JSONObject.toBean(jsonObject, pojoCalss);
        return pojo;
    }

    /**
     * 从json HASH表达式中获取一个map，改map支持嵌套功能
     * 
     * @param jsonString json字符串
     * @return Map map对象
     */
    public static Map getMap4Json(String jsonString) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Iterator keyIter = jsonObject.keys();
        String key;
        Object value;
        Map valueMap = new HashMap();

        while (keyIter.hasNext()) {
            key = (String) keyIter.next();
            value = jsonObject.get(key);
            valueMap.put(key, value);
        }

        return valueMap;
    }

    /**
     * 从json HASH表达式中获取一个map，改List支持嵌套功能
     *
     * @param jsonString json字符串
     * @return List 集合
     */
    public static List getList4Json(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        JSONObject jsonObject;
        List list = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
            jsonObject = jsonArray.getJSONObject(i);
            Iterator keyIter = jsonObject.keys();
            String key;
            JSONObject jsonObj = new JSONObject();
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                jsonObj.put(key, jsonObject.get(key));
            }
            list.add(jsonObj);
        }
        return list;
    }

    /**
     * 从json数组中得到相应java数组
     * 
     * @param jsonString json字符串
     * @return Object[] 数组
     */
    public static Object[] getObjectArray4Json(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return jsonArray.toArray();

    }

    /**
     * 从json对象集合表达式中得到一个java对象列表
     * 
     * @param jsonString json字符串
     * @param pojoClass class类
     * @return List 集合
     */
    public static List getList4Json(String jsonString, Class pojoClass) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        JSONObject jsonObject;
        Object pojoValue;

        List list = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
            jsonObject = jsonArray.getJSONObject(i);
            pojoValue = JSONObject.toBean(jsonObject, pojoClass);
            list.add(pojoValue);

        }
        return list;

    }

    /**
     * 从json对象集合表达式中得到一个java对象列表
     *
     * @param jsonString json字符串
     * @param pojoClass class类
     * @param map map内置集合对象
     * @return List 集合
     */
    public static List getList4Json(String jsonString, Class pojoClass, Map<String, Class> map) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        JSONObject jsonObject;
        Object pojoValue;

        List list = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
            jsonObject = jsonArray.getJSONObject(i);
            pojoValue = JSONObject.toBean(jsonObject, pojoClass, map);
            list.add(pojoValue);

        }
        return list;

    }

    /**
     * 从json数组中解析出java字符串数组
     * 
     * @param jsonString json字符串
     * @return String[] 字符串数组
     */
    public static String[] getStringArray4Json(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        String[] stringArray = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            stringArray[i] = jsonArray.getString(i);

        }

        return stringArray;
    }

    /**
     * 从json数组中解析出javaLong型对象数组
     * 
     * @param jsonString json字符串
     * @return Long[] long数组
     */
    public static Long[] getLongArray4Json(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Long[] longArray = new Long[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            longArray[i] = jsonArray.getLong(i);

        }
        return longArray;
    }

    /**
     * 从json数组中解析出java Integer型对象数组
     * 
     * @param jsonString json字符串
     * @return Integer[] Integer数组
     */
    public static Integer[] getIntegerArray4Json(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Integer[] integerArray = new Integer[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            integerArray[i] = jsonArray.getInt(i);

        }
        return integerArray;
    }

    /**
     * 从json数组中解析出java Date 型对象数组，使用本方法必须保证
     * 
     * @param jsonString json字符串
     * @param dataFormat 时间格式
     * @return Date[] 时间数组
     */
    public static Date[] getDateArray4Json(String jsonString, String dataFormat) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Date[] dateArray = new Date[jsonArray.size()];
        String dateString;
        Date date;

        for (int i = 0; i < jsonArray.size(); i++) {
            dateString = jsonArray.getString(i);
            date = DateUtil.parseDate(dateString, dataFormat);
            dateArray[i] = date;

        }
        return dateArray;
    }

    /**
     * 从json数组中解析出java Integer型对象数组
     * 
     * @param jsonString json字符串
     * @return Double[] 浮点型数组
     */
    public static Double[] getDoubleArray4Json(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Double[] doubleArray = new Double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            doubleArray[i] = jsonArray.getDouble(i);

        }
        return doubleArray;
    }

    /**
     * 将java对象转换成json字符串
     * 
     * @param javaObj 对象
     * @return String json字符串
     */
    public static String getJsonString4JavaPOJO(Object javaObj) {

        JSONObject json;
        json = JSONObject.fromObject(javaObj);
        return json.toString();

    }

    /**
     * JSON 时间解析器具
     * 
     * @param datePattern 时间格式
     * @return JsonConfig 对象
     */
    public static JsonConfig configJson(String datePattern) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] { "" });
        jsonConfig.setIgnoreDefaultExcludes(false);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(datePattern));

        return jsonConfig;
    }

    /**
     * 除去不想生成的字段（特别适合去掉级联的对象）+时间转换
     * 
     * @param excludes
     *            除去不想生成的字段
     * @param datePattern 时间格式
     * @return JsonConfig 对象
     */
    public static JsonConfig configJson(String[] excludes, String datePattern) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(excludes);
        jsonConfig.setIgnoreDefaultExcludes(true);
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(datePattern));

        return jsonConfig;
    }

}
