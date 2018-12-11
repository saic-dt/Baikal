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

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.DtCmdType;
import org.datatech.baikal.task.common.SourceDb;
import org.datatech.baikal.task.common.SourceJdbc;
import org.datatech.baikal.task.dao.EventLogDao;
import org.datatech.baikal.task.dao.MetaConfigDao;
import org.datatech.baikal.task.dao.SourceDbDao;
import org.datatech.baikal.task.dao.SourceJdbcDao;
import org.datatech.baikal.task.util.Bytes;
import org.datatech.baikal.task.util.DtCmdUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Task follow up the main task to launch full dump spark job.
 */
@Service
public class SecondaryTaskProcessor extends BaseTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SecondaryTaskProcessor.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
    private static final SimpleDateFormat TZ_FORMAT = new SimpleDateFormat("XXX");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat DB2_DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int TZ_OFFSET = TimeZone.getDefault().getRawOffset();
    @Autowired
    private ZkHandler handler;
    @Autowired
    private MetaConfigDao metaConfigDao;
    @Autowired
    private EventLogDao eventLogDao;
    @Autowired
    private DtCmdUtil dtCmdUtil;
    @Autowired
    private SourceJdbcDao sourceJdbcDbDao;
    @Autowired
    private SourceDbDao sourceDbDao;
    @Autowired
    private ZkHandler zkHandler;

    private static void dumpInput(InputStream input) throws Exception {
        byte[] buff = new byte[1024];
        while (true) {
            int len = input.read(buff);
            if (len < 0) {
                break;
            }
            logger.debug(new String(buff, 0, len));
        }
    }

    /**
     * Perform full dump after application adapter has been notified. dt console script
     * will be invoked before and after the full dump.
     *
     * @param task task
     * @throws Exception Exception
     */
    @Override
    public void execute(BaseTask task) throws Exception {
        String instanceName = task.getInstanceName();
        String schemaName = task.getSchemaName();
        String tableName = task.getTableName();
        String tenantName = zkHandler.getTenantName();
        String seqNode = task.getSeqNode();
        String tablePrefix = task.getTablePrefix();
        SourceJdbc sourceJdbc = sourceJdbcDbDao.get(instanceName, schemaName);
        String tableVersion = metaConfigDao.getTableVersion(instanceName, schemaName, tableName);
        DbType dbType = sourceJdbc.getDB_TYPE();
        String seqPath;
        if (Config.DB_TYPE_MONGO.equals(dbType.name())) {
            logger.info("dbType is MONGO");
            seqPath = handler.getSchemaPath(tenantName, instanceName, schemaName, Config.PATH_NOTIFY, seqNode,
                    Configuration.PATH_MONGO_SCHEMA);
        } else if (Config.DB_TYPE_MYSQL.equals(dbType.name())) {
            logger.info("dbType is MYSQL");
            seqPath = handler.getSchemaPath(tenantName, instanceName, schemaName, Config.PATH_NOTIFY, seqNode,
                    Configuration.PATH_MYSQL_SCHEMA);
        } else {
            logger.info("dbType is others");
            seqPath = handler.getSchemaPath(tenantName, instanceName, schemaName, Config.PATH_NOTIFY, seqNode,
                    Configuration.PATH_SCHEMA);
        }
        Stat seqStat = handler.getClient().checkExists().forPath(seqPath);
        if (seqStat != null) {
            long createTs = seqStat.getCtime();
            long currentTs = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Timestamp ts = new Timestamp(currentTs);
            logger.debug("currentTs [{}]", sdf.format(ts));
            ts = new Timestamp(createTs);
            logger.debug("createTs [{}]", sdf.format(ts));
            int timeout = Config.getIntProperty(Config.CFG_TASK_WAIT_TIMEOUT, Config.DEFAULT_TASK_WAIT_TIMEOUT);
            logger.debug("CFG_TASK_WAIT_TIMEOUT [{}]", timeout);
            String warnMessage = "the sequential node still exists for [{}] seconds, task discarded "
                    + "and set meta_flag to task timeout";
            if ((currentTs - createTs) >= (timeout * 1000)) {
                logger.info(warnMessage, timeout);
                logger.info(
                        "SecondaryTaskProcessor updateMetaFlag: instanceName [{}], schemaName [{}], tableName [{}], flagValue [{}]",
                        instanceName, schemaName, tableName, Config.META_FLAG_SECONDARY_TASK_TIMEOUT);
                metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName,
                        Config.META_FLAG_SECONDARY_TASK_TIMEOUT);
            } else {
                logger.info("the sequential node still exists, put task [{}] back to queue", task.toJson());
                task.setWaitCycles(task.getWaitCycles() + 1);
                handler.queueTask(task);
            }
            return;
        }
        String osIp = sourceJdbc.getOS_IP();
        String jdbcUrl = sourceJdbc.getJDBC_URL();
        String user = sourceJdbc.getUSER();
        String password = sourceJdbc.getPASSWORD();
        String osUser = null;
        String basePath = null;
        String ts = "ts".concat(tableVersion);
        String zk_quorum = handler.getZookeeperQuorum();
        String task_queueip = handler.getTaskQueueIp();
        if ((dbType != null) && (osIp != null)) {
            if ((!Config.DB_TYPE_MONGO.equals(dbType.name()))) {
                logger.info("dbType name [{}]", dbType.name());
                SourceDb sourceDb = sourceDbDao.get(osIp, instanceName);
                osUser = sourceDb.getOS_USER();
                basePath = sourceDb.getBASE_PATH();
                dtCmdUtil.executeConsoleCmd(DtCmdType.E_ADD_TRAN, dbType, osUser, osIp, instanceName, basePath,
                        schemaName, tableName);
            }
        } else {
            logger.warn(
                    "skip execute dt console command, "
                            + "can not get db type and ip for instance name [{}], dbType: [{}], osIp: [{}]",
                    instanceName, dbType, osIp);
        }
        // start full dump
        doFullDump(instanceName, schemaName, tableName, tablePrefix, dbType.name(), jdbcUrl, user, password, zk_quorum,
                task_queueip, tenantName, ts);

        // notify application adapter that full dump has ended
        String seqPrefixPath = handler.getSchemaPath(tenantName, instanceName, schemaName, Config.PATH_NOTIFY_END,
                Configuration.PATH_SEQ_PREFIX, Configuration.PATH_SCHEMA);
        handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath(seqPrefixPath, Bytes.toBytes(task.toJson()));
        logger.info("notified application adapter that full dump has ended");

        // execute dt command to remove allcols trandata for E process
        if ((dbType != null) && (osIp != null)) {
            if ((!Config.DB_TYPE_MONGO.equals(dbType.name()))) {
                dtCmdUtil.executeConsoleCmd(DtCmdType.E_SEM_TRAN, dbType, osUser, osIp, instanceName, basePath,
                        schemaName, tableName);
            }
        }
    }

    private void doFullDump(String instanceName, String schemaName, String tableName, String tablePrefix, String dbType,
            String jdbcUrl, String user, String password, String zk_quorum, String task_queueip, String tenantName,
            String ts) throws Exception {

        String parquetJarName = handler.getParquetJarName().trim();
        logger.info("Fulldump started instanceName [{}]", instanceName);
        logger.info("Fulldump started schemaName [{}]", schemaName);
        logger.info("Fulldump started tableName [{}]", tableName);
        logger.info("Fulldump started tablePrefix [{}]", tablePrefix);
        logger.info("Fulldump started dbType [{}]", dbType);
        logger.info("Fulldump started jdbcUrl [{}]", jdbcUrl);
        logger.info("Fulldump started user [{}]", user);
        logger.info("Fulldump started password [{}]", password);
        logger.info("Fulldump started zk_quorum [{}]", zk_quorum);
        logger.info("Fulldump started task_queueip [{}]", task_queueip);
        logger.info("Fulldump started tenantName [{}]", tenantName);
        logger.info("Fulldump started parquetJarName [{}]", parquetJarName);
        logger.info("Fulldump started ts [{}]", ts);
        File file = new File(parquetJarName);
        if (!file.exists()) {
            logger.info("Parquet jar file does not exists, file name [{}]", parquetJarName);
            metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, Config.META_FLAG_FULLDUMP_FAILED);
            return;
        }
        SparkLauncher launcher = new SparkLauncher();
        launcher.setAppResource(parquetJarName);
        launcher.setMainClass("org.datatech.baikal.fulldump.FulldumpMain");
        launcher.addAppArgs(instanceName, schemaName, tableName, tablePrefix, dbType, jdbcUrl, user, password,
                zk_quorum, task_queueip, tenantName, ts);
        // start spark job on yarn-cluster, use local if necessary
        launcher.setMaster("yarn");
        launcher.setVerbose(false);
        launcher.setConf(SparkLauncher.DRIVER_MEMORY, handler.getSpark_driver_memory());
        launcher.setConf(SparkLauncher.EXECUTOR_MEMORY, handler.getSpark_executor_memory());
        launcher.setConf(SparkLauncher.EXECUTOR_CORES, handler.getSpark_executor_cores());
        launcher.setConf("spark.default.parallelism", handler.getSpark_default_parallelism());
        launcher.setConf("spark.executor.instances", handler.getSpark_executor_instances());
        SparkAppHandle handle = launcher.startApplication();
        int retrytimes = 0;
        boolean sparkjobfailed_flag = false;
        while (handle.getState() != SparkAppHandle.State.FINISHED) {
            retrytimes++;
            Thread.sleep(5000L);
            logger.info("applicationId [{}]", handle.getAppId());
            logger.info("current state [{}]", handle.getState());
            if ((handle.getAppId() == null && handle.getState() == SparkAppHandle.State.FAILED) && retrytimes > 8) {
                logger.info("can not start spark job for parquetJarName. Fulldump failed. ");
                metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, Config.META_FLAG_FULLDUMP_FAILED);
                sparkjobfailed_flag = true;
                break;
            }
        }
        logger.info("launcher over");

        if (!sparkjobfailed_flag) {
            String message = String.format(
                    "full dump finished for instance [%s], schema [%s], " + "table [%s], hbase row key prefix [%s]",
                    instanceName, schemaName, tableName, tablePrefix);
            eventLogDao.insertEvent(instanceName, schemaName, tableName, message);
        }
    }
}
