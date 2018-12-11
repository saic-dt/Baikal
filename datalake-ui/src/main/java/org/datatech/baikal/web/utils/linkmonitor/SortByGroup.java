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

package org.datatech.baikal.web.utils.linkmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.web.entity.MonitorSchema;
import org.datatech.baikal.web.entity.model.LinkMonitorStateModel;

public class SortByGroup {

    private final static String E_GROUP = "E";
    private final static String D_GROUP = "D";
    private final static String R_GROUP = "R";
    private final static String STOPPED = "STOPPED";
    private final static int ZERO = 0;

    public static LinkMonitorStateModel getPrcoStatus(List<MonitorSchema> dataList) {
        Map<Long, MonitorSchema> rMap = new TreeMap<Long, MonitorSchema>();
        Map<Long, MonitorSchema> dMap = new TreeMap<Long, MonitorSchema>();
        Map<Long, MonitorSchema> eMap = new TreeMap<Long, MonitorSchema>();
        if (dataList == null || dataList.size() == 0) {
            return new LinkMonitorStateModel(E_GROUP, STOPPED, ZERO, D_GROUP, STOPPED, ZERO, R_GROUP, STOPPED, ZERO);
        }
        for (MonitorSchema bean : dataList) {
            putMap(bean, eMap, dMap, rMap);
        }
        return getMaxValue(eMap, dMap, rMap);
    }

    private static void putMap(MonitorSchema bean, Map<Long, MonitorSchema> eMap, Map<Long, MonitorSchema> dMap,
            Map<Long, MonitorSchema> rMap) {
        final String[] keys = bean.getRow_key().split(Configuration.DELIMITER);
        if (keys == null || keys.length < 4) {
            return;
        }
        final String group = keys[3].substring(0, 1);
        final Long timeStamp = Long.valueOf(keys[2]);
        bean.setProc_name(keys[3]);
        if (E_GROUP.equals(group)) {
            eMap.put(timeStamp, bean);
        } else if (D_GROUP.equals(group)) {
            dMap.put(timeStamp, bean);
        } else if (R_GROUP.equals(group)) {
            rMap.put(timeStamp, bean);
        }
    }

    private static LinkMonitorStateModel getMaxValue(Map<Long, MonitorSchema> eMap, Map<Long, MonitorSchema> dMap,
            Map<Long, MonitorSchema> rMap) {
        MonitorSchema e = getMaxValue(eMap, E_GROUP);
        MonitorSchema d = getMaxValue(dMap, D_GROUP);
        MonitorSchema r = getMaxValue(rMap, R_GROUP);
        return new LinkMonitorStateModel(e, d, r);
    }

    private static MonitorSchema getMaxValue(Map<Long, MonitorSchema> map, final String group) {
        int size = map.size();
        if (size == 0) {
            return new MonitorSchema(group, STOPPED, ZERO);
        } else {
            return new ArrayList<MonitorSchema>(map.values()).get(size - 1);
        }
    }
}
