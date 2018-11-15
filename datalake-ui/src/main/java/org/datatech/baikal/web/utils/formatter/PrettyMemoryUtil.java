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

/**
 *
 */
package org.datatech.baikal.web.utils.formatter;

/**
 * <p>
 * Date: 13-6-2 上午6:48
 * <p>
 * Version: 1.0
 */
public class PrettyMemoryUtil {

    private static final int UNIT = 1024;

    /**
     * @param byteSize
     *            字节
     * @return 字节大小
     */
    public static String prettyByteSize(long byteSize) {

        double size = 1.0 * byteSize;

        String type = "B";
        if ((int) Math.floor(size / UNIT) <= 0) { // 不足1KB
            type = "B";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { // 不足1MB
            type = "KB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { // 不足1GB
            type = "MB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { // 不足1TB
            type = "GB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { // 不足1PB
            type = "TB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) {
            type = "PB";
            return format(size, type);
        }
        return ">PB";
    }

    private static String format(double size, String type) {
        int precision = 0;

        if (size * 1000 % 10 > 0) {
            precision = 3;
        } else if (size * 100 % 10 > 0) {
            precision = 2;
        } else if (size * 10 % 10 > 0) {
            precision = 1;
        } else {
            precision = 0;
        }

        String formatStr = "%." + precision + "f";

        if ("KB".equals(type)) {
            return String.format(formatStr, (size)) + "KB";
        } else if ("MB".equals(type)) {
            return String.format(formatStr, (size)) + "MB";
        } else if ("GB".equals(type)) {
            return String.format(formatStr, (size)) + "GB";
        } else if ("TB".equals(type)) {
            return String.format(formatStr, (size)) + "TB";
        } else if ("PB".equals(type)) {
            return String.format(formatStr, (size)) + "PB";
        }
        return String.format(formatStr, (size)) + "B";
    }

    public static void main(String[] args) {
        System.out.println(PrettyMemoryUtil.prettyByteSize(1023));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT * UNIT));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT * 1023));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * 1023 * 1023 * 1023));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT * UNIT * UNIT));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT * UNIT * UNIT * UNIT));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT * UNIT * UNIT * UNIT * UNIT));
        System.out.println(PrettyMemoryUtil.prettyByteSize(1L * UNIT * UNIT * UNIT * UNIT * UNIT * UNIT));
    }

}
