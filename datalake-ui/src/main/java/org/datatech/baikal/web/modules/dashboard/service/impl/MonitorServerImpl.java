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

package org.datatech.baikal.web.modules.dashboard.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.web.common.Config;
import org.datatech.baikal.web.common.Enums;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.EventService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorSchemaService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorService;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.utils.EhcacheUtils;
import org.datatech.baikal.web.utils.MonitorSchemaRowKeyFilter;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.util.StringUtil;
import org.datatech.baikal.web.utils.formatter.DateFormatter;
import org.datatech.baikal.web.vo.EventVO;
import org.datatech.baikal.web.vo.MonitorSchemaVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Dashboard监控页面
 */
@Service
public class MonitorServerImpl implements MonitorService {

    @Resource
    private MonitorSchemaService monitorSchemaService;

    @Resource
    private SourceJdbcService sourceJdbcService;

    @Resource
    private EventService eventService;

    /**
     * 首页获取大图条信息
     *
     * @return json对象
     * @throws Exception 异常
     */
    @Override
    public JSONObject getSchemaBigDashboard(Integer particleSize) throws Exception {
        // 求当前的图表的
        JSONArray xArr = new JSONArray();
        JSONArray legendArr = new JSONArray();
        List<JSONObject> series = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        // 获取t_datasource中所有oracle记录 获取数据库url和驱动 rowkey为DOL_INSTANCE
        List<SourceJdbcBO> oracleList = sourceJdbcService.listInstanceSchema();
        String sourceSchema = "";
        String sourceInstance = "";
        Boolean isFist = true;
        long currentTime = System.currentTimeMillis();
        for (SourceJdbcBO sourceJdbcBO : oracleList) {
            sourceSchema = sourceJdbcBO.getSCHEMA_NAME();
            sourceInstance = sourceJdbcBO.getINSTANCE_NAME();
            Map<String, String> map = new HashMap<>();
            map.put("SOURCE_SCHEMA", sourceSchema);
            map.put("SOURCE_INSTANCE", sourceInstance);
            // 当前周期的数据
            legendArr.add(String.join(Config.BACKSLASH, sourceInstance, sourceSchema));
            String startTime = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(currentTime))).getTime() / 1000 + "";
            String endTime = currentTime / 1000 + "";
            String prefixRowkey = String.join(Configuration.DELIMITER, sourceInstance, sourceSchema);
            String startRowkey = String.join(Configuration.DELIMITER, prefixRowkey, startTime);
            String endRowkey = String.join(Configuration.DELIMITER, prefixRowkey, endTime);
            // 获取MonitorSchema表中的内容
            Map<String, Long> dataMap = getStringLongMap(map, startRowkey, endRowkey, particleSize);
            JSONArray currentData = new JSONArray();
            for (String key : dataMap.keySet()) {
                if (isFist) {
                    xArr.add(key);
                }
                currentData.add(dataMap.get(key));
            }
            JSONObject currentObj = new JSONObject();
            currentObj.put("name", String.join(Config.BACKSLASH, sourceInstance, sourceSchema));
            currentObj.put("data", currentData);
            series.add(currentObj);
            isFist = false;
        }
        jsonObject.put("legendArr", legendArr);
        jsonObject.put("xArr", xArr);
        jsonObject.put("series", series);
        return jsonObject;
    }

    /**
     * 获取dashboard下方数据源的小图表
     *
     * @param paramList 数据源集合
     * @return json对象集合
     * @throws Exception 异常
     */
    @Override
    public List<JSONObject> getSchemaSmallDashboard(List<SourceDataVO> paramList, Integer particleSize)
            throws Exception {
        List<JSONObject> reList = new ArrayList<>();
        JSONObject jsonObject = null;
        String tenantName = SecurityUtils.getTenantName();
        long currentTime = System.currentTimeMillis();
        for (SourceDataVO sd : paramList) {
            jsonObject = new JSONObject();
            jsonObject.put("source_schema", sd.getSource_schema());
            jsonObject.put("source_instance", sd.getSource_instance());
            Map<String, String> map = new HashMap<>();
            map.put("SOURCE_SCHEMA", sd.getSource_schema());
            map.put("SOURCE_INSTANCE", sd.getSource_instance());

            SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
            String startRowkey = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(currentTime))).getTime() + "";
            String endRowkey = (currentTime) + "";

            //todo 一般禁止使用map作为参数传值 有待优化
            // 查询事件信息
            List<EventVO> eventList = eventService.queryAllByFilter(startRowkey, endRowkey, map, tenantName);
            jsonObject.put("event_number", eventList.size());
            jsonObject.put("event_elements", eventList);

            map.put("META_FLAG", Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value());// 启用的数据
            // 获取config表中的内容
            List<JSONObject> configList = new ArrayList<>();
            jsonObject.put("sync_table_count", configList.size());
            // 求当前的图表的
            JSONArray xArr = new JSONArray();
            JSONArray legendArr = new JSONArray();
            JSONObject object = new JSONObject();
            legendArr.add("上一周期");
            long todaySeconds = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(currentTime))).getTime() / 1000;
            // 上一周期的数据
            String startTime = todaySeconds - 24 * 60 * 60 + "";
            String prefixRowkey = String.join(Configuration.DELIMITER, sd.getSource_instance(), sd.getSource_schema());
            startRowkey = String.join(Configuration.DELIMITER, prefixRowkey, startTime);
            endRowkey = String.join(Configuration.DELIMITER, prefixRowkey, todaySeconds + "");

            map.put("before", "true");
            // 获取MonitorSchema表中的内容
            Map<String, Long> dataMap = getStringLongMap(map, startRowkey, endRowkey, particleSize);
            List<JSONObject> series = new ArrayList<>();
            JSONArray data = new JSONArray();
            for (String key : dataMap.keySet()) {
                xArr.add(key);
                data.add(dataMap.get(key));
            }
            object.put("name", "上一周期");
            object.put("data", data);
            series.add(object);

            map.remove("before");
            // 当前周期的数据
            legendArr.add("当前周期");
            String endTime = currentTime / 1000 + "";
            startRowkey = String.join(Configuration.DELIMITER, prefixRowkey, todaySeconds + "");
            endRowkey = String.join(Configuration.DELIMITER, prefixRowkey, endTime);
            dataMap = getStringLongMap(map, startRowkey, endRowkey, particleSize);
            JSONArray currentData = new JSONArray();
            for (String key : dataMap.keySet()) {
                currentData.add(dataMap.get(key));
            }
            JSONObject currentObj = new JSONObject();
            currentObj.put("name", "当前周期");
            currentObj.put("data", currentData);
            series.add(currentObj);

            jsonObject.put("legendArr", legendArr);
            jsonObject.put("xArr", xArr);
            jsonObject.put("series", series);
            reList.add(jsonObject);
        }
        return reList;
    }

    /**
     * 统计监控实例的信息放入map中
     *
     * @param map         参数集合
     *                    SOURCE_SCHEMA 数据库schema
     *                    SOURCE_INSTANCE 数据库实例
     * @param startRowkey 起始查询条件
     * @param endRowkey   截止查询条件
     * @return map集合包含统计数据
     * @throws Exception 异常
     */
    private Map<String, Long> getStringLongMap(Map<String, String> map, String startRowkey, String endRowkey, int kld)
            throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        final SimpleDateFormat hhmm = new SimpleDateFormat("HH:mm"); // 不安全进程
        // 在方法内
        // 获取MonitorSchema表中的内容
        Calendar calendar = Calendar.getInstance();
        final String functionName = "DashboardServiceImpl.getStringLongMap";
        final String[] dts = startRowkey.split(Configuration.DELIMITER);
        final String instance = dts[0];
        final String schema = dts[1];
        final String cacheKey = String.join(Configuration.DELIMITER, functionName, instance, schema, startRowkey,
                tenantName);
        List<MonitorSchemaVO> msList = EhcacheUtils.getMonitorSchemaCache(cacheKey);
        if (msList == null) {
            if (StringUtil.isNull(map.get("before"))) {
                msList = monitorSchemaService.queryAllByFilter(tenantName, startRowkey, endRowkey);
            } else {
                msList = monitorSchemaService.queryAllByFilterYesterday(tenantName, startRowkey, endRowkey);
            }
            EhcacheUtils.putMonitorSchemaCache(cacheKey, msList);
        }
        HashMap<String, Long> srcDataMap = new HashMap<String, Long>(256);
        Map<String, Long> dataMap = new TreeMap<>();
        dataMap.putAll(Config.stepTimeMap(kld));
        List<MonitorSchemaVO> dlt = MonitorSchemaRowKeyFilter.filterList(msList);
        for (MonitorSchemaVO data : dlt) {
            String hourMin = DateFormatter
                    .long2HHmm(new Timestamp(Long.valueOf(data.getRowKey().split(Configuration.DELIMITER)[2]) * 1000));
            if (srcDataMap.containsKey(hourMin)) {
                srcDataMap.put(hourMin, Long.valueOf(data.getPROC_ROWS()) + srcDataMap.get(hourMin));
            } else {
                srcDataMap.put(hourMin, Long.valueOf(data.getPROC_ROWS()));
            }
        }
        for (String key : dataMap.keySet()) {
            if (null == key || "".equals(key)) {
                continue;
            }
            if ("00:00".equals(key)) {
                dataMap.put(key, nvp(srcDataMap.get(key)));
                continue;
            }
            final Date dt = hhmm.parse(key);
            calendar.setTime(dt);
            calendar.add(Calendar.MINUTE, 1);
            for (int i = kld; i > 0; --i) {
                calendar.add(Calendar.MINUTE, -1);
                final String v = hhmm.format(calendar.getTime());
                Long mse = nvp(srcDataMap.get(v));
                if (mse != null) {
                    dataMap.put(key, dataMap.get(key) + mse);
                }
            }
        }
        return dataMap;
    }

    /**
     * Long 类型空判
     *
     * @param v 长整形
     * @return 参数为空返回0L 不为空返回参数值
     */
    final Long nvp(Long v) {
        return v == null ? 0L : v;
    }
}
