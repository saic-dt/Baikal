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

import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.datatech.baikal.common.Bytes;
import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.MetaConfig;
import org.datatech.baikal.task.common.SourceJdbc;
import org.datatech.baikal.task.common.TaskType;
import org.datatech.baikal.task.dao.EventLogDao;
import org.datatech.baikal.task.dao.MetaConfigDao;
import org.datatech.baikal.task.dao.SourceJdbcDao;
import org.datatech.baikal.task.util.HiveUtil;
import org.datatech.baikal.task.util.ProcessorUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Main task is the entry point task for co-ordinate full dump and incremental log transfer.
 */
@Service
public class MainTaskProcessor extends BaseTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MainTaskProcessor.class);

    @Autowired
    private ZkHandler zkHandler;

    @Autowired
    private MetaConfigDao metaConfigDao;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private HiveUtil hiveUtil;

    @Autowired
    private SourceJdbcDao sourceJdbcDao;

    /**
     * Create full dump table meta in hbase Config table, inform application adapter via
     * zookeeper sequential node.
     *
     * @param task task
     * @throws Exception Exception
     */
    @Override
    public void execute(BaseTask task) throws Exception {
        // create zk sequential node first
        String tenantName = zkHandler.getTenantName();
        String instanceName = task.getInstanceName();
        String schemaName = task.getSchemaName();
        String tableName = task.getTableName();
        String seqPrefixPath = zkHandler.getSchemaPath(tenantName, instanceName, schemaName, Config.PATH_PREFIX_SEQ,
                Configuration.PATH_SEQ_PREFIX, Configuration.PATH_SCHEMA);
        String seqPath = zkHandler.getClient().create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(seqPrefixPath);
        String seqNode = ZKPaths.getNodeFromPath(seqPath);
        String tablePrefix = ProcessorUtil.shortenTableName(seqNode);

        // notify application adapter
        BaseTask secondaryTask = new BaseTask();
        secondaryTask.setTenantName(tenantName);
        secondaryTask.setTaskType(TaskType.SECONDARY_TASK);
        secondaryTask.setInstanceName(instanceName);
        secondaryTask.setSchemaName(schemaName);
        secondaryTask.setTableName(tableName);
        secondaryTask.setTablePrefix(tablePrefix);

        long ts = System.currentTimeMillis();
        MetaConfig meta = new MetaConfig(ts, instanceName, schemaName, tableName, tablePrefix,
                Config.META_FLAG_FULLDUMP_BEGIN);
        SourceJdbc sourceJdbc = sourceJdbcDao.get(instanceName, schemaName);
        if (!sourceJdbc.getDB_TYPE().name().equals(Config.DB_TYPE_MONGO)) {
            String pks = metaConfigDao.getSoucePk(instanceName, schemaName, tableName);
            meta.setSOURCE_PK(pks);
        } else {
            String pks = "_id";
            meta.setSOURCE_PK(pks);
        }
        metaConfigDao.insertMetaConfig(meta);
        logger.info("update zookeeper meta table with timestamp [{}]", ts);

        // execute hive ddl to map parquet files
        String tableVersion = "ts".concat(metaConfigDao.getTableVersion(instanceName, schemaName, tableName));
        String location = hiveUtil.getParquetLocation(tenantName, instanceName, schemaName, tableName, tablePrefix,
                tableVersion);

        hiveUtil.createHiveTable(instanceName, schemaName, tableName, location);

        // execute spark ddl to create spark table
        hiveUtil.createSparkDatabase(instanceName, schemaName, tableName);
        if (hiveUtil.failedflag) {
            logger.info("job failed due to spark job process failed");
            return;
        }
        hiveUtil.createSparkTable(instanceName, schemaName, tableName, tablePrefix);
        if (hiveUtil.failedflag) {
            logger.info("job failed due to spark job process failed");
            return;
        }
        String schemaPrefix = Configuration.PATH_SCHEMA;
        if (sourceJdbc != null) {
            if (Config.DB_TYPE_MONGO.equals(sourceJdbc.getDB_TYPE().name())) {
                schemaPrefix = Configuration.PATH_MONGO_SCHEMA;
            } else if (Config.DB_TYPE_MYSQL.equals(sourceJdbc.getDB_TYPE().name()) && sourceJdbc.getUSE_CANAL()) {
                schemaPrefix = Configuration.PATH_MYSQL_SCHEMA;
            }
        }
        seqPrefixPath = zkHandler.getSchemaPath(tenantName, instanceName, schemaName, Config.PATH_NOTIFY,
                Configuration.PATH_SEQ_PREFIX, schemaPrefix);
        seqPath = zkHandler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath(seqPrefixPath, Bytes.toBytes(secondaryTask.toJson()));
        logger.info("created zk sequential node [{}] and notified application adapter", seqPath);

        // set notify zk sequential node path to task data
        seqNode = ZKPaths.getNodeFromPath(seqPath);
        secondaryTask.setSeqNode(seqNode);

        // put secondary task to queue
        zkHandler.queueTask(secondaryTask);
        logger.info("put secondary task to queue");

        String message = String.format(
                "main task finished for instance [%s], schema [%s], " + "table [%s], hbase row key prefix [%s]",
                instanceName, schemaName, tableName, tablePrefix);
        eventLogDao.insertEvent(instanceName, schemaName, tableName, message);
    }

}
