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

package org.datatech.baikal.mongosync.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.mongosync.Config;
import org.datatech.baikal.mongosync.bean.SourceJdbcBO;
import org.datatech.baikal.mongosync.common.Executor;
import org.datatech.baikal.mongosync.common.Filter;
import org.datatech.baikal.mongosync.service.MongoService;
import org.datatech.baikal.mongosync.util.MongoUtil;
import org.datatech.baikal.mongosync.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * MongoDB oplog tailing implementation.
 */
@Service
public class MongoServiceImpl implements MongoService {

    @Resource
    private ZkHandler zkHandler;
    private Logger logger = LoggerFactory.getLogger(MongoServiceImpl.class);

    @Override
    public void executeTail() throws Exception {
        Map<String, MongoUtil> cliMap = new HashMap<>(10);
        CuratorFramework cli = zkHandler.getClient();
        logger.info("start tail ...");
        Stat set = cli.checkExists().forPath(Config.PATH_SYNC);
        if (set != null) {
            connectMongo(Filter.getDBConfigs(), cliMap);
        }
        for (String key : cliMap.keySet()) {
            Executor.FIXED_THREAD_POOL.execute(cliMap.get(key));
        }
    }

    private final void connectMongo(Map<String, SourceJdbcBO> config, Map<String, MongoUtil> cliMap) {
        // key is tenant___instance
        config.forEach((key, value) -> {
            MongoUtil util = cliMap.get(key);
            if (util == null) {
                util = new MongoUtil(value.getJDBC_URL(), key);
                cliMap.put(key, util);
            }
        });
    }

}
