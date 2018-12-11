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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.common.exp.BizException;
import org.datatech.baikal.web.common.Config;
import org.datatech.baikal.web.core.PageModel;
import org.datatech.baikal.web.entity.MonitorSchema;
import org.datatech.baikal.web.entity.bo.MetaBO;
import org.datatech.baikal.web.entity.bo.MonitorTableBO;
import org.datatech.baikal.web.entity.model.LinkMonitorStateModel;
import org.datatech.baikal.web.modules.dashboard.service.LinkMonitorService;
import org.datatech.baikal.web.modules.dashboard.service.MetaService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorSchemaService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorTableService;
import org.datatech.baikal.web.utils.CollectionsUtil;
import org.datatech.baikal.web.utils.EhcacheUtils;
import org.datatech.baikal.web.utils.MonitorSchemaRowKeyFilter;
import org.datatech.baikal.web.utils.ReflectUtils;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.web.utils.StringUtil;
import org.datatech.baikal.web.utils.TimeRangeUtil;
import org.datatech.baikal.web.utils.formatter.DateFormatter;
import org.datatech.baikal.web.utils.linkmonitor.SortByGroup;
import org.datatech.baikal.web.vo.MonitorSchemaVO;
import org.datatech.baikal.web.vo.MonitorTableVO;
import org.datatech.baikal.web.vo.SourceDataFilterVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 链接监控
 */
@Service
public class LinkMonitorServiceImpl implements LinkMonitorService {

    @Resource
    private MonitorSchemaService monitorSchemaService;

    @Resource
    private MonitorTableService monitorTableService;

    @Resource
    private MetaService configService;

    @Value("${datalake-ui.proc.time.range}")
    private String timeRange;

