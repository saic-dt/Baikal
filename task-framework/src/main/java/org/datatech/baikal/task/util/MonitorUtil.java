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

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility class providing methods for status monitoring.
 */
public class MonitorUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Get timestamp string from a date format string.
     *
     * @param dateTimeStr dateTimeStr
     * @return timestamp with precision of second
     */
    public static String getTimestamp(String dateTimeStr) {
        long ts;
        try {
            ts = format.parse(dateTimeStr).getTime();
        } catch (ParseException ex) {
            ts = System.currentTimeMillis();
        }
        String tsStr = Long.toString(ts);
        return tsStr.substring(0, tsStr.length() - 3);
    }
}
