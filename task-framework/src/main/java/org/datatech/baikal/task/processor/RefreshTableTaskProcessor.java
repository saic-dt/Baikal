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

import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.dao.EventLogDao;
import org.datatech.baikal.task.dao.MetaConfigDao;
import org.datatech.baikal.task.util.HiveUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Task to refresh the schema of specific table schema in data lake.
 */
@Service
public class RefreshTableTaskProcessor extends BaseTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTableTaskProcessor.class);

    @Autowired
    private HiveUtil hiveUtil;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private MetaConfigDao metaConfigDao;

    @Autowired
    private ZkHandler handler;

    /**
     * Reload source table schema and recreate hive/spark table.
     *
     * @param task task
     * @throws Exception Exception
     */
    @Override
    public void execute(BaseTask task) throws Exception {
        String instanceName = task.getInstanceName();
        String schemaName = task.getSchemaName();
        String tableName = task.getTableName();
        String tenantName = handler.getTenantName();
        String tablePrefix = task.getTablePrefix();

        String tableVersion = "ts".concat(metaConfigDao.getTableVersion(instanceName, schemaName, tableName));
        String location = hiveUtil.getParquetLocation(tenantName, instanceName, schemaName, tableName, tablePrefix,
                tableVersion);
        logger.info("start create HiveTable...");
        hiveUtil.createHiveTable(instanceName, schemaName, tableName, location);
        hiveUtil.createSparkDatabase(instanceName, schemaName, tableName);
        hiveUtil.createSparkTable(instanceName, schemaName, tableName, tablePrefix);
        logger.info("RefreshTableTaskProcessor updateMetaFlag: instanceName [{}], schemaName [{}], tableName [{}], flagValue [{}]",
            instanceName, schemaName, tableName, Config.META_FLAG_FULLDUMP_END);
        metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, Config.META_FLAG_FULLDUMP_END);

        String message = String.format("refresh table task finished for instance [%s], schema [%s], " + "table [%s]",
                instanceName, schemaName, tableName);
        eventLogDao.insertEvent(instanceName, schemaName, tableName, message);
    }
}