    /**
     * 监控图表展示 1、接收搜索框输入的表名list，若list为空,则取0时刻的所有表名 2、每一张表取出0刻同步总行数值，并取Top5的表名
     * 3、5张表取0刻到当前时刻的每一时刻的值 4、饼图：
     *
     * @param sdf source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            source_tables 数据库表集合 以逗号分隔
     *            keyword 关键字查询
     * @return 集合
     * @throws Exception 异常
     */
    public List<JSONObject> getGoldGateDashboard(SourceDataFilterVO sdf) throws Exception {
        int particleSize = sdf.getParticleSize();
        final String functionName = "org.datatech.baikal.web.modules.dashboard.service.impl.LinkMonitorService.getGoldGateDashboard(SourceDataFilter, int)";
        SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");

        Long currentTime = System.currentTimeMillis();
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(currentTime))).getTime() / 1000 + "";
        String endTime = (currentTime) / 1000 + "";

        List<String> source_tables = new ArrayList<>();
        String tenantName = SecurityUtils.getTenantName();

        // 含有零点时间戳的所有表名不重复的按照值取top5
        if (StringUtil.isNull(sdf.getSource_tables())) {
            String prefixFilter = String.join(Configuration.DELIMITER, sdf.getSource_instance(), sdf.getSource_schema(),
                    startTime);
            List<MonitorTableBO> tables = monitorTableService.listAllByFilter(prefixFilter, tenantName);

            // 以上值可以了----
            SourceDataVO sd = new SourceDataVO();
            ReflectUtils.copyAllPropertiesByName(sdf, sd, true);

            List<MetaBO> configBOs = configService.listAllByInstanceSchemaNotDelete(sdf.getSource_instance(),
                    sdf.getSource_schema());
            // 获取Config表中所有启用表
            Set<String> set = new HashSet<>();
            configBOs.forEach(x -> set.add(x.getSOURCE_TABLE()));
            Map<String, Long> comparMap = new HashMap<>();
            for (MonitorTableBO comObj : tables) {
                String tableName = comObj.getRowKey().split(Configuration.DELIMITER)[3];
                if (set.contains(tableName)) {
                    if (StringUtil.isNull(comparMap.get(tableName))) {
                        comparMap.put(tableName, new BigDecimal(comObj.getTOTAL_ROWS()).longValue());
                    } else {
                        comparMap.put(tableName, new BigDecimal(comObj.getTOTAL_ROWS())
                                .add(new BigDecimal(comparMap.get(tableName))).longValue());
                    }
                }
            }
            Map<String, Long> sortMap = CollectionsUtil.sortByValueDesc(comparMap);
            for (String str : sortMap.keySet()) {
                source_tables.add(str);
                if (source_tables.size() == 5) {
                    break;
                }
            }
        } else {
            source_tables = Arrays.asList(sdf.getSource_tables().split(","));
        }
        BizException.throwWhenFalse(source_tables.size() <= 5, "选的查询表太多，最多只能选择5个表");

        JSONArray topTableNames = new JSONArray();
        JSONArray momentArr = new JSONArray();

        Map<String, Long> oneTalbeAsyncRowsMap = new HashMap<>();
        Long allTablesAsyncRows = 0L; // topTables的总行数

        // 遍历TopTables,取出表中数据，并组织成"时刻--值"的结构,放入图表对象集合中
        List<JSONObject> allTablesMomentsValue = new ArrayList<>();
        boolean isFirst = true;
        Map<String, String> map = new HashMap<>();
        map.put("SOURCE_SCHEMA", sdf.getSource_schema());
        map.put("SOURCE_INSTANCE", sdf.getSource_instance());
        for (String tableName : source_tables) {
            String prefixRowkey = String.join(Configuration.DELIMITER, sdf.getSource_instance(),
                    sdf.getSource_schema());
            String startRowkey = String.join(Configuration.DELIMITER, prefixRowkey, startTime, tableName);
            String endRowkey = String.join(Configuration.DELIMITER, prefixRowkey, endTime, tableName);

            // 缓存处理
            final String cacheKey = String.join(Configuration.DELIMITER, functionName, sdf.getSource_instance(),
                    sdf.getSource_schema(), SecurityUtils.getTenantName());
            List<MonitorTableBO> list = EhcacheUtils.getMonitorTbaleCache(cacheKey);
            if (list == null || list.size() == 0) {
                list = monitorTableService.listAllByFilter(startRowkey, endRowkey, tenantName);
                EhcacheUtils.putMonitorTbaleCache(cacheKey, list);
            }

            // 得到该表的最终的数据集合
            // 从00:00到当前时刻的值,并根据粒度聚合计算
            Map<String, Long> momentMap = new TreeMap<>();
            momentMap.clear();
            momentMap.putAll(Config.stepTimeMap(particleSize));
            Long oneTableAsyncRows = 0L; // 1张表的同步总行数
            Calendar calendar = Calendar.getInstance();
            HashMap<String, Long> srcDataMap = new HashMap<String, Long>(1000);
            for (MonitorTableBO data : list) {
                if (!tableName.equals(data.getRowKey().split(Configuration.DELIMITER)[3])) {
                    continue;
                }
                String hourMin = DateFormatter
                        .long2HHmm(new Timestamp(new Long(data.getRowKey().split(Configuration.DELIMITER)[2]) * 1000));
                if (srcDataMap.containsKey(hourMin)) {
                    srcDataMap.put(hourMin, srcDataMap.get(hourMin) + data.getTOTAL_ROWS());
                } else {
                    srcDataMap.put(hourMin, Long.valueOf(data.getTOTAL_ROWS()));
                }
            }
            for (String key : momentMap.keySet()) {
                if ("00:00".equals(key)) {
                    momentMap.put(key, nvp(srcDataMap.get(key)));
                    continue;
                }
                calendar.setTime(HHmm.parse(key));
                calendar.add(Calendar.MINUTE, 1);
                for (int i = particleSize; i > 0; --i) {
                    calendar.add(Calendar.MINUTE, -1);
                    final String v = HHmm.format(calendar.getTime());
                    Long mse = nvp(srcDataMap.get(v));
                    if (mse != null) {
                        momentMap.put(key, momentMap.get(key) + mse);
                    }
                }
                // 1张表统计行数
                oneTableAsyncRows += momentMap.get(key);
            }

            // 保存每个表的每个时刻数据
            topTableNames.add(tableName);
            // 所有表统计行数
            allTablesAsyncRows += oneTableAsyncRows;
            oneTalbeAsyncRowsMap.put(tableName, oneTableAsyncRows);
            JSONArray allMomentValue = new JSONArray();
            for (String moment : momentMap.keySet()) {
                if (isFirst) {
                    momentArr.add(moment);
                }
                allMomentValue.add(momentMap.get(moment));
            }
            JSONObject oneTableMomentsValue = new JSONObject();
            oneTableMomentsValue.put("name", tableName);
            oneTableMomentsValue.put("data", allMomentValue);
            allTablesMomentsValue.add(oneTableMomentsValue);
            isFirst = false;
        }
        // 线形图表
        List<JSONObject> incrementalChart = new ArrayList<>();
        JSONObject lineChart = new JSONObject();
        lineChart.put("legendArr", topTableNames);
        lineChart.put("xArr", momentArr);
        lineChart.put("series", allTablesMomentsValue);
        lineChart.put("flag", "line");
        incrementalChart.add(lineChart);

        // 饼形图表,饼图图表对象
        JSONObject pieChart = new JSONObject();
        List<JSONObject> pieTableList = new ArrayList<>();
        pieChart.put("flag", "pie");
        JSONObject pieOneTable = new JSONObject();
        // 表名list
        JSONArray pieNames = new JSONArray();
        // 一张表的同步行数
        JSONObject pieTableRows;
        // 所有表的同步行数
        List<JSONObject> pieTableRowsList = new ArrayList<>();
        for (String tableName : oneTalbeAsyncRowsMap.keySet()) {
            pieNames.add(tableName);
            pieTableRows = new JSONObject();
            pieTableRows.put("name", tableName);
            pieTableRows.put("value", oneTalbeAsyncRowsMap.get(tableName));
            pieTableRowsList.add(pieTableRows);
        }
        pieChart.put("legendArr", pieNames);
        pieOneTable.put("name", "占比例");
        pieOneTable.put("data", pieTableRowsList);
        pieTableList.add(pieOneTable);
        pieChart.put("series", pieTableList);
        incrementalChart.add(pieChart);
        JSONObject pieObjectTop3 = new JSONObject();
        pieObjectTop3.put("flag", "pieTop3");
        List<JSONObject> tmpTop3 = new ArrayList<>();
        JSONObject tmpObjTop3;
        List<JSONObject> pieSeriesTop3;
        JSONObject pieSeriesObjTop3;
        JSONArray pieLegendArrTop3 = new JSONArray();
        Map<String, Long> sortMap = CollectionsUtil.sortByValueDesc(oneTalbeAsyncRowsMap);
        int i = 1;
        for (String tableName : sortMap.keySet()) {
            pieSeriesObjTop3 = new JSONObject();
            pieLegendArrTop3.add(tableName);
            pieSeriesObjTop3.put("name", tableName);
            pieSeriesObjTop3.put("value", sortMap.get(tableName));
            pieSeriesTop3 = new ArrayList<>();
            pieSeriesTop3.add(pieSeriesObjTop3);
            tmpObjTop3 = new JSONObject();
            tmpObjTop3.put("name", tableName);
            tmpObjTop3.put("data", pieSeriesTop3);
            tmpObjTop3.put("sum", allTablesAsyncRows - sortMap.get(tableName));
            tmpTop3.add(tmpObjTop3);
            if (i == 3) {
                break;
            }
            i++;
        }
        pieObjectTop3.put("legendArr", pieLegendArrTop3);
        pieObjectTop3.put("series", tmpTop3);
        incrementalChart.add(pieObjectTop3);
        return incrementalChart;
    }

    /**
     * 进程趋势
     *
     * @param sdf source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            source_tables 数据库表集合 以逗号分隔
     *            keyword 关键字查询
     * @return 对象
     * @throws Exception 异常
     */
    public JSONObject getGoldGateProcTrendDashboard(SourceDataFilterVO sdf) throws Exception {
        int particleSize = sdf.getParticleSize();
        final String functionName = "org.datatech.baikal.web.modules.dashboard.service.impl.LinkMonitorService.getGoldGateProcTrendDashboard(SourceDataFilter, int)";
        SimpleDateFormat hhmm = new SimpleDateFormat("HH:mm");
        Long currentTime = System.currentTimeMillis();
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(currentTime))).getTime() / 1000 + "";
        String endTime = (currentTime) / 1000 + "";

        String prefixRowkey = String.join(Configuration.DELIMITER, sdf.getSource_instance(), sdf.getSource_schema());
        String startRowkey = String.join(Configuration.DELIMITER, prefixRowkey, startTime);
        String endRowkey = String.join(Configuration.DELIMITER, prefixRowkey, endTime);
        Map<String, String> map = new HashMap<>();
        map.put("SOURCE_SCHEMA", sdf.getSource_schema());
        map.put("SOURCE_INSTANCE", sdf.getSource_instance());

        String tenantName = SecurityUtils.getTenantName();

        // 缓存处理
        final String cacheKey = String.join(Configuration.DELIMITER, functionName, sdf.getSource_instance(),
                sdf.getSource_schema(), tenantName);
        // 从cache中拉取数据，拉取不到则从数据库中查询
        List<MonitorSchemaVO> list = EhcacheUtils.getMonitorTbaleCache(cacheKey);
        if (list == null || list.size() == 0) {
            list = monitorSchemaService.queryAllByFilter(tenantName, startRowkey, endRowkey);
            EhcacheUtils.putMonitorTbaleCache(cacheKey, list);
        }

        // 获取所有数据
        Calendar calendar = Calendar.getInstance();
        HashMap<String, MonitorSchemaVO> srcDataMap = new HashMap<String, MonitorSchemaVO>(1000);
        for (MonitorSchemaVO data : MonitorSchemaRowKeyFilter.filterList(list)) {

            String hourMin = DateFormatter
                    .long2HHmm(new Timestamp(new Long(data.getRowKey().split(Configuration.DELIMITER)[2]) * 1000));
            srcDataMap.put(hourMin, data);
        }

        // procLag
        Map<String, Long> procLag = new TreeMap<>();
        procLag.clear();
        procLag.putAll(Config.stepTimeMap(particleSize));

        // procRowsTotal
        Map<String, Long> procRows = new TreeMap<>();
        procRows.clear();
        procRows.putAll(Config.stepTimeMap(particleSize));

        // procLag
        for (String key : procLag.keySet()) {
            /* final String[] dt = key.split(lip); */
            if ("00:00".equals(key)) {
                procLag.put(key, srcDataMap.get(key) == null ? 0L : nvp(srcDataMap.get(key).getPROC_LAG()));
                continue;
            }
            calendar.setTime(hhmm.parse(key));
            calendar.add(Calendar.MINUTE, 1); //
            for (int i = particleSize; i > 0; --i) {
                calendar.add(Calendar.MINUTE, -1);
                final String v = hhmm.format(calendar.getTime());
                MonitorSchemaVO mse = srcDataMap.get(v);
                if (mse != null) {
                    procLag.put(key, procLag.get(key) + nvp(mse.getPROC_LAG()));
                }
            }
        }
        // toatl
        for (String key : procRows.keySet()) {
            if ("00:00".equals(key)) {
                procRows.put(key, srcDataMap.get(key) == null ? 0L : nvp(srcDataMap.get(key).getPROC_ROWS()));
                continue;
            }
            calendar.setTime(hhmm.parse(key));
            calendar.add(Calendar.MINUTE, 1); //
            for (int i = particleSize; i > 0; --i) {
                calendar.add(Calendar.MINUTE, -1);
                final String v = hhmm.format(calendar.getTime());
                MonitorSchemaVO mse = srcDataMap.get(v);
                if (mse != null) {
                    procRows.put(key, procRows.get(key) + nvp(mse.getPROC_ROWS()));
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray legendArr = new JSONArray();
        JSONArray xArr = new JSONArray();
        List<JSONObject> series = new ArrayList<>();

        // lag
        JSONObject object = new JSONObject();
        JSONArray data = new JSONArray();
        for (String key : procLag.keySet()) {
            xArr.add(key);
            data.add(procLag.get(key));
        }
        object.put("name", "lag");
        object.put("data", data);
        series.add(object);

        // total
        JSONObject objTotal = new JSONObject();
        JSONArray dataTotal = new JSONArray();
        legendArr.add("lag");
        for (String key : procRows.keySet()) {
            dataTotal.add(procRows.get(key));
        }
        legendArr.add("total");
        objTotal.put("name", "total");
        objTotal.put("data", dataTotal);
        series.add(objTotal);
        jsonObject.put("legendArr", legendArr);
        jsonObject.put("xArr", xArr);
        jsonObject.put("series", series);
        return jsonObject;
    }

    /**
     * 根据一段时间内的时间戳及instance/schema等检索数据
     *
     * @param obj source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            keyword 关键字查询
     * @return 对象
     * @throws Exception 异常
     */
    public LinkMonitorStateModel getLatestState(SourceDataFilterVO obj) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        int interval = Integer.parseInt(timeRange);
        Map<String, Long> rangMillis = TimeRangeUtil.rangeMillis(interval);
        String prefixRowkey = String.join(Configuration.DELIMITER, obj.getSource_instance(), obj.getSource_schema());
        String startRowkey = String.join(Configuration.DELIMITER, prefixRowkey, rangMillis.get("startTime").toString());
        String endRowkey = String.join(Configuration.DELIMITER, prefixRowkey, rangMillis.get("endTime").toString());
        Map<String, String> map = new HashMap<>();
        map.put("SOURCE_SCHEMA", obj.getSource_schema());
        map.put("SOURCE_INSTANCE", obj.getSource_instance());
        List<MonitorSchemaVO> monitorSchemaVOs = monitorSchemaService.queryAllByFilter(tenantName, startRowkey,
                endRowkey);
        List<MonitorSchema> list = new ArrayList<>();
        for (MonitorSchemaVO m : monitorSchemaVOs) {
            MonitorSchema monitorSchemaEntity = new MonitorSchema();
            monitorSchemaEntity.setProc_checkpoint(m.getPROC_CHECKPOINT());
            monitorSchemaEntity.setProc_lag(m.getPROC_LAG());
            monitorSchemaEntity.setProc_name(m.getPROC_NAME());
            monitorSchemaEntity.setProc_rows(m.getPROC_ROWS());
            monitorSchemaEntity.setProc_status(m.getPROC_STATUS());
            monitorSchemaEntity.setProc_type(m.getPROC_TYPE());
            monitorSchemaEntity.setRow_key(m.getRowKey());
            list.add(monitorSchemaEntity);
        }
        LinkMonitorStateModel prcoStatus = SortByGroup.getPrcoStatus(list);
        return prcoStatus;
    }

    /**
     * 表数据流量
     *
     * @param obj source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            source_tables 数据库表集合 以逗号分隔
     *            keyword 关键字查询
     * @return 流量监控列表
     * @throws Exception 异常
     */
    public List<MonitorTableVO> getMonitorTableRowsList(SourceDataFilterVO obj) throws Exception {
        List<MonitorTableVO> list = new ArrayList<>();
        PageModel hpm = new PageModel(7);
        hpm.setPageIndex(1);
        getMonitorTablePageList(obj, list, hpm);
        return list;
    }

    /**
     * 获取流量列表
     *
     * @param obj  source_schema 数据库schema
     *             source_instance 数据库实例
     *             source_table 数据库表
     *             source_tables 数据库表集合 以逗号分隔
     *             keyword 关键字查询
     * @param list 返回的流量监控列表
     * @param hpm  分页对象
     * @throws Exception 异常
     */
    private void getMonitorTablePageList(SourceDataFilterVO obj, List<MonitorTableVO> list, PageModel hpm)
            throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        Long currentTime = System.currentTimeMillis();
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(currentTime))).getTime() / 1000 + "";
        String endTime = (currentTime) / 1000 + "";
        String prefix = String.join(Configuration.DELIMITER, obj.getSource_instance(), obj.getSource_schema());
        String prefixRowkey = String.join(Configuration.DELIMITER, prefix, startTime);
        String prefixEndRowkey = String.join(Configuration.DELIMITER, prefix, endTime);
        StringBuffer whereSql = new StringBuffer();
        if (!StringUtil.isNull(obj.getSource_tables())) {
            List<String> source_tables = Arrays.asList(obj.getSource_tables().split(","));
            whereSql.append(" and (");
            for (int i = 0; i < source_tables.size(); i++) {
                whereSql.append(" RowKey like '%").append(Configuration.DELIMITER).append(source_tables.get(i))
                        .append("'");
                if (i != source_tables.size() - 1) {
                    whereSql.append(" or ");
                }
            }
            whereSql.append(")");
        }
        List<MetaBO> deleteTables = configService.listDeteteTables(obj.getSource_instance(), obj.getSource_schema());
        if (deleteTables.size() > 0) {
            for (MetaBO configBO : deleteTables) {
                whereSql.append(" and RowKey not like '%").append(Configuration.DELIMITER)
                        .append(configBO.getSOURCE_TABLE()).append("'");
            }
        }

        List<MonitorTableVO> alls = monitorTableService.listPage(prefixRowkey, prefixEndRowkey, hpm, tenantName,
                whereSql.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (MonitorTableVO mt : alls) {
            String tableName = mt.getRowKey().split(Configuration.DELIMITER)[3];
            mt.setCreateTime(
                    sdf.format(new Timestamp(Long.valueOf(mt.getRowKey().split(Configuration.DELIMITER)[2]) * 1000)));
            mt.setSource_table(tableName);
            mt.setSource_instance(obj.getSource_instance());
            mt.setSource_schema(obj.getSource_schema());
            list.add(mt);
        }
    }

    /**
     * 表数据流量分页
     *
     * @param sdf source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            keyword 关键字查询
     * @param hpm 分页大小
     * @return 数据流量分页集合
     * @throws Exception 异常
     */
    @Override
    public List<MonitorTableVO> getMonitorTableListByPage(SourceDataFilterVO sdf, PageModel hpm) throws Exception {
        List<MonitorTableVO> list = new ArrayList<>();
        hpm.setPageSize(7);
        if (hpm.getPageIndex() <= 0) {
            hpm.setPageIndex(1);
        }
        getMonitorTablePageList(sdf, list, hpm);
        return list;
    }

    final Integer nvp(Integer v) {
        return v == null ? 0 : v;
    }

    final Long nvp(Long v) {
        return v == null ? 0L : v;
    }

}