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
package org.datatech.baikal.task.processor;

import org.apache.zookeeper.CreateMode;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.EventType;
import org.datatech.baikal.task.common.SourceJdbc;
import org.datatech.baikal.task.dao.EventLogDao;
import org.datatech.baikal.task.dao.SourceJdbcDao;
import org.datatech.baikal.task.util.HiveUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Task to delete table on data lake.
 */
@Service
public class DeleteTableTaskProcessor extends BaseTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DeleteTableTaskProcessor.class);

    @Autowired
    private HiveUtil hiveUtil;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private ZkHandler zkHandler;

    @Autowired
    private SourceJdbcDao sourceJdbcDao;

    /**
     * Perform hive table delete.
     *
     * @param task task
     * @throws Exception Exception
     */
    @Override
    public void execute(BaseTask task) throws Exception {
        String tenantName = zkHandler.getTenantName();
        String instanceName = task.getInstanceName();
        String schemaName = task.getSchemaName();
        String tableName = task.getTableName();
        String mongoSync = "/mongo-schema" + "/" + tenantName + "/" + instanceName + "/" + schemaName + "/" + "notify"
                + "/" + Config.PATH_SEQ_PREFIX;
        String mysqlSync = "/canal-schema" + "/" + tenantName + "/" + instanceName + "/" + schemaName + "/" + "notify"
                + "/" + Config.PATH_SEQ_PREFIX;
        hiveUtil.dropHiveTable(instanceName, schemaName, tableName);
        hiveUtil.dropSparkTable(instanceName, schemaName, tableName);
        JSONObject json = new JSONObject();
        json.put("tenantName", tenantName);
        json.put("instanceName", instanceName);
        json.put("schemaName", schemaName);
        json.put("tableName", tableName);
        json.put("taskType", EventType.DELETE_SYNC_TABLE_TASK);
        String notifyMessage = JSON.toJSONString(json);
        SourceJdbc sourceJdbc = sourceJdbcDao.get(instanceName, schemaName);
        if (sourceJdbc.getDB_TYPE().name().equals(Config.DB_TYPE_MONGO)) {
            zkHandler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(mongoSync, notifyMessage.getBytes());
        }
        if (sourceJdbc.getDB_TYPE().name().equals(Config.DB_TYPE_MYSQL)) {
            zkHandler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(mysqlSync, notifyMessage.getBytes());
        }
        String message = String.format("delete table task finished for instance [%s], schema [%s], " + "table [%s]",
                instanceName, schemaName, tableName);
        eventLogDao.insertEvent(instanceName, schemaName, tableName, message);
    }
}
