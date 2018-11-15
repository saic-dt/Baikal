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
package org.datatech.baikal.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.SourceDb;
import org.datatech.baikal.task.common.TaskStatus;
import org.datatech.baikal.task.dao.MetaConfigDao;
import org.datatech.baikal.task.dao.MonitorSchemaDao;
import org.datatech.baikal.task.dao.MonitorTableDao;
import org.datatech.baikal.task.dao.SourceDbDao;
import org.datatech.baikal.task.processor.BaseTaskProcessor;
import org.datatech.baikal.task.util.Bytes;
import org.datatech.baikal.task.util.DtCmdUtil;
import org.datatech.baikal.task.util.SystemContext;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

/**
 * Task invocation framework.
 */
@Service
public class Framework {

    private static final Logger logger = LoggerFactory.getLogger(Framework.class);

    @Autowired
    private ZkHandler handler;

    @Autowired
    private SystemContext sysContext;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private MonitorSchemaDao monitorSchemaDao;

    @Autowired
    private MonitorTableDao monitorTableDao;

    @Autowired
    private MetaConfigDao metaConfigDao;

    @Autowired
    private DtCmdUtil dtCmdUtil;

    @Autowired
    private SourceDbDao sourceDbDao;

    @Autowired
    private ZkHandler zkHandler;

