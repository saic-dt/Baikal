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
package org.datatech.baikal.task.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.zookeeper.CreateMode;
import org.datatech.baikal.common.Bytes;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.MetaConfig;
import org.datatech.baikal.task.util.DbHelper;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Data Access Object for meta configuration table.
 */
@Service
public class MetaConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(MetaConfigDao.class);

    @Autowired
    private ZkHandler handler;

    @Autowired
    private DbHelper dbHelper;

    /**
     * Insert a meta config record to Config table.
     *
     * @param metaConfig metaConfig
     * @throws Exception Exception
     */
    public void insertMetaConfig(MetaConfig metaConfig) throws Exception {
        String metaTablePath = handler.getMetaTablePath(metaConfig.getSOURCE_INSTANCE(), metaConfig.getSOURCE_SCHEMA(),
                metaConfig.getSOURCE_TABLE());
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().forPath(metaTablePath, Bytes.toBytes(metaConfig.toJson()));
        } else {
            String metaTablePathNode = handler.getClient().create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(metaTablePath, Bytes.toBytes(metaConfig.toJson()));
        }
        logger.info("insertMetaConfig completed, metaTablePathNode: [{}]", metaTablePath);
    }

    private JsonObject buildNewJsonObject(String zkPath, String key, String value) throws Exception {
        JsonObject obj = null;
        String metaTableValue = Bytes.toString(handler.getClient().getData().forPath(zkPath));
        try {
            JsonParser parser = new JsonParser();
            obj = parser.parse(metaTableValue).getAsJsonObject();
            obj.addProperty(key, value);
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }

        return obj;
    }

    /**
     * Update TOTALWORK column in Config table.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param tableName    Table Name
     * @param count        count
     * @throws Exception Exception
     */
    public void updateTotalWork(String instanceName, String schemaName, String tableName, long count) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "TOTAL_WORK", String.valueOf(count));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        }
        logger.info("updateTotalWork completed, metaTablePath: [{}], count: [{}]", metaTablePath, count);
    }

    /**
     * Update SOFAR column in Config table.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param tableName    Table Name
     * @param count        count
     * @throws Exception Exception
     */
    public void updateSoFar(String instanceName, String schemaName, String tableName, long count) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "SOFAR", String.valueOf(count));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().withVersion(-1).forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        }
        logger.info("updateSoFar completed, metaTablePath: [{}], count: [{}]", metaTablePath, count);
    }

    /**
     * Update META_FLAG column in Config table.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param tableName    Table Name
     * @param value        value
     * @throws Exception Exception
     */
    public void updateMetaFlag(String instanceName, String schemaName, String tableName, int value) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "META_FLAG", String.valueOf(value));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().withVersion(-1).forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        }
        logger.info("updateMetaFlag  metaTablePath: [{}], value: [{}]", metaTablePath, value);
    }

    /**
     * Update SOURCE_PK column in Config table.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param tableName    Table Name
     * @param value        value
     * @throws Exception Exception
     */
    public void updateSourcePk(String instanceName, String schemaName, String tableName, String value)
            throws Exception {
        if (value == null) {
            value = "";
        }
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "SOURCE_PK", String.valueOf(value));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().withVersion(-1).forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, Bytes.toBytes(jobj.toString()));
        }
        logger.info("updateSourcePk completed, metaTablePath: [{}], value: [{}]", metaTablePath, value);
    }

    /**
     * Get SOURCE_PK column in Config table.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param tableName    Table Name
     * @return value of SOURCE_PK column
     * @throws Exception Exception
     */
    public String getSoucePk(String instanceName, String schemaName, String tableName) throws Exception {
        Pair<Connection, DbType> p = dbHelper.getConnection(instanceName, schemaName);
        Connection connection = p.getLeft();
        ResultSet res;
        DatabaseMetaData dm = connection.getMetaData();
        DbType dbType1 = p.getRight();
        if ((dbType1 == DbType.ORACLE) || (dbType1 == DbType.DB2)) {
            res = dm.getPrimaryKeys(null, schemaName.toUpperCase(), tableName.toUpperCase());
        } else if (dbType1 == DbType.MYSQL) {
            res = dm.getPrimaryKeys("", schemaName, tableName);
        } else {
            res = dm.getPrimaryKeys(null, schemaName, tableName);
        }
        List<String> keyList = new ArrayList<>();
        while (res.next()) {
            String pkey = res.getString("COLUMN_NAME");
            if (pkey != null) {
                keyList.add(pkey);
            }
        }
        logger.info("key column name is [{}]", keyList);
        String pks = String.join(",", keyList);
        logger.info("SourcePK generated: [{}]", pks);
        return pks;
    }

    /**
     * Get Table Version in Config table.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param tableName    Table Name
     * @return value of Table Version
     * @throws Exception Exception
     */
    public String getTableVersion(String instanceName, String schemaName, String tableName) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        String metaTableValue = Bytes.toString(handler.getClient().getData().forPath(metaTablePath));
        logger.info("metaTableValue [{}]", metaTableValue);
        try {
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(metaTableValue).getAsJsonObject();
            String key = "TABLE_VERSION";
            if (obj.get(key) != null) {
                logger.info("getTableVersion  : [{}]", obj.get(key).toString());
                return obj.get("TABLE_VERSION").toString();
            }
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }
        return null;
    }

}