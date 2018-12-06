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

package org.datatech.baikal.web.external;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.SimpleDistributedQueue;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.datatech.baikal.web.common.conf.BaseTask;
import org.datatech.baikal.web.common.conf.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.source.ZooKeeperConfigurationSource;

@Service
public class ZkHandler implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ZkHandler.class);

    private SimpleDistributedQueue queueTask;
    @Value("${datalake-ui.zookeeper.quorum}")
    private String zookeeperQuorum;

    private CuratorFramework client;

    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.builder().connectString(zookeeperQuorum).retryPolicy(retryPolicy)
                    .namespace(Config.ZK_NAMESPACE).sessionTimeoutMs(1000 * 6).connectionTimeoutMs(1000 * 6).build();
            client.start();

            String queuePath = getQueuePath(Config.PATH_QUEUE_MAIN_TASK);
            logger.info("task queue path [{}]", queuePath);
            queueTask = new SimpleDistributedQueue(client, queuePath);

            ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(client, Config.PATH_CONFIG);
            zkConfigSource.start();
            DynamicWatchedConfiguration zkDynamicConfig = new DynamicWatchedConfiguration(zkConfigSource);
            ConfigurationManager.install(zkDynamicConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queueTask(String queuePath, BaseTask task) throws Exception {
        Gson gson = new Gson();
        (new SimpleDistributedQueue(client, queuePath)).offer(gson.toJson(task).getBytes());
    }

    public void queueTask(BaseTask task) throws Exception {
        Gson gson = new Gson();
        queueTask.offer(gson.toJson(task).getBytes());
    }

    /**
     * return queue path string with tenant name if configured.
     *
     * @param path path
     * @return a zk path string
     */
    public String getQueuePath(String path) {
        return ZKPaths.makePath(Config.PATH_QUEUE, path);
    }
}