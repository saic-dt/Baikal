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

package org.datatech.baikal.task.util;

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.SimpleDistributedQueue;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.ZKPaths;
import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.source.ZooKeeperConfigurationSource;

/**
 * Handler class providing methods to work with Zookeeper resources.
 */
@Service
public class ZkHandler implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ZkHandler.class);

    private SimpleDistributedQueue queueTask;
    private SimpleDistributedQueue queueTaskBackOut;
    @Value("${zookeeper.quorum}")
    private String zookeeperQuorum;
    @Value("${task.queue.ip:@null}")
    private String taskQueueIp;
    @Value("${tenant.name:@null}")
    private String tenantName;
    @Value("${db2parquet.jarfile:@null}")
    private String parquetJarName;
    private CuratorFramework client;
    @Value("${restservice.url:@null}")
    private String restUrl;
    @Value("${spark.driver.memory:@null}")
    private String spark_driver_memory;

    @Value("${spark.executor.memory:@null}")
    private String spark_executor_memory;

    @Value("${spark.executor.cores:@null}")
    private String spark_executor_cores;

    @Value("${spark.default.parallelism:@null}")
    private String spark_default_parallelism;

    @Value("${spark.executor.instances:@null}")
    private String spark_executor_instances;

    public String getSpark_executor_instances() {
        return spark_executor_instances;
    }

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public String getTaskQueueIp() {
        return taskQueueIp;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getParquetJarName() {
        return parquetJarName;
    }

    public String getSpark_driver_memory() {
        return spark_driver_memory;
    }

    public String getSpark_executor_memory() {
        return spark_executor_memory;
    }

    public String getSpark_executor_cores() {
        return spark_executor_cores;
    }

    public String getSpark_default_parallelism() {
        return spark_default_parallelism;
    }

    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = CuratorFrameworkFactory.builder().retryPolicy(new RetryOneTime(10))
                .namespace(Configuration.ZK_NAMESPACE).ensembleProvider(new FixedEnsembleProvider(zookeeperQuorum))
                .connectionTimeoutMs(0).build();
        client.start();
        String queuePath;
        if (taskQueueIp != null) {
            queuePath = getQueuePath(Config.PATH_QUEUE_TASK_PREFIX).concat(taskQueueIp);
        } else {
            queuePath = getQueuePath(Config.PATH_QUEUE_MAIN_TASK);
        }
        logger.info("task queue path [{}]", queuePath);
        queueTask = new SimpleDistributedQueue(client, queuePath);
        queueTaskBackOut = new SimpleDistributedQueue(client, getQueuePath(Config.PATH_QUEUE_TASK_BACK_OUT));
        ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(client,
                Configuration.PATH_CONFIG);
        zkConfigSource.start();
        DynamicWatchedConfiguration zkDynamicConfig = new DynamicWatchedConfiguration(zkConfigSource);
        ConfigurationManager.install(zkDynamicConfig);
    }

    public void queueMessage(String queuePath, byte[] msg) throws Exception {
        (new SimpleDistributedQueue(client, queuePath)).offer(msg);
        logger.info("queue path [{}]", queuePath);
    }

    public byte[] dequeueMessage(String queuePath) throws Exception {
        return (new SimpleDistributedQueue(client, queuePath)).poll();
    }

    public void queueTask(String queuePath, BaseTask task) throws Exception {
        Gson gson = new Gson();
        (new SimpleDistributedQueue(client, queuePath)).offer(Bytes.toBytes(gson.toJson(task)));
    }

    public void queueTask(BaseTask task) throws Exception {
        Gson gson = new Gson();
        queueTask.offer(Bytes.toBytes(gson.toJson(task)));
    }

    public void queueTaskBackOut(BaseTask task) throws Exception {
        Gson gson = new Gson();
        queueTaskBackOut.offer(Bytes.toBytes(gson.toJson(task)));
    }

    public BaseTask dequeueTaskBackOut() throws Exception {
        return dequeueTask(queueTaskBackOut);
    }

    public BaseTask dequeueTask(String queuePath) throws Exception {
        return dequeueTask(new SimpleDistributedQueue(client, queuePath));
    }

    public BaseTask dequeueTask() throws Exception {
        return dequeueTask(queueTask);
    }

    private BaseTask dequeueTask(SimpleDistributedQueue queue) throws Exception {
        byte[] msg = queue.poll();
        if (msg == null) {
            return null;
        } else {
            try {
                Gson gson = new Gson();
                return gson.fromJson(Bytes.toString(msg), BaseTask.class);
            } catch (JsonSyntaxException ex) {
                logger.info("json syntax exception [{}]", ex.getMessage());
                logger.info("msg is [{}]", Bytes.toString(msg));
                return null;
            }
        }
    }

    /**
     * Return application adapter notification path string.
     *
     * @param tenantName   tenantName
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param notifyPath   notifyPath
     * @param prefix       prefix
     * @param schemaPrefix schemaPrefix
     * @return a zk path string
     */
    public String getSchemaPath(String tenantName, String instanceName, String schemaName, String notifyPath,
            String prefix, String schemaPrefix) {
        if (tenantName != null) {
            return ZKPaths.makePath(schemaPrefix, tenantName, instanceName, schemaName, notifyPath, prefix);
        } else {
            return ZKPaths.makePath(schemaPrefix, instanceName, schemaName, notifyPath, prefix);
        }
    }

    /**
     * Return meta table path string.
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @return a zk path string
     */
    public String getMetaTablePath(String instanceName, String schemaName, String tableName) {
        if (tenantName == null) {
            return ZKPaths.makePath(Config.PATH_METASTORE, Config.PATH_META, instanceName, schemaName, tableName);
        } else {
            return ZKPaths.makePath(Config.PATH_METASTORE, tenantName, Config.PATH_META, instanceName, schemaName,
                    tableName);
        }
    }

    public String getAdminPath() {
        return ZKPaths.makePath("users", "admin");
    }

    public String getSourceDBPath(String osIp, String instanceName) {
        return ZKPaths.makePath(Config.PATH_METASTORE, tenantName, Config.PATH_SOURCEDB, osIp, instanceName);

    }

    public String getSourceDBOSIpPath() {
        return ZKPaths.makePath(Config.PATH_METASTORE, tenantName, Config.PATH_SOURCEDB);

    }

    public String getSourceDBInstancePath(String osIp) {
        return ZKPaths.makePath(Config.PATH_METASTORE, tenantName, Config.PATH_SOURCEDB, osIp);

    }

    public String getSourceJdbcPath(String instanceName, String schemaName) {
        return ZKPaths.makePath(Config.PATH_METASTORE, tenantName, Config.PATH_SOURCEJDBC, instanceName, schemaName);

    }

    /**
     * Return queue path string with tenant name if configured.
     *
     * @param path path
     * @return a zk path string
     */
    public String getQueuePath(String path) {
        if (tenantName == null) {
            return ZKPaths.makePath(Configuration.PATH_QUEUE, path);
        } else {
            return ZKPaths.makePath(Configuration.PATH_QUEUE, tenantName, path);
        }
    }
}