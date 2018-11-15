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

package org.datatech.baikal.mongosync.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.mongosync.Config;
import org.datatech.baikal.mongosync.bean.TimestampBean;
import org.datatech.baikal.mongosync.common.BaseTask;
import org.datatech.baikal.mongosync.common.Enums;
import org.datatech.baikal.mongosync.common.Filter;
import org.datatech.baikal.mongosync.common.PrefixGet;
import org.datatech.baikal.mongosync.common.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.json.JSONException;

/**
 * Handler class providing methods to work with Zookeeper resources.
 */
@Service
public class ZkHandler implements InitializingBean {

    final static Logger logger = LoggerFactory.getLogger(ZkHandler.class);
    Pattern pattern = Pattern.compile(Config.PATTERN_SYNC);
    Pattern aclPattern = Pattern.compile(Config.ACL_PATTERN_SYNC);
    @Value("${mongo-sync.zookeeper.quorum}")
    private String zookeeperQuorum;
    @Value("${mongo-sync.tenant-name}")
    private String tenantName;
    private CuratorFramework client;

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void afterPropertiesSet() {
        client = CuratorFrameworkFactory.builder().retryPolicy(new RetryOneTime(10)).namespace(Config.ZK_NAMESPACE)
                .ensembleProvider(new FixedEnsembleProvider(zookeeperQuorum)).connectionTimeoutMs(6000).build();
        client.start();

        try {
            Filter.init(Config.PATH_SYNC, Enums.DataBaseType.MONGO.value(), this, tenantName);
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
            System.exit(1);
        }

        // monitor ZK nodes update event under specified path
        try {
            setListenterTreeCache(client);
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
            System.exit(1);
        }
    }

    private void setListenterTreeCache(CuratorFramework client) throws Exception {
        // create cache for ZK nodes
        TreeCache treeCache = new TreeCache(client, Config.PATH_SCHEMA);
        // add listener to monitor node update event
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if (data != null) {
                    switch (event.getType()) {
                    case NODE_ADDED:
                        dealData(data);
                        break;
                    case NODE_REMOVED:
                        break;
                    case NODE_UPDATED:
                        dealData(data);
                        break;
                    default:
                        break;
                    }
                } else {
                    logger.debug("data is null : " + event.getType());
                }
            }
        });
        // start monitoring
        treeCache.start();
    }

    private void dealData(ChildData data) throws Exception {
        String path = data.getPath();
        String value = new String(data.getData());
        if (pathMatcher(path)) {
            return;
        }
        logger.info("NODE_ADDED, path [{}], value [{}]", path, value);
        if (StringUtil.isNotEmpty(value)) {
            BaseTask baseTask = null;
            try {
                Object object4JsonString = JsonUtil.getObject4JsonString(value, BaseTask.class);
                if (object4JsonString == null) {
                    logger.info("BaseTask return null");
                    return;
                }
                baseTask = (BaseTask) object4JsonString;
                if (!tenantName.equals(baseTask.getTenantName())) {
                    logger.info("task not for tenant [{}], path [{}]", tenantName, path);
                    return;
                }
                String node = getNodeString(baseTask);
                String newPath = Config.PATH_SYNC + "/" + node;
                Stat check = client.checkExists().forPath(newPath);

                if (TaskType.SECONDARY_TASK.equals(baseTask.getTaskType())) {
                    if (check == null) {
                        TimestampBean timestampBean = new TimestampBean();
                        timestampBean.setOrdinal(0);
                        timestampBean.setTime_t(Long.valueOf(System.currentTimeMillis() / 1000).intValue());
                        client.create().creatingParentsIfNeeded().forPath(newPath,
                                JsonUtil.getJsonString4JavaPOJO(timestampBean).getBytes());
                        // TODO: 6/14/18 to be refined
                        Filter.add(node, Enums.DataBaseType.MONGO.value(), this);
                    }
                    final String key = getNodeString(baseTask);
                    PrefixGet.updatePrefixInCache(key, baseTask.getTablePrefix());
                } else if (TaskType.DELETE_SYNC_TABLE_TASK.equals(baseTask.getTaskType())) {
                    if (check != null) {
                        client.delete().forPath(newPath);
                    }
                    Filter.remove(node);
                }
                client.delete().guaranteed().forPath(path);
            } catch (JSONException e) {
                logger.error("Exception occurred.", e);
            }
        }
    }

    private String getNodeString(BaseTask baseTask) {
        return baseTask.getTenantName() + Config.UNDERLINE_3 + baseTask.getInstanceName() + Config.UNDERLINE_3
                + baseTask.getSchemaName() + Config.UNDERLINE_3 + baseTask.getTableName();
    }

    private boolean pathMatcher(String path) {
        if (StringUtil.isNotEmpty(path)) {
            Matcher m = pattern.matcher(path);
            if (!m.find()) {
                return true;
            }
        }
        return false;
    }

    private boolean aclMatcher(String path) {
        if (StringUtil.isNotEmpty(path)) {
            Matcher m = aclPattern.matcher(path);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

}