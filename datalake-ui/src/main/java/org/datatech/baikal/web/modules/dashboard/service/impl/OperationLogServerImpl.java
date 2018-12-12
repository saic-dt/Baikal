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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datatech.baikal.common.exp.BizException;
import org.datatech.baikal.web.core.PageModel;
import org.datatech.baikal.web.entity.bo.OperationLogBO;
import org.datatech.baikal.web.modules.dashboard.service.OperationLogService;
import org.datatech.baikal.web.modules.sqlite.service.SqliteService;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.util.StringUtil;
import org.datatech.baikal.web.vo.OperationLogVO;
import org.springframework.stereotype.Service;

/**
 * 系统操作日志
 */
@Service
public class OperationLogServerImpl implements OperationLogService {

    private static final Log log = LogFactory.getLog(OperationLogServerImpl.class);
    @Resource
    private SqliteService sqliteService;

    @Override
    public int save(OperationLogBO operationLogBO) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            connection = sqliteService.getOperationLogConnection("");
            state = connection.createStatement();
            String sql = "insert into Operation_log(ID,USER_NAME,TENANT_NAME,SOURCE_INSTANCE,SOURCE_SCHEMA,"
                    + "SOURCE_TABLE,DB_TYPE,OPERATE_TYPE,MESSAGE,OPERATION_TIME)" + "values(null,'"
                    + operationLogBO.getUSER_NAME() + "','" + operationLogBO.getTENANT_NAME() + "','"
                    + operationLogBO.getSOURCE_INSTANCE() + "','" + operationLogBO.getSOURCE_SCHEMA() + "','"
                    + operationLogBO.getSOURCE_TABLE() + "','" + operationLogBO.getDB_TYPE() + "'," + "'"
                    + operationLogBO.getOPTERATE_TYPE() + "','" + operationLogBO.getMESSAGE() + "',"
                    + operationLogBO.getOPERATION_TIME() + ")";
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
    public int save(String operationType, String message) throws Exception {
        OperationLogBO operationLogBO = new OperationLogBO();
        operationLogBO.setDB_TYPE("");
        operationLogBO.setSOURCE_INSTANCE("");
        operationLogBO.setSOURCE_SCHEMA("");
        operationLogBO.setSOURCE_TABLE("");
        operationLogBO.setOPTERATE_TYPE(operationType);
        operationLogBO.setTENANT_NAME(SecurityUtils.getTenantName());
        operationLogBO.setUSER_NAME(SecurityUtils.getUserName());
        operationLogBO.setOPERATION_TIME(System.currentTimeMillis());
        operationLogBO.setMESSAGE(message);
        return save(operationLogBO);
    }

    @Override
    public List<OperationLogVO> getList(OperationLogVO operationLogVO, PageModel hpm) throws Exception {

        if (StringUtil.isNull(hpm.getPageSize())) {
            hpm.setPageSize(10);
        }
        if (StringUtil.isNull(hpm.getPageIndex())) {
            hpm.setPageIndex(1);
        } else if (hpm.getPageIndex() < 0) {
            hpm.setPageIndex(1);
        }
        StringBuffer whereSql = new StringBuffer();
        if (StringUtil.isNotEmpty(operationLogVO.getDb_type())) {
            whereSql.append(" and db_type='").append(operationLogVO.getDb_type()).append("'");
        }
        if (StringUtil.isNotEmpty(operationLogVO.getUser_name())) {
            whereSql.append(" and user_name like '%").append(operationLogVO.getUser_name()).append("%'");
        }
        if (StringUtil.isNotEmpty(operationLogVO.getSource_instance())) {
            whereSql.append(" and source_instance like '%").append(operationLogVO.getSource_instance()).append("%'");
        }
        if (StringUtil.isNotEmpty(operationLogVO.getSource_schema())) {
            whereSql.append(" and source_schema like '%").append(operationLogVO.getSource_schema()).append("%'");
        }
        if (StringUtil.isNotEmpty(operationLogVO.getSource_table())) {
            whereSql.append(" and source_table like '%").append(operationLogVO.getSource_table()).append("%'");
        }
        if (StringUtil.isNotEmpty(operationLogVO.getOpterate_type())) {
            whereSql.append(" and operate_type ='").append(operationLogVO.getOpterate_type()).append("'");
        }
        if (StringUtil.isNotEmpty(operationLogVO.getTenant_name())) {
            whereSql.append(" and tenant_name like '%").append(operationLogVO.getTenant_name()).append("%'");
        }
        SimpleDateFormat YYYY_MM_DDHH24miss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtil.isNotEmpty(operationLogVO.getStartTime())) {
            whereSql.append(" and operation_time>=")
                    .append(YYYY_MM_DDHH24miss.parse(operationLogVO.getStartTime()).getTime());
        }
        if (StringUtil.isNotEmpty(operationLogVO.getEndTime())) {
            whereSql.append(" and operation_time<=")
                    .append(YYYY_MM_DDHH24miss.parse(operationLogVO.getEndTime()).getTime());
        }

        Statement state = null;
        ResultSet set = null;
        List<OperationLogVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getOperationLogConnection("");
            state = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from Operation_log");
            if (StringUtil.isNotEmpty(whereSql)) {
                sql.append(" where 1=1 ").append(whereSql);
            }
            // 加入排序
            sql.append(" order by id desc ");
            if (hpm != null) {
                sql.append(" limit ").append((hpm.getPageIndex() - 1) * hpm.getPageSize()).append(",")
                        .append(hpm.getPageSize());
            }
            log.debug("execute Sql:" + sql.toString());
            set = state.executeQuery(sql.toString());
            OperationLogVO olv = null;
            while (set.next()) {
                olv = new OperationLogVO();
                olv.setDb_type(set.getString("DB_TYPE"));
                olv.setOperation_time(YYYY_MM_DDHH24miss.format(set.getLong("OPERATION_TIME")));
                olv.setMessage(set.getString("MESSAGE"));
                olv.setSource_instance(set.getString("SOURCE_INSTANCE"));
                olv.setSource_schema(set.getString("SOURCE_SCHEMA"));
                olv.setSource_table(set.getString("SOURCE_TABLE"));
                olv.setTenant_name(set.getString("TENANT_NAME"));
                olv.setOpterate_type(set.getString("OPERATE_TYPE"));
                olv.setUser_name(set.getString("USER_NAME"));
                reList.add(olv);
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
