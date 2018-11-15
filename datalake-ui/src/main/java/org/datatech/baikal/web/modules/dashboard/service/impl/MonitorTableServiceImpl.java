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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.exp.BizException;
import org.datatech.baikal.web.entity.bean.TenantBean;
import org.datatech.baikal.web.entity.bo.MonitorTableBO;
import org.datatech.baikal.web.modules.dashboard.service.MonitorTableService;
import org.datatech.baikal.web.modules.dashboard.service.TenantService;
import org.datatech.baikal.web.modules.external.PageModel;
import org.datatech.baikal.web.modules.sqlite.service.SqliteService;
import org.datatech.baikal.web.utils.StringUtil;
import org.datatech.baikal.web.vo.MonitorTableVO;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

/**
 * 事件管理
 */
@Service
public class MonitorTableServiceImpl implements MonitorTableService {

    @Resource
    private SqliteService sqliteService;

    @Resource
    private TenantService tenantService;

    @Override
    public int save(MonitorTableBO monitorTableDO) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(monitorTableDO.getTenantName());
            state = connection.createStatement();
            String sql = "insert into MonitorTable(RowKey,INSERT_ROWS,UPDATE_ROWS,DELETE_ROWS,DISCARD_ROWS,TOTAL_ROWS)"
                    + "values('" + monitorTableDO.getRowKey() + "'," + monitorTableDO.getINSERT_ROWS() + ","
                    + monitorTableDO.getUPDATE_ROWS() + "," + monitorTableDO.getDELETE_ROWS() + ","
                    + monitorTableDO.getDISCARD_ROWS() + "," + monitorTableDO.getTOTAL_ROWS() + ")";
            int count = state.executeUpdate(sql);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, null);
        }
        return 0;
    }

    @Override
    public int saveBatch(List<MonitorTableBO> list) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            if (list.size() > 0) {
                connection = sqliteService.getMonitorConnection(list.get(0).getTenantName());
                state = connection.createStatement();
                for (MonitorTableBO monitorTableBO : list) {
                    String sql = "insert into MonitorTable(RowKey,INSERT_ROWS,UPDATE_ROWS,DELETE_ROWS,DISCARD_ROWS,TOTAL_ROWS)"
                            + "values('" + monitorTableBO.getRowKey() + "'," + monitorTableBO.getINSERT_ROWS() + ","
                            + monitorTableBO.getUPDATE_ROWS() + "," + monitorTableBO.getDELETE_ROWS() + ","
                            + monitorTableBO.getDISCARD_ROWS() + "," + monitorTableBO.getTOTAL_ROWS() + ")";
                    state.addBatch(sql);
                }
                int[] count = state.executeBatch();
                return count.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, null);
        }
        return 0;
    }

    @Override
    public List<MonitorTableBO> listAllByFilter(String startRowkey, String endRowkey, String tenantName)
            throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<MonitorTableBO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(tenantName);
            state = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from MonitorTable where RowKey>='").append(startRowkey).append("' and RowKey<='")
                    .append(endRowkey).append("'");
            set = state.executeQuery(sql.toString());
            MonitorTableBO monitorTableBO = null;
            while (set.next()) {
                monitorTableBO = new MonitorTableBO();
                monitorTableBO.setRowKey(set.getString("RowKey"));
                monitorTableBO.setDELETE_ROWS(set.getLong("DELETE_ROWS"));
                monitorTableBO.setDISCARD_ROWS(set.getLong("DISCARD_ROWS"));
                monitorTableBO.setINSERT_ROWS(set.getLong("INSERT_ROWS"));
                monitorTableBO.setUPDATE_ROWS(set.getLong("UPDATE_ROWS"));
                monitorTableBO.setTOTAL_ROWS(set.getLong("TOTAL_ROWS"));
                reList.add(monitorTableBO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }

    @Override
    public List<MonitorTableBO> listAllByFilter(String prefixFilter, String tenantName) throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<MonitorTableBO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(tenantName);
            state = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from MonitorTable where RowKey>='").append(prefixFilter).append("'");
            set = state.executeQuery(sql.toString());
            MonitorTableBO monitorTableBO = null;
            while (set.next()) {
                monitorTableBO = new MonitorTableBO();
                monitorTableBO.setRowKey(set.getString("RowKey"));
                monitorTableBO.setDELETE_ROWS(set.getLong("DELETE_ROWS"));
                monitorTableBO.setDISCARD_ROWS(set.getLong("DISCARD_ROWS"));
                monitorTableBO.setINSERT_ROWS(set.getLong("INSERT_ROWS"));
                monitorTableBO.setUPDATE_ROWS(set.getLong("UPDATE_ROWS"));
                monitorTableBO.setTOTAL_ROWS(set.getLong("TOTAL_ROWS"));
                reList.add(monitorTableBO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }

    @Override
    public List<MonitorTableVO> listPage(String prefixRowkey, String prefixEndRowkey, PageModel hpm, String tenantName,
            String whereSql) throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<MonitorTableVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(tenantName);
            state = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from MonitorTable where RowKey>='").append(prefixRowkey).append("' and RowKey<='")
                    .append(prefixEndRowkey).append("'");
            if (!StringUtil.isNull(whereSql)) {
                sql.append(whereSql);
            }
            // 加入排序
            sql.append(" order by RowKey desc ");
            if (hpm != null) {
                sql.append(" limit ").append((hpm.getPageIndex() - 1) * hpm.getPageSize()).append(",")
                        .append(hpm.getPageSize());
            }
            set = state.executeQuery(sql.toString());
            MonitorTableVO monitorTableVO = null;
            while (set.next()) {
                monitorTableVO = new MonitorTableVO();
                monitorTableVO.setRowKey(set.getString("RowKey"));
                monitorTableVO.setDelete_rows(set.getLong("DELETE_ROWS"));
                monitorTableVO.setDiscard_rows(set.getLong("DISCARD_ROWS"));
                monitorTableVO.setInsert_rows(set.getLong("INSERT_ROWS"));
                monitorTableVO.setUpdate_rows(set.getLong("UPDATE_ROWS"));
                monitorTableVO.setTotal_rows(set.getLong("TOTAL_ROWS"));
                reList.add(monitorTableVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }

    @Override
    public List<JSONObject> listAll() throws Exception {
        List<JSONObject> reList = new ArrayList<>();
        // 获取所有租户信息
        List<TenantBean> list = tenantService.tenantList();
        Statement state = null;
        ResultSet set = null;
        Connection connection = null;
        JSONObject object = null;
        for (TenantBean te : list) {
            object = new JSONObject();
            object.put("tenantName", te.getTenantName());
            List<JSONObject> elements = new ArrayList<>();
            List<JSONObject> tables = new ArrayList<>();
            try {
                connection = sqliteService.getMonitorConnection(te.getTenantName());
                state = connection.createStatement();
                StringBuffer sql = new StringBuffer();
                sql.append("select substr(substr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER + "')+1),"
                        + "instr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER + "')+1),'" + Config.DELIMITER
                        + "')+1)," + "instr(substr( substr(RowKey,instr(RowKey,'" + Config.DELIMITER + "')+1),"
                        + "instr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER + "')+1),'" + Config.DELIMITER
                        + "')+1),'" + Config.DELIMITER + "')+1) as `table`," + "substr(RowKey,0,instr(RowKey,'"
                        + Config.DELIMITER + "')) as `instance`,substr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER
                        + "')+1),0," + "instr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER + "')+1),'"
                        + Config.DELIMITER + "')) as `schema`," + "max(substr(substr(substr(RowKey,instr(RowKey,'"
                        + Config.DELIMITER + "')+1)," + "instr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER
                        + "')+1),'" + Config.DELIMITER + "')+1),0," + "instr(substr(substr(RowKey,instr(RowKey,'"
                        + Config.DELIMITER + "')+1)," + "instr(substr(RowKey,instr(RowKey,'" + Config.DELIMITER
                        + "')+1),'" + Config.DELIMITER + "')+1),'" + Config.DELIMITER + "'))) as `timestamp`,"
                        + "sum(insert_rows) as insert_sum,sum(UPDATE_ROWS) as update_sum,sum(DELETE_ROWS) as delete_sum,"
                        + "sum(total_rows) as totals from MonitorTable group by  `schema`,`instance`,`table` order by `schema`,`instance`,`table`");
                set = state.executeQuery(sql.toString());
                JSONObject jsonObject = null;
                JSONObject instanceSchema = null;
                Map<String, List<JSONObject>> dataMap = new HashMap<>();
                while (set.next()) {
                    jsonObject = new JSONObject();
                    String key = set.getString("instance") + Config.DELIMITER + set.getString("schema");
                    if (StringUtil.isNull(dataMap.get(key))) {
                        tables = new ArrayList<>();
                    } else {
                        tables = dataMap.get(key);
                    }
                    jsonObject.put("table", set.getString("table"));
                    jsonObject.put("insert_sum", set.getLong("insert_sum"));
                    jsonObject.put("update_sum", set.getLong("update_sum"));
                    jsonObject.put("delete_sum", set.getLong("delete_sum"));
                    jsonObject.put("timestamp", set.getLong("timestamp"));
                    jsonObject.put("totals", set.getLong("totals"));
                    tables.add(jsonObject);
                    dataMap.put(set.getString("instance") + Config.DELIMITER + set.getString("schema"), tables);
                }
                for (Map.Entry<String, List<JSONObject>> entry : dataMap.entrySet()) {
                    instanceSchema = new JSONObject();
                    instanceSchema.put("instance", entry.getKey().split(Config.DELIMITER)[0]);
                    instanceSchema.put("schema", entry.getKey().split(Config.DELIMITER)[1]);
                    instanceSchema.put("tables", entry.getValue());
                    elements.add(instanceSchema);
                }
                object.put("elements", elements);
            } catch (Exception e) {
                e.printStackTrace();
                BizException.throwWhenFalse(false, e.getMessage());
            } finally {
                sqliteService.close(connection, state, set);
            }
            reList.add(object);
        }
        return reList;
    }
}
