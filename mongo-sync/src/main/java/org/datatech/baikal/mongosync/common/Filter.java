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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.mongosync.Config;
import org.datatech.baikal.mongosync.bean.SourceJdbcBO;
import org.datatech.baikal.mongosync.bean.TimestampBean;
import org.datatech.baikal.mongosync.util.MongoUtil;
import org.datatech.baikal.mongosync.util.ZkHandler;
import org.datatech.baikal.util.JsonUtil;
import org.datatech.baikal.util.StringUtil;

/**
 * Provide functions to manage MongoDB oplog sync process.
 */
public class Filter {

    private final static String point = ".";
    private static Map<String, ConcurrentSkipListSet<String>> FILTER_MAP = new ConcurrentHashMap<>();
    private static Map<String, Map<String, TimestampBean>> MIN_TIME_MAP = new ConcurrentHashMap<>();
    private static Map<String, SourceJdbcBO> DB_CONFIG_MAP = new ConcurrentHashMap<>();

    /**
     * Get all nodes under ZK path /datalake/mongo/.sync and initialize filter map with these nodes
     * @param nodes ZK path, for example /datalake/mongo/.sync
     * @param fdbtype MongoDB type string
     * @param zkHandler zkHandler
     * @param tenantName tenant name
     * @throws Exception Exception
     */
    public static synchronized void init(String nodes, final String fdbtype, ZkHandler zkHandler, String tenantName)
            throws Exception {
        Stat stat = zkHandler.getClient().checkExists().forPath(nodes);
        if (stat == null) {
            return;
        }
        List<String> childNodes = zkHandler.getClient().getChildren().forPath(nodes);
        init(childNodes, fdbtype, zkHandler, tenantName);
    }

    private static synchronized void init(List<String> childNodes, final String fdbtype, ZkHandler zkHandler,
            String tenantName) throws Exception {
        Map<String, ConcurrentSkipListSet<String>> newFilter = new ConcurrentHashMap<>(30);
        Map<String, Map<String, TimestampBean>> timeFilter = new ConcurrentHashMap<>(30);
        Map<String, SourceJdbcBO> newConfig = new ConcurrentHashMap<>(30);
        for (String node : childNodes) {
            // node format is <tenant>___<instance>___<schema>___<table>
            String[] info = node.split(Config.UNDERLINE_3);
            if (!tenantName.equals(info[0])) {
                continue;
            }
            String timeData = new String(zkHandler.getClient().getData().forPath(Config.PATH_SYNC + "/" + node));
            TimestampBean timestampBean = null;
            if (StringUtil.isNotEmpty(timeData)) {
                timestampBean = (TimestampBean) JsonUtil.getObject4JsonString(timeData, TimestampBean.class);
            } else {
                timestampBean = new TimestampBean();
                timestampBean.setTime_t(Long.valueOf(System.currentTimeMillis() / 1000).intValue());
                timestampBean.setOrdinal(0);
            }

            String data = new String(zkHandler.getClient().getData()
                    .forPath(String.format(Config.PATHFORMAT, info[0], info[1], info[2])));
            SourceJdbcBO sourceJdbc = (SourceJdbcBO) JsonUtil.getObject4JsonString(data, SourceJdbcBO.class);
            if (sourceJdbc != null) {
                sourceJdbc.setTenantName(info[0]);
                final String url = sourceJdbc.getJDBC_URL();
                final String dbType = sourceJdbc.getDB_TYPE();
                if (url != null && fdbtype.equals(dbType) && !"".equals(url)) {
                    // format is tenant___instance
                    final String filterKey = info[0] + Config.UNDERLINE_3 + info[1];
                    // format is schema.table
                    final String filterValue = info[2] + point + info[3];
                    ConcurrentSkipListSet<String> skipListSet = newFilter.get(filterKey);
                    if (skipListSet == null) {
                        newConfig.put(filterKey, sourceJdbc);
                        skipListSet = new ConcurrentSkipListSet<>();
                        skipListSet.add(filterValue);
                        newFilter.put(filterKey, skipListSet);
                    } else {
                        skipListSet.add(filterValue);
                    }

                    Map<String, TimestampBean> timeListMap = timeFilter.get(filterKey);
                    if (StringUtil.isNull(timeListMap)) {
                        Map<String, TimestampBean> timesMap = new HashMap<>();
                        timesMap.put(filterValue, timestampBean);
                        timeFilter.put(filterKey, timesMap);
                    } else {
                        timeListMap.put(filterValue, timestampBean);
                    }
                }
            }
        }
        FILTER_MAP.clear();
        FILTER_MAP = newFilter;
        DB_CONFIG_MAP.clear();
        DB_CONFIG_MAP = newConfig;
        MIN_TIME_MAP.clear();
        MIN_TIME_MAP = timeFilter;
    }

