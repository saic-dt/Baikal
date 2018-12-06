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

package org.datatech.baikal.web.utils.TaskTool;

import java.sql.Connection;
import java.util.List;

import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.entity.bo.MetaBO;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.external.MongoDb;
import org.datatech.baikal.web.external.SpringUtil;
import org.datatech.baikal.web.external.ZkHandler;
import org.datatech.baikal.web.utils.DataBaseUtil;
import org.datatech.baikal.web.utils.EhcacheUtils;
import org.datatech.baikal.web.utils.JsonUtil;
import org.datatech.baikal.web.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取数据库表的个数
 */
public class TaskTool {
    private static final Logger logger = LoggerFactory.getLogger(TaskTool.class);
    private static final String LIP = ",";
    private static final String PATHFORMATE = "/metastore/%s/meta/%s/%s";

    /**
     * 返回DB中表行数 捕捉异常返回zero
     *
     * @param jdbcEntity SourceJdbc对象
     * @param refreshFlg 是否启用缓存
     * @return 数据库表的个数
     */
    public static Integer getDbTableCount(SourceJdbcBO jdbcEntity, Boolean refreshFlg) {

        final String dbTyp = jdbcEntity.getDB_TYPE();
        final String jdbcUrl = jdbcEntity.getJDBC_URL();
        final String user = jdbcEntity.getUSER();
        final String password = jdbcEntity.getPASSWORD();
        final String cln = DataBaseUtil.DRIVE_CLASS_MAP.get(dbTyp);
        final String instance = jdbcEntity.getINSTANCE_NAME();
        final String schema = jdbcEntity.getSCHEMA_NAME();
        final String tenantName = jdbcEntity.getTableName();
        final String key = String.join(LIP, tenantName, instance, schema);
        if (refreshFlg) {
            EhcacheUtils.removeCountCatch(key);
        }
        Integer v = EhcacheUtils.getTableCountCache(key);
        if (v != null && v != 0) {
            return v;
        }
        logger.info(instance + "." + schema + " BEGIN ");
        logger.info(jdbcUrl);
        Integer count = 0;
        try {
            if (Enums.DbType.DB_TYPE_MONGO.value().equals(jdbcEntity.getDB_TYPE())) {
                System.out.println(jdbcEntity.getUSER());
                System.out.println(jdbcEntity.getPASSWORD());
                String uri = MongoDb.getMongoUri(jdbcEntity.getJDBC_URL(), jdbcEntity.getUSER(),
                        jdbcEntity.getPASSWORD());
                count = MongoDb.getTableCount(jdbcEntity.getSCHEMA_NAME(), uri);
            } else {
                Connection connection = DataBaseUtil.getConnentByClassPool(jdbcUrl, user, password, cln);
                count = DataBaseUtil.getTableQuantity(connection, schema, cln);
                connection.close();
            }
            EhcacheUtils.putTableCountCache(key, count);
            logger.info(instance + "." + schema + " SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * 已同步表的个数
     * @param sourceJdbcBO SourceJdbc对象
     * @return 已同步表的个数
     */
    public static Integer getTableCountSync(SourceJdbcBO sourceJdbcBO) {
        ZkHandler zkHandler = SpringUtil.getBean(ZkHandler.class);
        int count = 0;
        final String tenantName = sourceJdbcBO.getTenantName();
        final String instance = sourceJdbcBO.getINSTANCE_NAME();
        final String schema = sourceJdbcBO.getSCHEMA_NAME();
        final String path = String.format(PATHFORMATE, tenantName, instance, schema);
        final String key = String.join(LIP, tenantName, instance, schema);
        Integer v = EhcacheUtils.getTableCountSyncCache(key);
        if (v != null) {
            return v;
        }
        try {
            Stat check = zkHandler.getClient().checkExists().forPath(path);
            if (null == check) {
                return count;
            }
            List<String> tables = zkHandler.getClient().getChildren().forPath(path);
            for (String table : tables) {
                String content = new String(zkHandler.getClient().getData().forPath(path + Config.BACKSLASH + table));
                if (StringUtil.isNotEmpty(content)) {
                    MetaBO configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
                    if (Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value().equals(configBO.getMETA_FLAG())
                            || Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION.value().equals(configBO.getMETA_FLAG())) {
                        if (!StringUtil.isNull(configBO.getSOFAR()) && !StringUtil.isNull(configBO.getTOTAL_WORK())) {
                            if (configBO.getSOFAR().equals(configBO.getTOTAL_WORK())) {
                                ++count;
                            }
                        }
                    }
                }
            }
            EhcacheUtils.putTableCountSyncCache(key, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

}
