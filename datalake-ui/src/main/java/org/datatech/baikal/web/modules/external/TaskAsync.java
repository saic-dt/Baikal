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

package org.datatech.baikal.web.modules.external;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.entity.bo.EventBO;
import org.datatech.baikal.web.entity.bo.MonitorSchemaBO;
import org.datatech.baikal.web.entity.bo.MonitorTableBO;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.EventService;
import org.datatech.baikal.web.modules.dashboard.service.MetaService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorSchemaService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorTableService;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.utils.DataBaseUtil;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

/**
 * 测试模式 同步代码
 */
@Service
public class TaskAsync {

    @Resource
    private MetaService configService;

    @Resource
    private MonitorTableService monitorTableService;

    @Resource
    private MonitorSchemaService monitorSchemaService;

    @Resource
    private EventService eventService;

    @Resource
    private SourceJdbcService sourceJdbcService;

    // @Async
    public void syncSimulator(SourceDataVO sourceData) throws Exception {
        if (!Constants.TEST_MODE) {
            return;
        } // 非测试模式 直接返回
        String tenantName = SecurityUtils.getTenantName();
        SourceJdbcBO sourceJdbcBO = sourceJdbcService.getSourceJdbcByInstanceSchema(sourceData.getSource_schema(),
                sourceData.getSource_instance(), tenantName);
        long count = 0;
        if (Enums.DbType.DB_TYPE_MONGO.value().equals(sourceJdbcBO.getDB_TYPE())) {
            String uri = MongoDb.getMongoUri(sourceJdbcBO.getJDBC_URL(), sourceJdbcBO.getUSER(),
                    sourceJdbcBO.getPASSWORD());
            count = MongoDb.getDocumentCount(sourceJdbcBO.getSCHEMA_NAME(), uri, sourceData.getSource_table());
        } else {
            Connection connection = DataBaseUtil.getConnentByClassPool(sourceJdbcBO.getJDBC_URL(),
                    sourceJdbcBO.getUSER(), sourceJdbcBO.getPASSWORD(), Config.JDBC_CLASS_NAME_MYSQL);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select count(*) from " + sourceData.getSource_table());

            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            statement.close();
            connection.close();
        }
        final String instance = sourceData.getSource_instance();
        final String table = sourceData.getSource_table();
        final String schema = sourceData.getSource_schema();
        try {
            long syncSum = 0;
            int i = 0;
            while (syncSum <= count && i < 50) {
                ++i;
                int randam = (int) (Math.random() * (count / 2));
                syncSum += randam;
                int metaFlag = 0;
                if (syncSum >= count) {
                    syncSum = count;
                    metaFlag = 3;
                }
                Long currentTimeMillis = System.currentTimeMillis();

                JSONObject obj = new JSONObject();
                obj.put("TABLE_VERSION", String.valueOf(currentTimeMillis));
                obj.put("SOURCE_INSTANCE", instance);
                obj.put("SOURCE_SCHEMA", schema);
                obj.put("SOURCE_TABLE", table);
                obj.put("SOFAR", String.valueOf(syncSum));
                obj.put("META_FLAG", String.valueOf(metaFlag));
                obj.put("PREFIX", "");
                obj.put("TOTAL_WORK", count + "");
                obj.put("DDL_CHANGED", "0");
                configService.save(obj.toString(), SecurityUtils.getTenantName(), sourceData);

                String mtt_row_key = String.join(Config.DELIMITER, instance, schema,
                        String.valueOf(currentTimeMillis / 1000), table);

                MonitorTableBO monitorTableBO = new MonitorTableBO();
                monitorTableBO.setRowKey(mtt_row_key);
                monitorTableBO.setDELETE_ROWS(Long.parseLong((Integer.toString((int) (Math.random() * 2)))));
                monitorTableBO.setDISCARD_ROWS(Long.parseLong((Integer.toString((int) (Math.random() * 2)))));
                monitorTableBO.setINSERT_ROWS(Long.parseLong((Integer.toString((int) (Math.random() * 2)))));
                monitorTableBO.setUPDATE_ROWS(Long.parseLong((Integer.toString((int) (Math.random() * 2)))));
                monitorTableBO.setTOTAL_ROWS(Long.parseLong(randam + ""));
                monitorTableBO.setTenantName(tenantName);
                monitorTableService.save(monitorTableBO);

                String row_key = String.join(Config.DELIMITER, instance, schema,
                        String.valueOf(currentTimeMillis / 1000), "RKAFKASS");

                MonitorSchemaBO monitorSchemaBO = new MonitorSchemaBO();
                monitorSchemaBO.setRowKey(row_key);
                monitorSchemaBO.setPROC_CHECKPOINT(Integer.parseInt(Integer.toString((int) (Math.random() * 2))));
                monitorSchemaBO.setPROC_LAG(Integer.parseInt(Integer.toString((int) (Math.random() * 2))));
                monitorSchemaBO.setPROC_ROWS(Integer.parseInt(syncSum + ""));
                monitorSchemaBO.setPROC_STATUS("RUNNING");
                monitorSchemaBO.setPROC_TYPE("REPLICAT");
                monitorSchemaBO.setTenantName(tenantName);
                monitorSchemaService.save(monitorSchemaBO);

                if (syncSum == count) {
                    break;
                }
                TimeUnit.SECONDS.sleep(2);
            }

            long currentTimeMillis = System.currentTimeMillis();

            EventBO eventVO = new EventBO();
            eventVO.setEVENT_ID(currentTimeMillis);
            eventVO.setPARENT_EVENT_ID(currentTimeMillis);
            eventVO.setMESSAGE("full dump finished for instance [" + instance + "], \n" + " schema [" + schema
                    + "], table [" + table + "], hbase row key prefix ["
                    + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 6) + "], row dumped ["
                    + count + "]");
            eventVO.setSOURCE_INSTANCE(instance);
            eventVO.setSOURCE_SCHEMA(schema);
            eventVO.setSOURCE_TABLE(table);
            eventVO.setTenantName(tenantName);
            List<EventBO> list = new ArrayList<>();
            list.add(eventVO);
            eventService.saveBatch(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
