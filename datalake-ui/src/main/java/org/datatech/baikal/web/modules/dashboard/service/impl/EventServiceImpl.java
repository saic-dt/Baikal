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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.datatech.baikal.common.exp.BizException;
import org.datatech.baikal.web.entity.bo.EventBO;
import org.datatech.baikal.web.modules.dashboard.service.EventService;
import org.datatech.baikal.web.modules.sqlite.service.SqliteService;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.util.StringUtil;
import org.datatech.baikal.web.vo.EventVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Service;

/**
 * 事件管理
 */
@Service
public class EventServiceImpl implements EventService {

    @Resource
    private SqliteService sqliteService;

    /**
     * 保存事件
     *
     * @param eventBO 事件BO
     * @return 保存的记录数 此方法保存单条记录
     * @throws Exception 异常
     */
    @Override
    public int save(EventBO eventBO) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            connection = sqliteService.getEventConnection(eventBO.getTenantName());
            state = connection.createStatement();
            String sql = "insert into Event(EVENT_ID,PARENT_EVENT_ID,SOURCE_INSTANCE,SOURCE_SCHEMA,SOURCE_TABLE,MESSAGE)"
                    + "values(" + eventBO.getEVENT_ID() + "," + eventBO.getPARENT_EVENT_ID() + ",'"
                    + eventBO.getSOURCE_INSTANCE() + "','" + eventBO.getSOURCE_SCHEMA() + "','"
                    + eventBO.getSOURCE_TABLE() + "','" + eventBO.getMESSAGE() + "')";
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

