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

package org.datatech.baikal.fulldump.meta;

import java.io.IOException;

import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Data Access Object for meta configuration table.
 */
public class MetaConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(MetaConfigDao.class);
    private ZkHandler handler;

    public MetaConfigDao(String zk_quorum, String task_queueip, String tenantName) {
        handler = new ZkHandler(zk_quorum, task_queueip, tenantName);
    }

    public String getFulldumpCustomizables(String nodeName) {
        String metaTablePath = ZKPaths.makePath("config", nodeName);
        String nodeValue = null;
        try {
            nodeValue = new String(handler.getClient().getData().forPath(metaTablePath));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(">>>>>> getFulldumpCustomizables failed");
        }
        return nodeValue;
    }

    private JsonObject buildNewJsonObject(String zkPath, String key, String value) throws Exception {
        JsonObject obj = null;

        String metaTableValue = new String(handler.getClient().getData().forPath(zkPath), "UTF-8");
        try {
            JsonParser parser = new JsonParser();
            obj = parser.parse(metaTableValue).getAsJsonObject();
            obj.addProperty(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Update TOTALWORK column in Config table.
     *
     * @param instanceName instanceName
     * @param schemaName schemaName
     * @param tableName tableName
     * @param count count
     * @throws IOException IOException
     */
    public void updateTotalWork(String instanceName, String schemaName, String tableName, long count) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "TOTAL_WORK", String.valueOf(count));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().forPath(metaTablePath, jobj.toString().getBytes());
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, jobj.toString().getBytes());
        }
        logger.info(">>>>>> updateTotalWork metaTablePath: [{}], count: [{}]", metaTablePath, count);

    }

    /**
     * Update SOFAR column in Config table.
     *
     * @param instanceName instanceName
     * @param schemaName schemaName
     * @param tableName tableName
     * @param count count
     * @throws IOException IOException
     */
    public void updateSoFar(String instanceName, String schemaName, String tableName, long count) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        logger.info(">>>>>> metaTablePath,i.e. zk node path is: " + metaTablePath);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "SOFAR", String.valueOf(count));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().withVersion(-1).forPath(metaTablePath, jobj.toString().getBytes());
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, jobj.toString().getBytes());
        }
    }

    /**
     * Update META_FLAG column in Config table.
     *
     * @param instanceName instanceName
     * @param schemaName schemaName
     * @param tableName tableName
     * @param value value
     * @throws IOException IOException
     */
    public void updateMetaFlag(String instanceName, String schemaName, String tableName, int value) throws Exception {
        String metaTablePath = handler.getMetaTablePath(instanceName, schemaName, tableName);
        JsonObject jobj = buildNewJsonObject(metaTablePath, "META_FLAG", String.valueOf(value));
        if (handler.getClient().checkExists().forPath(metaTablePath) != null) {
            handler.getClient().setData().withVersion(-1).forPath(metaTablePath, jobj.toString().getBytes());
        } else {
            handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(metaTablePath, jobj.toString().getBytes());
        }
        System.out.println("updateMetaFlag completed, metaTablePath:  " + metaTablePath + " value=" + value);
        logger.info("updateMetaFlag  metaTablePath: [{}], value: [{}]", metaTablePath, value);
    }

}