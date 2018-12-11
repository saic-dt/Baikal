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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.datatech.baikal.common.exp.BizException;
import org.datatech.baikal.web.entity.bo.MonitorSchemaBO;
import org.datatech.baikal.web.modules.dashboard.service.MonitorSchemaService;
import org.datatech.baikal.web.modules.sqlite.service.SqliteService;
import org.datatech.baikal.web.utils.DateUtil;
import org.datatech.baikal.web.vo.MonitorSchemaVO;
import org.springframework.stereotype.Service;

/**
 * 监控schema管理
 */
@Service
public class MonitorSchemaServiceImpl implements MonitorSchemaService {

    @Resource
    private SqliteService sqliteService;

    @Override
    public int save(MonitorSchemaBO monitorSchemaDO) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(monitorSchemaDO.getTenantName());
            state = connection.createStatement();
            String sql = "insert into MonitorSchema(RowKey,PROC_TYPE,PROC_STATUS,PROC_LAG,PROC_CHECKPOINT,PROC_ROWS)"
                    + "values('" + monitorSchemaDO.getRowKey() + "','" + monitorSchemaDO.getPROC_TYPE() + "','"
                    + monitorSchemaDO.getPROC_STATUS() + "'," + monitorSchemaDO.getPROC_LAG() + ","
                    + monitorSchemaDO.getPROC_CHECKPOINT() + "," + monitorSchemaDO.getPROC_ROWS() + ")";
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

    public int save() {

        return 0;
    }

    @Override
    public int saveBatch(List<MonitorSchemaBO> list) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            if (list.size() > 0) {
                connection = sqliteService.getMonitorConnection(list.get(0).getTenantName());
                state = connection.createStatement();
                for (MonitorSchemaBO monitorSchemaBO : list) {
                    String sql = "insert into MonitorSchema(RowKey,PROC_TYPE,PROC_STATUS,PROC_LAG,PROC_CHECKPOINT,PROC_ROWS)"
                            + "values('" + monitorSchemaBO.getRowKey() + "','" + monitorSchemaBO.getPROC_TYPE() + "','"
                            + monitorSchemaBO.getPROC_STATUS() + "'," + monitorSchemaBO.getPROC_LAG() + ","
                            + monitorSchemaBO.getPROC_CHECKPOINT() + "," + monitorSchemaBO.getPROC_ROWS() + ")";
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
    public List<MonitorSchemaVO> queryAllByFilter(String tenantName, String startRowkey, String endRowkey)
            throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<MonitorSchemaVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(tenantName);
            state = connection.createStatement();
            String sql = "select * from MonitorSchema where RowKey>='" + startRowkey + "' and RowKey<='" + endRowkey
                    + "'";

            MonitorSchemaVO monitorSchemaVO = null;
            set = state.executeQuery(sql);
            while (set.next()) {
                monitorSchemaVO = new MonitorSchemaVO();
                monitorSchemaVO.setPROC_ROWS(set.getInt("PROC_ROWS"));
                monitorSchemaVO.setRowKey(set.getString("RowKey"));
                monitorSchemaVO.setPROC_CHECKPOINT(set.getInt("PROC_CHECKPOINT"));
                monitorSchemaVO.setPROC_LAG(set.getInt("PROC_LAG"));
                monitorSchemaVO.setPROC_STATUS(set.getString("PROC_STATUS"));
                monitorSchemaVO.setPROC_TYPE(set.getString("PROC_TYPE"));
                reList.add(monitorSchemaVO);
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
    public List<MonitorSchemaVO> queryAllByFilterYesterday(String tenantName, String startRowkey, String endRowkey)
            throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<MonitorSchemaVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            String yyyyMMdd = new SimpleDateFormat("yyyyMMdd").format(DateUtil.getNextDay(new Date(), -1));
            connection = sqliteService.getMonitorConnection(tenantName, yyyyMMdd);
            state = connection.createStatement();
            String sql = "select * from MonitorSchema where RowKey>='" + startRowkey + "' and RowKey<='" + endRowkey
                    + "'";
            set = state.executeQuery(sql);
            MonitorSchemaVO monitorSchemaVO = null;
            while (set.next()) {
                monitorSchemaVO = new MonitorSchemaVO();
                monitorSchemaVO.setPROC_ROWS(set.getInt("PROC_ROWS"));
                monitorSchemaVO.setRowKey(set.getString("RowKey"));
                monitorSchemaVO.setPROC_CHECKPOINT(set.getInt("PROC_CHECKPOINT"));
                monitorSchemaVO.setPROC_LAG(set.getInt("PROC_LAG"));
                monitorSchemaVO.setPROC_STATUS(set.getString("PROC_STATUS"));
                monitorSchemaVO.setPROC_TYPE(set.getString("PROC_TYPE"));
                reList.add(monitorSchemaVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }
}