    /**
     * 根据主键 起始值和截止值检索事件信息
     *
     * @param startRowkey 起始值
     * @param endRowkey   截止值
     * @return 事件集合
     * @throws Exception 异常
     */
    @Override
    public List<EventVO> getListByRowKey(Long startRowkey, Long endRowkey) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        Statement state = null;
        ResultSet set = null;
        List<EventVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getEventConnection(tenantName);
            state = connection.createStatement();
            String sql = "select * from Event where EVENT_ID>=" + startRowkey + " and EVENT_ID<=" + endRowkey;
            set = state.executeQuery(sql);
            EventVO eventVO = null;
            while (set.next()) {
                eventVO = new EventVO();
                eventVO.setEvent_id(set.getLong("EVENT_ID"));
                eventVO.setMessage(set.getString("MESSAGE"));
                eventVO.setParent_event_id(set.getLong("PARENT_EVENT_ID"));
                eventVO.setSource_instance(set.getString("SOURCE_INSTANCE"));
                eventVO.setSource_schema(set.getString("SOURCE_SCHEMA"));
                eventVO.setSource_table(set.getString("SOURCE_TABLE"));
                reList.add(eventVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }

    /**
     * 根据过滤条件检索事件信息
     *
     * @param startRowkey 起始值
     * @param endRowkey   截止值
     * @param sd          源数据信息
     *                    source_schema schema
     *                    source_instance 数据库实例
     *                    source_table   数据库表
     * @param tenantName  租户
     * @return 事件集合
     * @throws Exception 异常
     */
    @Override
    public List<EventVO> getListByRowKeyFileter(String startRowkey, String endRowkey, SourceDataVO sd,
            String tenantName) throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<EventVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getEventConnection(tenantName);
            state = connection.createStatement();
            String sql = "select * from Event where EVENT_ID>=" + startRowkey + " and EVENT_ID<=" + endRowkey
                    + " and SOURCE_INSTANCE='" + sd.getSource_instance() + "' and SOURCE_SCHEMA='"
                    + sd.getSource_schema() + "'";
            set = state.executeQuery(sql);
            EventVO eventVO = null;
            while (set.next()) {
                eventVO = new EventVO();
                eventVO.setEvent_id(set.getLong("EVENT_ID"));
                eventVO.setMessage(set.getString("MESSAGE"));
                eventVO.setParent_event_id(set.getLong("PARENT_EVENT_ID"));
                eventVO.setSource_instance(set.getString("SOURCE_INSTANCE"));
                eventVO.setSource_schema(set.getString("SOURCE_SCHEMA"));
                eventVO.setSource_table(set.getString("SOURCE_TABLE"));
                reList.add(eventVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }

    /**
     * 根据过滤条件检索事件信息
     *
     * @param startRowkey 起始值
     * @param endRowkey   截止值
     * @param map         源数据信息
     *                    SOURCE_SCHEMA schema
     *                    SOURCE_INSTANCE 数据库实例
     * @param tenantName  租户
     * @return 事件集合
     * @throws Exception 异常
     */
    @Override
    public List<EventVO> queryAllByFilter(String startRowkey, String endRowkey, Map<String, String> map,
            String tenantName) throws Exception {
        Statement state = null;
        ResultSet set = null;
        List<EventVO> reList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = sqliteService.getEventConnection(tenantName);
            state = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from Event where EVENT_ID>=").append(startRowkey).append(" and EVENT_ID<=")
                    .append(endRowkey);
            if (!StringUtil.isNull(map.get("SOURCE_INSTANCE"))) {
                sql.append(" and SOURCE_INSTANCE='").append(map.get("SOURCE_INSTANCE")).append("'");
            }
            if (!StringUtil.isNull(map.get("SOURCE_SCHEMA"))) {
                sql.append(" and SOURCE_SCHEMA='").append(map.get("SOURCE_SCHEMA")).append("'");
            }
            set = state.executeQuery(sql.toString());
            EventVO eventVO = null;
            while (set.next()) {
                eventVO = new EventVO();
                eventVO.setEvent_id(set.getLong("EVENT_ID"));
                eventVO.setMessage(set.getString("MESSAGE"));
                reList.add(eventVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return reList;
    }

    /**
     * 根据过滤条件检索事件记录数
     *
     * @param startRowkey 起始值
     * @param endRowkey   截止值
     * @param map         源数据信息
     *                    SOURCE_SCHEMA schema
     *                    SOURCE_INSTANCE 数据库实例
     * @param tenantName  租户
     * @return 事件记录数
     * @throws Exception 异常
     */
    @Override
    public long queryAllByFilterCount(String startRowkey, String endRowkey, Map<String, String> map, String tenantName)
            throws Exception {
        Statement state = null;
        ResultSet set = null;
        Connection connection = null;
        long count = 0L;
        try {
            connection = sqliteService.getEventConnection(tenantName);
            state = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("select count(*) from Event where EVENT_ID>=").append(startRowkey).append(" and EVENT_ID<=")
                    .append(endRowkey);
            if (!StringUtil.isNull(map.get("SOURCE_INSTANCE"))) {
                sql.append(" and SOURCE_INSTANCE='").append(map.get("SOURCE_INSTANCE")).append("'");
            }
            if (!StringUtil.isNull(map.get("SOURCE_SCHEMA"))) {
                sql.append(" and SOURCE_SCHEMA='").append(map.get("SOURCE_SCHEMA")).append("'");
            }
            set = state.executeQuery(sql.toString());
            while (set.next()) {
                count = set.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(connection, state, set);
        }
        return count;
    }

    /**
     * 批量写入事件数据信息
     *
     * @param list 事件保存集合
     * @return 保存的记录数
     * @throws Exception 异常
     */
    @Override
    public int saveBatch(List<EventBO> list) throws Exception {
        Statement state = null;
        Connection connection = null;
        try {
            if (list.size() > 0) {
                connection = sqliteService.getEventConnection(list.get(0).getTenantName());
                state = connection.createStatement();
                for (EventBO eventBO : list) {
                    String sql = "insert into Event(EVENT_ID,PARENT_EVENT_ID,SOURCE_INSTANCE,SOURCE_SCHEMA,SOURCE_TABLE,MESSAGE)"
                            + "values(" + eventBO.getEVENT_ID() + "," + eventBO.getPARENT_EVENT_ID() + ",'"
                            + eventBO.getSOURCE_INSTANCE() + "','" + eventBO.getSOURCE_SCHEMA() + "','"
                            + eventBO.getSOURCE_TABLE() + "','" + eventBO.getMESSAGE() + "')";
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
}
