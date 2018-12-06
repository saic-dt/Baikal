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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.conf.BaseTask;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.common.conf.TaskType;
import org.datatech.baikal.web.common.exp.BizException;
import org.datatech.baikal.web.entity.bo.MetaBO;
import org.datatech.baikal.web.entity.bo.MonitorTableBO;
import org.datatech.baikal.web.entity.bo.OperationLogBO;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.DashboardService;
import org.datatech.baikal.web.modules.dashboard.service.EventService;
import org.datatech.baikal.web.modules.dashboard.service.MetaService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorTableService;
import org.datatech.baikal.web.modules.dashboard.service.OperationLogService;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.external.TaskAsync;
import org.datatech.baikal.web.external.ZkHandler;
import org.datatech.baikal.web.utils.DataBaseUtil;
import org.datatech.baikal.web.utils.DateUtil;
import org.datatech.baikal.web.utils.EhcacheUtils;
import org.datatech.baikal.web.utils.JsonUtil;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.web.utils.StringUtil;
import org.datatech.baikal.web.utils.TaskTool.TaskTool;
import org.datatech.baikal.web.utils.formatter.DateFormatter;
import org.datatech.baikal.web.vo.EventVO;
import org.datatech.baikal.web.vo.SourceDataFilterVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * <p>
 * 图表展示 实现类
 * <p>
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private ZkHandler handler;

    @Resource
    private TaskAsync taskAsyncService;

    @Resource
    private EventService eventService;

    @Resource
    private SourceJdbcService sourceJdbcService;

    @Resource
    private MetaService configService;

    @Resource
    private MonitorTableService monitorTableService;

    @Resource
    private OperationLogService operationLogService;

    /**
     * 获取Dashboard下方所有的schema的列表
     *
     * @return schema的集合列表
     * @throws IOException 异常
     */
    @Override
    public List<JSONObject> getSchemaList() throws Exception {
        List<JSONObject> rtList = new ArrayList<>();
        String tenantName = SecurityUtils.getTenantName();
        List<SourceJdbcBO> dbList = sourceJdbcService.list();
        for (SourceJdbcBO jdbcEntity : dbList) {
            getDbCount(jdbcEntity, rtList, tenantName);
        }
        return rtList;
    }

    /**
     * 首页获取数据库表同步记录数和源数据库总数
     *
     * @param sourceJdbcBO 数据源对象
     * @param rtList       返回的对象
     * @param tenantName   租户名称
     */
    public void getDbCount(SourceJdbcBO sourceJdbcBO, List<JSONObject> rtList, String tenantName) {
        JSONObject jsonObject = new JSONObject();
        String db_typ = sourceJdbcBO.getDB_TYPE();
        String sourceSchema = sourceJdbcBO.getSCHEMA_NAME();
        String sourceInstance = sourceJdbcBO.getINSTANCE_NAME();
        sourceJdbcBO.setTenantName(tenantName);
        jsonObject.put("source_schema", sourceSchema);
        jsonObject.put("source_instance", sourceInstance);
        jsonObject.put("db_typ", db_typ);
        jsonObject.put("table_count", TaskTool.getDbTableCount(sourceJdbcBO, false));
        int sync_table_count = TaskTool.getTableCountSync(sourceJdbcBO);
        jsonObject.put("sync_table_count", sync_table_count);
        rtList.add(jsonObject);
    }

    /**
     * 获取所有schema的事件列表
     *
     * @return 事件列表
     */
    @Override
    public List<EventVO> getSchemaEventList() {
        List<EventVO> reList = new ArrayList<>();
        try {
            Long currentTime = System.currentTimeMillis();
            SimpleDateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
            Long startRowkey = yyyy_mm_dd.parse(yyyy_mm_dd.format(new Timestamp(System.currentTimeMillis()))).getTime();
            Long endRowkey = (currentTime);
            reList = eventService.getListByRowKey(startRowkey, endRowkey);
            for (EventVO ee : reList) {
                ee.setCreateTime(DateFormatter.long2MMcnDDcnHHmm(new Timestamp(Long.valueOf(ee.getEvent_id()))));
            }
            Collections.reverse(reList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reList;
    }

    /**
     * 删除Config表中的数据 逻辑删除 将meta_flag字段修改为1 同事发送指令到zk 删除hive表
     *
     * @param rowkey config主键
     * @throws IOException 异常
     */
    @Override
    public void delete(String rowkey) throws Exception {
        final String tenantName = SecurityUtils.getTenantName();
        BizException.throwWhenFalse(!StringUtil.isNull(rowkey), "传入的参数不对");
        String[] arr = rowkey.split(Config.DELIMITER);
        SourceDataVO sourceData = new SourceDataVO();
        sourceData.setSource_instance(arr[0]);
        sourceData.setSource_schema(arr[1]);
        sourceData.setSource_table(arr[2]);

        MetaBO configBO = configService.get(sourceData);
        BizException.throwWhenNull(configBO, "删除的数据在已同步表中不存在");
        if (!StringUtil.isNull(configBO.getMETA_FLAG())) {
            BizException.throwWhenTrue(Enums.MetaFlag.META_FLAG_DELETE.value().equals(configBO.getMETA_FLAG()),
                    "数据已经删除，不能重复删除,如未同步中不存在，请刷新页面");
        }
        configBO.setMETA_FLAG(Enums.MetaFlag.META_FLAG_DELETE.value());
        configService.save(JsonUtil.getJsonString4JavaPOJO(configBO), tenantName, sourceData);
        queueTask(sourceData, TaskType.DELETE_TABLE_TASK, tenantName, "");
    }

    /**
     * 获取数据源未同步表列表
     *
     * @param sdf source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            keyword 关键字查询
     * @return 集合
     */
    @Override
    public List<JSONObject> getSchemaTableList(SourceDataFilterVO sdf) {
        String tenantName = SecurityUtils.getTenantName();
        List<JSONObject> reList = new ArrayList<>();
        try {
            SourceJdbcBO sourceJdbcBO = sourceJdbcService.getSourceJdbcByInstanceSchema(sdf.getSource_schema(),
                    sdf.getSource_instance(), tenantName);
            sourceJdbcBO.setTableName(sdf.getKeyword());
            List<String> tables = DataBaseUtil.getTableName(sourceJdbcBO);
            Set<String> set = new HashSet<>(tables);
            List<JSONObject> unsyncList = new ArrayList<>();
            List<JSONObject> unsyncTmpList = new ArrayList<>();
            // hbase获取所有已同步表中的表名
            List<MetaBO> list = configService.listAllByInstanceSchemaNotDelete(sdf.getSource_instance(),
                    sdf.getSource_schema());
            JSONObject obj = null;
            for (MetaBO object : list) {
                final String mateFlag = object.getMETA_FLAG();
                if (mateFlag == null) {
                    continue;
                }
                if (set.contains(object.getSOURCE_TABLE())) {
                    obj = new JSONObject();
                    obj.put("row_key", String.join(Config.DELIMITER, sdf.getSource_instance(), sdf.getSource_schema(),
                            object.getSOURCE_TABLE()));
                    obj.put("source_table", object.getSOURCE_TABLE());
                    if (StringUtil.isNull(object.getSOFAR()) || StringUtil.isNull(object.getTOTAL_WORK())) {
                        if (unsyncTable(mateFlag)) {
                            unsyncTmpList.add(obj);
                        }
                    } else if (object.getSOFAR() >= object.getTOTAL_WORK() && (Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH
                            .value().equals(object.getMETA_FLAG())
                            || Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION.value().equals(object.getMETA_FLAG()))) {
                    } else {
                        if (unsyncTable(mateFlag)) {
                            unsyncTmpList.add(obj);
                        }
                    }
                    set.remove(object.getSOURCE_TABLE());
                }
            }

            for (JSONObject un : unsyncTmpList) {
                unsyncList.add(syncData(un.get("row_key").toString()));
            }
            JSONObject sync = new JSONObject();
            if (set.size() > 0) {
                for (String str : set) {
                    obj = new JSONObject();
                    obj.put("source_table", str);
                    unsyncList.add(obj);
                }
            }
            sync.put("flag", "unsync");
            List<JSONObject> subList = new ArrayList<>();
            if (StringUtil.isNotEmpty(sdf.getStart())) {
                if (unsyncList.size() > 0) {
                    subList = paging(unsyncList, sdf.getRows(), sdf.getStart());
                }
            }
            sync.put("elements", subList);
            sync.put("rowCount", unsyncList.size());
            reList.add(sync);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reList;
    }

    /**
     * 用于判断是否属于未同步列表
     *
     * @param mateflg meta表状态
     * @return boolean
     */
    private Boolean unsyncTable(String mateflg) {
        if (mateflg == null) {
            return true;
        }
        if (Enums.MetaFlag.sync.contains(mateflg)) {
            return false;
        }
        return true;
    }

    /**
     * 获取数据源实例的事件列表
     *
     * @param sd source_schema 数据库schema
     *           source_instance 数据库实例
     * @return 事件集合
     */
    @Override
    public List<EventVO> getSchemaEventDetailList(SourceDataVO sd) {
        List<EventVO> reList = new ArrayList<>();
        try {
            Long currentTime = System.currentTimeMillis();
            SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
            String startRowkey = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(System.currentTimeMillis()))).getTime()
                    + "";
            String endRowkey = (currentTime) + "";
            String tenantName = SecurityUtils.getTenantName();
            reList = eventService.getListByRowKeyFileter(startRowkey, endRowkey, sd, tenantName);
            for (EventVO ee : reList) {
                ee.setCreateTime(DateFormatter.long2MMcnDDcnHHmm(new Timestamp(Long.valueOf(ee.getEvent_id()))));
            }
            Collections.reverse(reList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reList;
    }

    /**
     * 同步的方法
     *
     * @param sourceData source_schema 数据库schema
     *                   source_instance 数据库实例
     *                   source_table 数据库表
     * @return 同步表的rowkey
     * @throws Exception 异常
     */
    @Override
    public String startSync(SourceDataVO sourceData) throws Exception {
        final String tenantName = SecurityUtils.getTenantName();
        String row_key = String.join(Config.DELIMITER, sourceData.getSource_instance(), sourceData.getSource_schema(),
                sourceData.getSource_table());

        MetaBO configBO = configService.get(sourceData);
        if (configBO != null) {
            if (!StringUtil.isNull(configBO.getMETA_FLAG())) {
                if (Enums.MetaFlag.META_FLAG_UI_FULLDUMP.value().equals(configBO.getMETA_FLAG())) {
                    return row_key;
                }
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("META_FLAG", Enums.MetaFlag.META_FLAG_UI_FULLDUMP.value());
        obj.put("DDL_CHANGED", Enums.DdlChanged.DDLCHANGED_DISABLED.value());
        configService.save(obj.toString(), tenantName, sourceData);

        // 给zk 发送指令
        queueTask(sourceData, TaskType.MAIN_TASK, tenantName, "");
        // asyncService.checkTimeOut(row_key);
        taskAsyncService.syncSimulator(sourceData);
        // 加入操作日志
        saveOperationLog(sourceData, tenantName);
        return row_key;
    }

    /**
     * 保存操作日志
     *
     * @param sourceData
     * @param tenantName
     */
    private void saveOperationLog(SourceDataVO sourceData, String tenantName) {
        // 加入操作日志
        try {
            SourceJdbcBO sourceJdbcBO = sourceJdbcService.getSourceJdbcByInstanceSchema(sourceData.getSource_schema(),
                    sourceData.getSource_instance(), tenantName);
            OperationLogBO operationLogBO = new OperationLogBO();
            operationLogBO.setDB_TYPE(sourceJdbcBO.getDB_TYPE());
            operationLogBO.setSOURCE_INSTANCE(sourceData.getSource_instance());
            operationLogBO.setSOURCE_SCHEMA(sourceData.getSource_schema());
            operationLogBO.setSOURCE_TABLE(sourceData.getSource_table());
            operationLogBO.setOPTERATE_TYPE(Enums.OperationLogType.OPERATIONLOGTYPE_SYNC.value());
            operationLogBO.setTENANT_NAME(tenantName);
            operationLogBO.setUSER_NAME(SecurityUtils.getUserName());
            operationLogBO.setOPERATION_TIME(System.currentTimeMillis());
            operationLogBO
                    .setMESSAGE("用户【" + SecurityUtils.getUserName() + "】同步了表【" + sourceData.getSource_table() + "】");
            operationLogService.save(operationLogBO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 发送指令给zk 如果IP配置不存在按原方案写入节点
     *
     * @param sourceData
     * @throws Exception
     */
    private void queueTask(SourceDataVO sourceData, TaskType taskType, String tenantName, String tablePrefix)
            throws Exception {
        queueTask(sourceData, taskType, tenantName, tablePrefix, "");
    }

    /**
     * 插入zk队列
     *
     * @param sourceData  源数据信息
     * @param taskType    taskType
     * @param tenantName  租户
     * @param tablePrefix 表前缀
     * @param sandBoxName 沙盒名称
     * @throws Exception 异常
     */
    private void queueTask(SourceDataVO sourceData, TaskType taskType, String tenantName, String tablePrefix,
            String sandBoxName) throws Exception {
        final String instance = sourceData.getSource_instance();
        final String schema = sourceData.getSource_schema();
        BaseTask mainTask = new BaseTask();
        mainTask.setTaskType(taskType);
        mainTask.setInstanceName(instance);
        mainTask.setSchemaName(schema);
        mainTask.setTableName(sourceData.getSource_table());
        mainTask.setTablePrefix(tablePrefix);
        mainTask.setSandBoxName(sandBoxName);
        SourceJdbcBO sourceJdbcBO = sourceJdbcService.getSourceJdbcByInstanceSchema(schema, instance, tenantName);
        if (StringUtil.isNull(sourceJdbcBO.getRMT_IP())) {
            handler.queueTask(mainTask);
        } else {
            handler.queueTask(
                    Config.PATH_QUEUE + Config.BACKSLASH + tenantName + Config.TASK_PREFIX + sourceJdbcBO.getRMT_IP(),
                    mainTask);
        }
    }

    /**
     * 重新同步
     *
     * @param sourceData source_schema 数据库schema
     *                   source_instance 数据库实例
     *                   source_table 数据库表
     * @return rowkey
     * @throws Exception 异常
     */
    @Override
    public String repStartSync(SourceDataVO sourceData) throws Exception {
        final String tenantName = SecurityUtils.getTenantName();
        String rowkey = String.join(Config.DELIMITER, sourceData.getSource_instance(), sourceData.getSource_schema(),
                sourceData.getSource_table());
        MetaBO configBO = configService.get(sourceData);
        BizException.throwWhenFalse(!StringUtil.isNull(configBO), "数据记录不存在，不能重新同步");
        BizException.throwWhenFalse(ist(configBO.getMETA_FLAG(), configBO.getDDL_CHANGED()),
                "重新同步状态不正确(必须为fulldump完成)");
        configBO.setMETA_FLAG(Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION.value());
        configBO.setDDL_CHANGED(Enums.DdlChanged.DDLCHANGED_DISABLED.value());
        configService.save(JsonUtil.getJsonString4JavaPOJO(configBO), tenantName, sourceData);
        // 直接成为已同步记录
        queueTask(sourceData, TaskType.REFRESH_TABLE_TASK, tenantName, configBO.getPREFIX());
        return rowkey;
    }

    /**
     * 判断 metaFlag 和ddl状态是否在重新同步和完成状态中
     *
     * @param mateflg 状态
     * @param ddl     ddl状态
     * @return boolean
     */
    private Boolean ist(String mateflg, String ddl) {
        if (Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value().equals(mateflg)) {
            return true;
        } else if (Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION.value().equals(mateflg) && "2".equals(ddl)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据实例获取表信息
     *
     * @param sourceData source_schema 数据库schema
     *                   source_instance 数据库实例
     *                   source_table 数据库表
     *                   keyword 关键字查询
     * @return set集合
     * @throws IOException 异常
     */
    @Override
    public Set<String> getTableLSchemaInstance(SourceDataVO sourceData) throws Exception {
        List<MetaBO> list = configService.listAllByInstanceSchemaNotDelete(sourceData.getSource_instance(),
                sourceData.getSource_schema());
        Set<String> set = new HashSet<>();
        list.forEach(x -> set.add(x.getSOURCE_TABLE()));
        return set;
    }

    /**
     * 获取已同步列表
     *
     * @param sdf source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            keyword 关键字查询
     * @return 集合
     */
    @Override
    public List<JSONObject> getSchemaSyncTableList(SourceDataFilterVO sdf) {
        List<JSONObject> syncList = new ArrayList<>();
        try {
            // hbase获取所有已同步表中的表名
            List<MetaBO> list = configService.listAllByInstanceSchemaNotDelete(sdf.getSource_instance(),
                    sdf.getSource_schema());
            JSONObject obj = null;
            final String kw = sdf.getKeyword();
            for (MetaBO object : list) {
                if (object.getMETA_FLAG() == null) {
                    continue;
                }
                final String sourceTable = object.getSOURCE_TABLE();
                if (!DataBaseUtil.stringMatch(kw, sourceTable)) {
                    continue;
                }
                obj = new JSONObject();
                obj.put("row_key", String.join(Config.DELIMITER, object.getSOURCE_INSTANCE(), object.getSOURCE_SCHEMA(),
                        object.getSOURCE_TABLE()));
                obj.put("source_table", sourceTable);
                obj.put("meta_flag", object.getMETA_FLAG());
                if (!unsyncTable(object.getMETA_FLAG())) {
                    syncList.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        syncList = paging(syncList, sdf.getRows(), sdf.getStart());
        return syncList;
    }

    /**
     * 同步显示进度条和剩余信息
     *
     * @param rowkey 同步的主键
     * @return 同步的信息 包含进度条
     * @throws Exception 异常
     */
    @Override
    public JSONObject syncData(String rowkey) throws Exception {
        JSONObject reObj = new JSONObject();
        SourceDataVO sourceDataVO = new SourceDataVO();
        String[] arr = rowkey.split(Config.DELIMITER);
        sourceDataVO.setSource_instance(arr[0]);
        sourceDataVO.setSource_schema(arr[1]);
        sourceDataVO.setSource_table(arr[2]);
        MetaBO configBO = configService.get(sourceDataVO);
        if (StringUtil.isNotEmpty(configBO)) {
            if (StringUtil.isNull(configBO.getMETA_FLAG())
                    || Enums.MetaFlag.META_FLAG_MAINTASK_FULLDUMP.value().equals(configBO.getMETA_FLAG())) {
                String sourceTable = configBO.getSOURCE_TABLE();
                if (StringUtil.isNull(configBO.getTOTAL_WORK()) || StringUtil.isNull(configBO.getSOFAR())
                        || StringUtil.isNull(configBO.getTABLE_VERSION())) {
                    reObj.put("row_key", rowkey);
                    reObj.put("percent", "0.00%");
                    reObj.put("source_table", sourceTable);
                    reObj.put("residue_seconds", "[--:--]");
                    reObj.put("status", "正在构建表");
                } else {
                    long sofar = new Long(configBO.getSOFAR());
                    long totalwork = new Long(configBO.getTOTAL_WORK());
                    long timestamp = new Long(configBO.getTABLE_VERSION());
                    long currentTimestamp = System.currentTimeMillis();
                    reObj.put("row_key", rowkey);
                    // 计算剩余时间
                    if (sofar <= totalwork) {
                        BigDecimal minRow = new BigDecimal(totalwork).subtract(new BigDecimal(sofar));
                        long time = (currentTimestamp - timestamp) / 1000;
                        Double divide = new BigDecimal(sofar)
                                .divide(new BigDecimal(time == 0 ? 1 : time), 2, BigDecimal.ROUND_UP).doubleValue();
                        if (divide <= 0) {
                            divide = 1.00;
                        }
                        long residue_time = minRow.divide(new BigDecimal(divide), 0, BigDecimal.ROUND_UP).longValue();
                        String residue_seconds = "";
                        int days = ((int) residue_time) / (3600 * 24);
                        int hours = ((int) residue_time) % (3600 * 24) / 3600;
                        int minutes = ((int) residue_time) % (3600 * 24) % 3600 / 60;
                        int seconds = ((int) residue_time) % (3600 * 24) % 3600 % 60 % 60;

                        if (days > 0) {
                            residue_seconds = days + " ";
                            if (hours < 10) {
                                residue_seconds += "0" + hours;
                            } else {
                                residue_seconds += "" + hours;
                            }
                            if (minutes < 10) {
                                residue_seconds += ":0" + minutes;
                            } else {
                                residue_seconds += ":" + minutes;
                            }
                            if (seconds < 10) {
                                residue_seconds += ":0" + seconds;
                            } else {
                                residue_seconds += ":" + seconds;
                            }
                        } else if (hours > 0) {
                            if (hours < 10) {
                                residue_seconds = "0" + hours;
                            } else {
                                residue_seconds = "" + hours;
                            }
                            if (minutes < 10) {
                                residue_seconds += ":0" + minutes;
                            } else {
                                residue_seconds += ":" + minutes;
                            }
                            if (seconds < 10) {
                                residue_seconds += ":0" + seconds;
                            } else {
                                residue_seconds += ":" + seconds;
                            }
                        } else if (minutes > 0) {
                            if (minutes < 10) {
                                residue_seconds = "0" + minutes;
                            } else {
                                residue_seconds = "" + minutes;
                            }
                            if (seconds < 10) {
                                residue_seconds += ":0" + seconds;
                            } else {
                                residue_seconds += ":" + seconds;
                            }
                        } else if (seconds > 0) {
                            if (seconds < 10) {
                                residue_seconds = "00:0" + seconds;
                            } else {
                                residue_seconds = "00:" + seconds;
                            }
                        } else {
                            residue_seconds = "00:00";
                        }
                        reObj.put("row_key", rowkey);
                        reObj.put("residue_seconds", residue_seconds);
                        reObj.put("source_table", sourceTable);
                        reObj.put("meta_flag", Enums.MetaFlag.META_FLAG_MAINTASK_FULLDUMP.value());
                        // 防止删除操作中 sofar==totalwork显示进度条100%的问题
                        if (totalwork == sofar) {
                            reObj.put("percent", "0:00%");
                            reObj.put("residue_seconds", "[--:--]");
                        } else {
                            if (sofar == 0) {
                                reObj.put("residue_seconds", "[--:--]");
                            }
                            reObj.put("percent", totalwork - sofar <= 0 ? "100%"
                                    : new BigDecimal(sofar).divide(new BigDecimal(totalwork == 0 ? 1 : totalwork), 2,
                                            BigDecimal.ROUND_UP).multiply(new BigDecimal(100)) + "%");
                        }
                        reObj.put("status", "全量同步");
                    }
                }
            } else if (Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value().equals(configBO.getMETA_FLAG())) {
                reObj.put("row_key", rowkey);
                reObj.put("percent", "100%");
                reObj.put("status", "同步完成");
                reObj.put("meta_flag", Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value());
                reObj.put("source_table", configBO.getSOURCE_TABLE());
                reObj.put("residue_seconds", "[00:00]");
            } else if (Enums.MetaFlag.META_FLAG_UI_FULLDUMP.value().equals(configBO.getMETA_FLAG())) {
                reObj.put("row_key", rowkey);
                reObj.put("percent", "0.00%");
                reObj.put("residue_seconds", "[--:--]");
                reObj.put("meta_flag", Enums.MetaFlag.META_FLAG_UI_FULLDUMP.value());
                reObj.put("source_table", rowkey.substring(rowkey.lastIndexOf(Config.DELIMITER) + 1));
                reObj.put("status", "正在构建表");
            }
        } else {
            reObj.put("row_key", rowkey);
            reObj.put("percent", "0.00%");
            reObj.put("residue_seconds", "[--:--]");
            reObj.put("meta_flag", "0");
            reObj.put("source_table", rowkey.substring(rowkey.lastIndexOf(Config.DELIMITER) + 1));
            reObj.put("status", "正在构建表");
        }
        return reObj;
    }

    /**
     * 已同步列表获取监控数据列表
     *
     * @param sd source_schema 数据库schema
     *           source_instance 数据库实例
     *           source_table 数据库表
     *           keyword 关键字查询
     * @return 集合
     * @throws IOException    异常
     * @throws ParseException 异常
     */
    @Override
    public List<JSONObject> getSyncDataTransferList(SourceDataFilterVO sd, int particleSize) throws Exception {
        final SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
        final String functionName = "org.datatech.baikal.web.modules.dashboard.service.impl.DashboardServiceImpl.getSyncDataTransferList";
        final String tenantName = SecurityUtils.getTenantName();
        List<JSONObject> reList = new ArrayList<>();
        JSONObject reObj = null;
        // 获取hbase中所有已经同步的表
        List<JSONObject> tables = configService.listAllByInstanceSchemaSyncData(sd);
        StringBuffer configStartRowkey = new StringBuffer();
        StringBuffer configEndRowkey = new StringBuffer();
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(System.currentTimeMillis()))).getTime() / 1000
                + "";
        String endTime = System.currentTimeMillis() / 1000 + "";
        List<JSONObject> tmpList = new ArrayList<>();
        for (JSONObject obj : tables) {
            if (obj.get("meta_flag") == null) {
                continue;
            }
            final String source_table = obj.getString("source_table");
            if (!DataBaseUtil.stringMatch(sd.getKeyword(), source_table)) {
                continue;
            }
            if (StringUtil.isNull(obj.get("sofar")) || StringUtil.isNull(obj.get("total_work"))
                    || new Long(obj.get("sofar").toString()) < new Long(obj.get("total_work").toString())) {
                obj.put("source_table", source_table);
                reList.add(obj);
                continue;
            }
            tmpList.add(obj);
        }
        if (StringUtil.isNotEmpty(sd.getStart())) {
            tmpList = paging(tmpList, sd.getRows(), sd.getStart());
        }

        for (JSONObject obj : tmpList) {
            final String source_table = obj.getString("source_table");
            final String source_instance = obj.get("source_instance").toString();
            final String source_schema = obj.get("source_schema").toString();

            reObj = new JSONObject();
            HashMap<String, Long> srcDataMap = new HashMap<>(1000);
            Map<String, Long> dataMap = new TreeMap<>();
            dataMap.clear();
            dataMap.putAll(Config.stepTimeMap(particleSize));
            Calendar calendar = Calendar.getInstance();
            reObj.put("meta_flag", obj.get("meta_flag"));
            reObj.put("row_key",
                    String.join(Config.DELIMITER, source_instance, source_schema, obj.getString("source_table")));
            reObj.put("source_table", obj.get("source_table"));
            if (!StringUtil.isNull(obj.get("ddl_changed"))) {
                reObj.put("ddl_changed", obj.get("ddl_changed"));
            } else {
                reObj.put("ddl_changed", Enums.DdlChanged.DDLCHANGED_DISABLED.value());
            }
            configStartRowkey
                    .append(String.join(Config.DELIMITER, source_instance, source_schema, startTime, source_table));
            configEndRowkey
                    .append(String.join(Config.DELIMITER, source_instance, source_schema, endTime, source_table));
            final String startRowkey = configStartRowkey.toString();
            final String endRowkey = configEndRowkey.toString();
            final String cacheKey = String.join(Config.DELIMITER, functionName, source_instance, source_schema,
                    source_table, tenantName, sd.getStart() + "");
            List<MonitorTableBO> monitorList = EhcacheUtils.getMonitorTbaleCache(cacheKey);
            if (monitorList == null) {
                monitorList = monitorTableService.listAllByFilter(startRowkey, endRowkey, tenantName);
                EhcacheUtils.putMonitorTbaleCache(cacheKey, monitorList);
            }
            for (MonitorTableBO tableData : monitorList) {
                if (!source_table.equals(tableData.getRowKey().split(Config.DELIMITER)[3])) {
                    continue;
                }
                String hourMin = DateFormatter
                        .long2HHmm(new Timestamp(new Long(tableData.getRowKey().split(Config.DELIMITER)[2]) * 1000));
                if (srcDataMap.containsKey(hourMin)) {
                    srcDataMap.put(hourMin, Long.valueOf(tableData.getTOTAL_ROWS()) + srcDataMap.get(hourMin));
                } else {
                    srcDataMap.put(hourMin, Long.valueOf(tableData.getTOTAL_ROWS()));
                }
            }
            for (String key : dataMap.keySet()) {
                if ("00:00".equals(key)) {
                    dataMap.put(key, nvp(srcDataMap.get(key)));
                    continue;
                }
                calendar.setTime(HHmm.parse(key));
                calendar.add(Calendar.MINUTE, 1); //
                for (int i = particleSize; i > 0; --i) {
                    calendar.add(Calendar.MINUTE, -1);
                    final String v = HHmm.format(calendar.getTime());
                    Long mse = nvp(srcDataMap.get(v));
                    if (mse != null) {
                        dataMap.put(key, dataMap.get(key) + mse);
                    }
                }
            }

            // 求当前的图表的
            JSONArray xArr = new JSONArray();
            JSONArray legendArr = new JSONArray();
            List<JSONObject> series = new ArrayList<>();
            JSONArray data = new JSONArray();
            JSONObject object = new JSONObject();
            legendArr.add("current");
            for (String str : dataMap.keySet()) {
                xArr.add(str);
                data.add(dataMap.get(str));
            }
            object.put("name", "current");
            object.put("data", data);
            series.add(object);

            reObj.put("legendArr", legendArr);
            reObj.put("xArr", xArr);
            reObj.put("series", series);

            reList.add(reObj);

            configStartRowkey.delete(0, configStartRowkey.length());
            configEndRowkey.delete(0, configEndRowkey.length());
        }
        return reList;
    }

    /**
     * 热力图
     *
     * @param source_schema   数据库schema
     * @param source_instance 数据库实例
     * @return 对象
     * @throws Exception 异常
     */
    @Override
    public JSONObject getOGGTrend(String source_schema, String source_instance) throws Exception {
        JSONObject reObj = new JSONObject();
        JSONArray dataArr = new JSONArray();
        String[] months = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
        int year = DateUtil.getYear();
        String currentFistDay = DateUtil.getFirstDayOfMonth(year, 1);
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        String startRowkey = yyyyMmDd.parse(currentFistDay).getTime() + "";
        String endRowkey = System.currentTimeMillis() + "";
        Map<String, String> map = new HashMap<>();
        if (!StringUtil.isNull(source_schema) && !StringUtil.isNull(source_instance)) {
            map.put("SOURCE_SCHEMA", source_schema);
            map.put("SOURCE_INSTANCE", source_instance);
        }
        //todo 尽量避免使用map传参
        List<EventVO> list = eventService.queryAllByFilter(startRowkey, endRowkey, map, SecurityUtils.getTenantName());
        Map<String, Long> timeMap = new LinkedHashMap<>();
        for (String str : months) {
            int month = Integer.valueOf(str);
            String firstDay = DateUtil.getFirstDayOfMonth(year, month);
            String lastDay = DateUtil.getLastDayOfMonth(year, month);
            long day = DateUtil.getTwoDay(lastDay, firstDay);
            for (int i = 0; i <= day; i++) {
                long startTime = yyyyMmDd.parse(DateUtil.getNextDay(firstDay, i + "")).getTime();
                long endTime = yyyyMmDd.parse(DateUtil.getNextDay(firstDay, (i + 1) + "")).getTime();
                String key = String.join(Config.DELIMITER, startTime + "", endTime + "");
                timeMap.put(key, 0L);
            }
            if (day < 30) {
                for (int k = 0; k < 30 - day; k++) {
                    String key = UUID.randomUUID().toString();
                    timeMap.put(key, null);
                }
            }
        }

        for (EventVO obj : list) {
            for (String key : timeMap.keySet()) {
                if (!StringUtil.isNull(timeMap.get(key))) {
                    String[] timeArr = key.split(Config.DELIMITER);
                    long startTime = Long.valueOf(timeArr[0]);
                    long endTime = Long.valueOf(timeArr[1]);
                    long times = obj.getEvent_id();
                    if (times >= startTime && times < endTime) {
                        timeMap.put(key, timeMap.get(key) + 1);
                    }
                }
            }
        }
        JSONArray arr = null;
        int day = 0;
        int month = 0;
        for (String key : timeMap.keySet()) {
            arr = new JSONArray();
            arr.add(day);
            arr.add(month);
            if (day == 30) {
                day = 0;
                month++;
            } else {
                day++;
            }
            if (!StringUtil.isNull(timeMap.get(key))) {
                arr.add(timeMap.get(key));
            } else {
                arr.add(0);
            }
            dataArr.add(arr);
        }

        String[] days = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };
        reObj.put("days", days);
        reObj.put("months", months);
        reObj.put("data", dataArr);
        return reObj;
    }

    /**
     * 热力图获取当天的事件数量
     *
     * @param obj source_schema 数据库schema
     *            source_instance 数据库实例
     *            source_table 数据库表
     *            keyword 关键字查询
     * @return JSONObject对象
     * @throws Exception 异常
     */
    @Override
    public JSONObject getOGGTrendCurrentTime(SourceDataVO obj) throws Exception {
        JSONObject reObj = new JSONObject();
        int year = DateUtil.getYear();
        JSONArray arr = new JSONArray();

        Map<String, String> map = new HashMap<>();
        if (!StringUtil.isNull(obj) && !StringUtil.isNull(obj.getSource_instance())
                && !StringUtil.isNull(obj.getSource_schema())) {
            map.put("SOURCE_SCHEMA", obj.getSource_schema());
            map.put("SOURCE_INSTANCE", obj.getSource_instance());
        }
        SimpleDateFormat yyyyMmDd = new SimpleDateFormat("yyyy-MM-dd");
        String startRowkey = yyyyMmDd.parse(yyyyMmDd.format(new Timestamp(System.currentTimeMillis()))).getTime() + "";
        String endRowkey = System.currentTimeMillis() + "";
        long count = eventService.queryAllByFilterCount(startRowkey, endRowkey, map, SecurityUtils.getTenantName());

        // 获取今天是今年的第几天
        int month = DateUtil.getMonth();
        int indexOf = (month - 1) * 31;
        String firstDay = DateUtil.getFirstDayOfMonth(year, month);
        String lastDay = yyyyMmDd.format(new Date());
        long days = DateUtil.getDays(lastDay, firstDay);
        arr.add(days);
        arr.add(month - 1);
        arr.add(count);
        reObj.put("indexOf", indexOf + days);
        reObj.put("value", arr);
        return reObj;
    }

    final Long nvp(Long v) {
        return v == null ? 0L : v;
    }

    private List<JSONObject> paging(List<JSONObject> list, Integer pageSize, Integer start) {
        int totalCount = list.size();
        if (totalCount == 0) {
            return list;
        }
        int pageCount;
        int m = totalCount % pageSize;

        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }

        List<JSONObject> totalList = new ArrayList();
        if (start < 0) {
            start = 0;
        }
        start = start + 1;
        if (start > pageCount) {
            // start=pageCount;
            return new ArrayList<>();
        }
        if (start <= pageCount) {
            if (m == 0) {
                totalList = list.subList((start - 1) * pageSize, pageSize * (start));
            } else {
                if (start == pageCount) {
                    totalList = list.subList((start - 1) * pageSize, totalCount);
                } else {
                    totalList = list.subList((start - 1) * pageSize, pageSize * (start));
                }
            }
        }
        return totalList;
    }

}