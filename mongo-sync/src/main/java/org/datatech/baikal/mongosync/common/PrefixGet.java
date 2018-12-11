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

package org.datatech.baikal.mongosync.common;

import java.util.HashMap;
import java.util.Map;

import org.datatech.baikal.mongosync.bean.ConfigBO;
import org.datatech.baikal.mongosync.util.SpringUtil;
import org.datatech.baikal.mongosync.util.ZkHandler;
import org.datatech.baikal.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide functions to get table prefix from meta store.
 */
public class PrefixGet {
    private static final Logger logger = LoggerFactory.getLogger(PrefixGet.class);
    private final static String prefixFormat = "/metastore/%s/meta/%s/%s/%s";

    private final static String regex = "___";

    private static Map<String, String> PREFIX_CACHE_MAP = new HashMap<>();

    /**
     * Get prefix in zookeeper
     *
     * @param info       node info
     * @param zkHandler  zkHandler
     * @return prefix    prefix
     * @throws Exception Exception
     */
    public static String getPrefix(String[] info, ZkHandler zkHandler) throws Exception {
        final String prefixPath = String.format(prefixFormat, info);
        String data = new String(zkHandler.getClient().getData().forPath(prefixPath));
        ConfigBO config = (ConfigBO) JsonUtil.getObject4JsonString(data, ConfigBO.class);
        if (config != null) {
            final String prefix = config.getPREFIX();
            if (prefix != null && !"".equals(prefix)) {
                return prefix;
            }
        }
        throw new Exception("prefix not find!");
    }

    public synchronized static String[] getPrefix4Cache(String key) {
        final String[] info = key.split(regex);
        String prefix = PREFIX_CACHE_MAP.get(key);
        String[] rt = new String[5];
        rt[0] = info[0];
        rt[1] = info[1];
        rt[2] = info[2];
        rt[3] = info[3];
        if (prefix == null) {
            try {
                ZkHandler zkHandler = SpringUtil.getBean(ZkHandler.class);
                prefix = getPrefix(key.split(regex), zkHandler);
                PREFIX_CACHE_MAP.put(key, prefix);
            } catch (Exception e) {
                logger.error("Exception occurred.", e);
            }
        }
        rt[4] = prefix;
        return rt;
    }

    public synchronized static void updatePrefixInCache(String key, String prefix) {
        PREFIX_CACHE_MAP.put(key, prefix);
    }

}
