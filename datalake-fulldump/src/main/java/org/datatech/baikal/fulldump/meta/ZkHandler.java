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

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.ZKPaths;
import org.datatech.baikal.fulldump.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler class providing methods to work with Zookeeper resources.
 */
public class ZkHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZkHandler.class);

    private String zookeeperQuorum;
    private String taskQueueIp;
    private String tenantName;
    private CuratorFramework client;

    public ZkHandler(String zk_quorum, String task_queueip, String tenantName) {

        this.zookeeperQuorum = zk_quorum;
        this.taskQueueIp = task_queueip;
        this.tenantName = tenantName;

        client = CuratorFrameworkFactory.builder().retryPolicy(new RetryOneTime(10)).namespace(Config.ZK_NAMESPACE)
                .ensembleProvider(new FixedEnsembleProvider(zookeeperQuorum)).connectionTimeoutMs(10000).build();
        client.start();

        String queuePath;
        if (taskQueueIp != null) {
            queuePath = getQueuePath(Config.PATH_QUEUE_TASK_PREFIX).concat(taskQueueIp);
        } else {
            queuePath = getQueuePath(Config.PATH_QUEUE_MAIN_TASK);
        }
        logger.info(">>>>>> task queue path [{}]", queuePath);
    }

    /**
     * return meta table path string.
     * @param instanceName instanceName
     * @param schemaName schemaName
     * @param tableName tableName
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

    /**
     * return queue path string with tenant name if configured.
     * @param path path
     * @return a zk path string
     */
    public String getQueuePath(String path) {
        if (tenantName == null) {
            return ZKPaths.makePath(Config.PATH_QUEUE, path);
        } else {
            return ZKPaths.makePath(Config.PATH_QUEUE, tenantName, path);
        }
    }

    public CuratorFramework getClient() {
        return client;
    }

}