    public static void add(final String node, final String fdbType, ZkHandler zkHandler) throws Exception {
        String[] info = node.split(Config.UNDERLINE_3);
        String data = new String(
                zkHandler.getClient().getData().forPath(String.format(Config.PATHFORMAT, info[0], info[1], info[2])));
        SourceJdbcBO sourceJdbc = (SourceJdbcBO) JsonUtil.getObject4JsonString(data, SourceJdbcBO.class);
        if (sourceJdbc != null) {
            sourceJdbc.setTenantName(info[0]);
            final String url = sourceJdbc.getJDBC_URL();
            final String dbType = sourceJdbc.getDB_TYPE();
            if (url != null && fdbType.equals(dbType) && !"".equals(url)) {
                final String filterKey = info[0] + Config.UNDERLINE_3 + info[1];
                final String filterValue = info[2] + point + info[3];
                ConcurrentSkipListSet<String> skipListSet = FILTER_MAP.get(filterKey);
                Map<String, TimestampBean> timeListMap = MIN_TIME_MAP.get(filterKey);
                TimestampBean timestampBean = new TimestampBean();
                timestampBean.setTime_t(Long.valueOf(System.currentTimeMillis() / 1000).intValue());
                timestampBean.setOrdinal(0);
                if (StringUtil.isNull(timeListMap)) {
                    Map<String, TimestampBean> timesMap = new HashMap<>();
                    timesMap.put(filterValue, timestampBean);
                    MIN_TIME_MAP.put(filterKey, timesMap);
                } else {
                    MIN_TIME_MAP.get(filterKey).put(filterValue, timestampBean);
                }
                if (skipListSet == null) {
                    DB_CONFIG_MAP.put(filterKey, sourceJdbc);
                    skipListSet = new ConcurrentSkipListSet<>();
                    skipListSet.add(filterValue);
                    FILTER_MAP.put(filterKey, skipListSet);

                    // single thread implementation
                    Executor.FIXED_THREAD_POOL.execute(new MongoUtil(url, filterKey));
                    // TODO multi-thread multi-instance implmentation
                } else {
                    skipListSet.add(filterValue);
                }
            }
        }
    }

    public static void remove(final String node) {
        String[] info = node.split(Config.UNDERLINE_3);
        final String filterKey = info[0] + Config.UNDERLINE_3 + info[1];
        final String filterValue = info[2] + point + info[3];
        ConcurrentSkipListSet<String> skipListSet = FILTER_MAP.get(filterKey);
        Map<String, TimestampBean> timestampBeanMap = MIN_TIME_MAP.get(filterKey);
        if (skipListSet == null) {
        } else {
            skipListSet.remove(filterValue);
        }
        if (StringUtil.isNotEmpty(timestampBeanMap)) {
            timestampBeanMap.remove(filterValue);
        }

        if (StringUtil.isNotEmpty(skipListSet)) {
            if (skipListSet.size() == 0) {
                // interrupt the thread and remove it from map when no table in the list
                Thread t = Executor.FIXED_THREAD_POOL_THREADS.get(filterKey);
                if (t != null) {
                    t.interrupt();
                }
                DB_CONFIG_MAP.remove(filterKey);
                FILTER_MAP.remove(filterKey);
                MIN_TIME_MAP.remove(filterKey);
            }
        }
    }

    public static Map<String, SourceJdbcBO> getDBConfigs() {
        return DB_CONFIG_MAP;
    }

    public static Map<String, Map<String, TimestampBean>> getMinTimeMap() {
        return MIN_TIME_MAP;
    }

    public static Map<String, ConcurrentSkipListSet<String>> getTableSubordinate() {
        return FILTER_MAP;
    }

    public static boolean isFilter(String filterKey, String filterValue) {
        ConcurrentSkipListSet filter = FILTER_MAP.get(filterKey);
        if (filter == null) {
            return false;
        } else if (filter.contains(filterValue)) {
            return true;
        }
        return false;
    }

}
