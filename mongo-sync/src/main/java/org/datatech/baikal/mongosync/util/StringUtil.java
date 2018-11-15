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

import java.util.Random;

/**
 * Utility class providing methods for string operations.
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * Generate random string with specified length.
     * 
     * @param length length
     * @return random string
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * Return true if object is null or empty string.
     * @param object object
     * @return boolean true if object is null or empty string, false otherwise
     */
    public static boolean isNull(Object object) {
        if (object == null || "null".equals(object) || "".equals(object) || "\"\"".equals(object)) {
            return true;
        }
        return false;
    }

    /**
     * Return false if object is null or empty string.
     * @param object object
     * @return boolean false if object is null or empty string, true otherwise
     */
    public static boolean isNotEmpty(Object object) {
        return !isNull(object);
    }

    /**
     * Fill string with specified string at the front.
     * @param string string
     * @param x string for fill at the front
     * @param length target length
     * @return Filled string
     */
    public static String fillXInFrontOfString(String string, String x, int length) {
        if (string == null || string.length() == length) {
            return string;
        }
        for (int i = string.length(); i < length; i++) {
            string = x + string;
        }
        return string;
    }
}
