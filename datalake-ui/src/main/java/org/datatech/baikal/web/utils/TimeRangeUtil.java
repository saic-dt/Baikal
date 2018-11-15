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

package org.datatech.baikal.web.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeRangeUtil {

    public static Map<String, Long> rangeMillis(int timeRange) throws ParseException {
        Map<String, Long> map = new HashMap<String, Long>();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat minute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 当前分钟数
        Date currentMinute = minute.parse(minute.format(date));
        Calendar c = Calendar.getInstance();

        c.setTime(currentMinute);
        c.add(Calendar.MINUTE, 1 - timeRange);
        c.add(Calendar.SECOND, 0);
        map.put("startTime", c.getTimeInMillis() / 1000);

        c.setTime(currentMinute);
        c.add(Calendar.MINUTE, 0);
        c.add(Calendar.SECOND, 59);
        map.put("endTime", c.getTimeInMillis() / 1000);

        return map;
    }

}