    /**
     * Get data transfer statistics by invoke a schell script, parse the output of the script, and
     * insert parsed data to monitor tables.
     *
     * @throws IOException IOException
     */
    public void processDtStats() throws Exception {
        // process data transfer monitor statistics
        logger.info("start processing data transfer monitor statistics");
        String cmd = Config.getStringProperty(Config.CFG_DT_CMD_MONITOR, Config.DEFAULT_DT_CMD_MONITOR);
        String dtMonitorCmd = dtCmdUtil.getCmdStr(cmd);
        if (dtMonitorCmd != null) {
            String ip = handler.getTaskQueueIp();
            ArrayList<SourceDb> sourceDbs = sourceDbDao.getAll();
            for (SourceDb sourceDb : sourceDbs) {
                String dbTypeStr = sourceDb.getDB_TYPE().toString();
                String osUser = sourceDb.getOS_USER();
                String basePath = sourceDb.getBASE_PATH();
                String rmtIp = sourceDb.getRMT_IP();
                String osIp = sourceDb.getOS_IP();
                String dbSid = sourceDb.getDB_SID();

                if (((dbTypeStr != null) && (osUser != null) && (basePath != null) && (ip != null))
                        && ((dbTypeStr.equals(DbType.HBASE.name()) && ip.equals(osIp)) || ip.equals(rmtIp))) {
                    // run dbmonitor script when the host is the central replicate node that related
                    // db source extract nodes connected to
                    ProcessBuilder pb = new ProcessBuilder(dtMonitorCmd, dbTypeStr, osUser, osIp, dbSid, basePath);
                    logger.info("start invoking dt cmd [{}]", pb.command().toString());
                    pb.redirectErrorStream(true);
                    Process p = pb.start();
                    InputStream stdout = p.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("stdout: [{}]", line);
                        String[] strs = StringUtils.splitPreserveAllTokens(line, ',');
                        if (strs.length != 10) {
                            logger.warn("column count is [{}], not a valid stats line, ignored", strs.length);
                        } else {
                            if ("PROCESS_STATUS".equals(strs[0]) && !strs[3].isEmpty()) {
                                // schema stats line
                                monitorSchemaDao.insertSchemaStats(strs[1], strs[2], strs[3], strs[4], strs[5], strs[6],
                                        strs[7], strs[8], strs[9]);
                            } else if ("TABLE_LIST".equals(strs[0])) {
                                // table stats line
                                monitorTableDao.insertTableStats(strs[1], strs[2], strs[3], strs[4], strs[5], strs[6],
                                        strs[7], strs[8], strs[9]);
                            } else {
                                logger.warn("not a valid stats line, ignored");
                            }
                        }
                    }
                }
            }
            logger.info("dt cmd invoked [{}] time(s)", sourceDbs.size());
        }
    }

    /**
     * Get task from zookeeper queue and invoke related task processor, task will be processed
     * with exception retry control and concurrent execution control.
     *
     * @throws Exception Exception
     */
    public void processGeneralTask() throws Exception {
        TaskStatus ts = getTaskStatus();
        BaseTask currentTask = null;
        if (ts != null) {
            if (ts.getFailedTask() != null) {
                currentTask = ts.getFailedTask();
                int maxTaskRetry = Config.getIntProperty(Config.CFG_MAX_TASK_RETRY,
                        Config.DEFAULT_MAX_TASK_RETRY);
                if (currentTask.getRetryCount() >= maxTaskRetry) {
                    logger.info("the failed task [{}] has reached max retry count [{}], discarded",
                            currentTask.toJson(), maxTaskRetry);
                    // update META_FLAG for failed task status
                    String instanceName = currentTask.getInstanceName();
                    String schemaName = currentTask.getSchemaName();
                    String tableName = currentTask.getTableName();
                    if ((instanceName != null) && (schemaName != null) && (tableName != null)) {
                        String configRowKey = String.join(Config.DELIMITER, instanceName, schemaName, tableName);
                        int flagValue;
                        switch (currentTask.getTaskType()) {
                        case MAIN_TASK:
                        case SECONDARY_TASK:
                            flagValue = Config.META_FLAG_FULLDUMP_FAILED;
                            break;
                        default:
                        }
                        switch (currentTask.getTaskType()) {
                        case MAIN_TASK:
                        case SECONDARY_TASK:
                            flagValue = Config.META_FLAG_FULLDUMP_FAILED;
                            break;
                        case REFRESH_TABLE_TASK:
                            flagValue = Config.META_FLAG_REFRESH_TABLE_FAILED;
                            break;
                        default:
                            flagValue = Config.META_FLAG_TASK_FAILED;
                            break;
                        }
                        metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, flagValue);
                    }
                    currentTask = null;
                }
            } else {
                // check task slot available
                int maxTask = Config.getIntProperty(Config.CFG_MAX_TASK, Config.DEFAULT_MAX_TASK);
                int maxTaskPerHost = Config.getIntProperty(Config.CFG_MAX_TASK_PER_HOST,
                        Config.DEFAULT_MAX_TASK_PER_HOST);
                logger.info("max task slot is [{}], max task slot per host is [{}]", maxTask, maxTaskPerHost);
                logger.info("current task count is [{}], current task count on this host is [{}]", ts.getTotalTasks(),
                        ts.getTotalTasksOnHost());
                if (ts.getTotalTasks() >= maxTask || (ts.getTotalTasksOnHost() >= maxTaskPerHost)) {
                    logger.info("reach max task count");
                    return;
                }
            }

            if (currentTask == null) {
                currentTask = handler.dequeueTask();
                if (currentTask != null) {
                    if (currentTask.getTaskType() == null) {
                        logger.info("task type is unknown, msg [{}] ignored", currentTask.toJson());
                        return;
                    }
                }
            }

            if (currentTask != null) {
                // task msg exists, proceeding
                try {
                    String hostname = sysContext.getHostName();
                    // tenant node under ZK task node
                    String hostPath = ZKPaths.makePath(Config.PATH_TASK, zkHandler.getTenantName(), hostname);
                    String pid = sysContext.getPid();
                    String processPath = ZKPaths.makePath(hostPath, pid);
                    String beginPath = ZKPaths.makePath(processPath, Config.PATH_BEGIN);
                    String processingPath = ZKPaths.makePath(processPath, Config.PATH_PROCESSING);
                    try {
                        handler.getClient().create().creatingParentsIfNeeded().forPath(processPath);
                    } catch (KeeperException.NodeExistsException ex) {
                        logger.info("pid path already exists, it not normal, but continue anyway");
                    }

                    if (Config.ZK_TX_SUPPORT) {
                        // curator transaction not supported using cdh library 5.12
                        CuratorOp beginOp = handler.getClient().transactionOp().create().forPath(beginPath,
                                Bytes.toBytes(currentTask.toJson()));

                        CuratorOp processingOp = handler.getClient().transactionOp().create()
                                .withMode(CreateMode.EPHEMERAL).forPath(processingPath);
                        Collection<CuratorTransactionResult> results = handler.getClient().transaction()
                                .forOperations(beginOp, processingOp);
                        for (CuratorTransactionResult result : results) {
                            logger.info("task creation result [{}] - [{}]", result.getForPath(), result.getType());
                        }
                    } else {
                        handler.getClient().create().withMode(CreateMode.EPHEMERAL).forPath(processingPath);
                        logger.info("created [{}]", processingPath);
                        handler.getClient().create().forPath(beginPath, Bytes.toBytes(currentTask.toJson()));
                        logger.info("created [{}]", beginPath);
                    }

                    // begin task
                    Class<? extends BaseTaskProcessor> c = currentTask.getTaskType().getProcessor();
                    logger.info("begin task, task processor is [{}], task data is [{}]", c, currentTask.toJson());
                    appContext.getBean(c).execute(currentTask);

                    // end task
                    handler.getClient().delete().deletingChildrenIfNeeded().forPath(processPath);
                    logger.info("task finished");
                } catch (Exception ex) {
                    logger.error("exception occurred during task processing, put task back to queue", ex);
                    // put to back out to queue if exception occurs below
                    handler.queueTaskBackOut(currentTask);
                }
            } else {
                logger.info("no task on queue or no failed task, exit");
            }

        }
    }

    private TaskStatus getTaskStatus() throws Exception {
        List<String> hostNodes;
        TaskStatus ts = new TaskStatus();
        String hostname = sysContext.getHostName();
        int taskCount = 0;
        int hostTaskCount = 0;
        try {
            String taskPath = ZKPaths.makePath(Config.PATH_TASK, zkHandler.getTenantName());
            hostNodes = handler.getClient().getChildren().forPath(taskPath);
            for (String node : hostNodes) {
                List<String> processNodes;
                try {
                    String thisPath = ZKPaths.makePath(taskPath, node);
                    processNodes = handler.getClient().getChildren().forPath(thisPath);
                    for (String processNode : processNodes) {
                        String processPath = ZKPaths.makePath(thisPath, processNode);
                        String beginPath = ZKPaths.makePath(processPath, Config.PATH_BEGIN);
                        String processingPath = ZKPaths.makePath(processPath, Config.PATH_PROCESSING);
                        Stat beginStat = handler.getClient().checkExists().forPath(beginPath);
                        Stat processingStat = handler.getClient().checkExists().forPath(processingPath);

                        if ((beginStat != null) && (processingStat == null) && (node.equals(hostname))
                                && (ts.getFailedTask() == null)) {
                            // failure tasks exists, on same node and its the first failed task
                            try {
                                byte[] taskData = handler.getClient().getData().forPath(beginPath);
                                handler.getClient().delete().deletingChildrenIfNeeded().forPath(processPath);
                                Gson gson = new Gson();
                                BaseTask failedTask = gson.fromJson(Bytes.toString(taskData), BaseTask.class);
                                failedTask.setRetryCount(failedTask.getRetryCount() + 1);
                                logger.info("failure tasks exists, task data is [{}]", failedTask.toJson());
                                ts.setFailedTask(failedTask);
                            } catch (KeeperException.NoNodeException dummy) {
                                // another client remove the process path first, try next
                                logger.info("another client remove the process path first, try next");
                            }
                        }

                        if ((beginStat != null) && (processingStat != null)) {
                            // running task exist
                            taskCount += 1;
                            if (node.equals(hostname)) {
                                // task running on current host exist
                                hostTaskCount += 1;
                            }
                        }

                    }
                } catch (KeeperException.NoNodeException dummy) {
                    // no node under task host path, try next
                    // may need to remove this node later
                }
            }

        } catch (KeeperException.NoNodeException dummy) {
            // no node under task path
        }
        ts.setTotalTasks(taskCount);
        ts.setTotalTasksOnHost(hostTaskCount);
        return ts;
    }

}
