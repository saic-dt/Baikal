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

package org.datatech.baikal.web.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class Config {
    //metastore node path
    public static final String ZK_NODE_METASTORE = "/metastore";
    public static final String ZK_NODE_USER = "/users";
    public static final String ZK_NODE_TENANT = "/tenant";
    public static final int REMINDER_TIME = 7;
    public static final int FAILURE_NUMBER = 5;
    public static final int PASSWORD_GROUP_SIZE = 6;
    public static final String LINE_3 = "---";
    //task prefix
    public static final String TASK_PREFIX = "/task_";
    public static String BACKSLASH = "/"; // char 10 used for delimiter
    //源数据库为mongo数据库时生成的mongo节点
    public static String PATH_MONGO_SCHEMA = "mongo-schema";
    //zk main task队列路径
    public static String PATH_QUEUE_MAIN_TASK = "/queue/main_task";

    /**
     * 根据给定的步长输出时刻集合
     *
     * @param step 步长
     * @return map集合
     */
    public static Map<String, Long> stepTimeMap(int step) {
        if (0 >= step || step > 24 * 60) {
            return null;
        }

        int count = 24 * 60 / step;
        if (24 * 60 % step > 0) {
            count = count + 1;
        }

        Map<String, Long> treeMap = new TreeMap<String, Long>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < count; i++) {
            treeMap.put(sdf.format(calendar.getTime()), 0L);
            calendar.add(Calendar.MINUTE, step);
        }
        return treeMap;
    }
}
