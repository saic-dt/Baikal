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
 * <p>ClassName: StringUtil</p>
 * <p>Description: 字符串操作</p>
 */
package org.datatech.baikal.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * <p>
 * ClassName: StringUtil
 * </p>
 * <p>
 * Description: 字符串操作
 * </p>
 */
public class StringUtil {
    private final static Pattern LOCALPATTERN = Pattern.compile("_");

    /**
     * 私有构造方法，防止类的实例化，因为工具类不需要实例化。
     */
    private StringUtil() {
    }

    /**
     * 此方法将给出的字符串source使用delim划分为单词数组。
     *
     * @param source
     *            需要进行划分的原字符串
     * @param delim
     *            单词的分隔字符串
     * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组， 如果delim为null则使用逗号作为分隔字符串。
     * @since 0.1
     */
    public static String[] split(String source, String delim) {
        String[] wordLists;
        if (source == null) {
            wordLists = new String[1];
            wordLists[0] = source;
            return wordLists;
        }
        if (delim == null) {
            delim = ",";
        }
        StringTokenizer st = new StringTokenizer(source, delim);
        int total = st.countTokens();
        wordLists = new String[total];
        for (int i = 0; i < total; i++) {
            wordLists[i] = st.nextToken();
        }
        return wordLists;
    }

    /**
     * 随机数
     * 
     * @param length 随机字符串的长度
     * @return 随机后的字符串
     */
    public static String getRandomString(int length) { // length表示生成字符串的长度
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
     * 字符在字符串中出现的次数
     *
     * @param str
     *            字符串
     * @param index
     *            字符
     * @return 字符串出现的次数
     */
    public static int getCountByStr(String str, char index) {
        int total = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == index) {
                total++;
            }
        }
        return total;
    }

    /**
     * 此方法将给出的字符串source使用delim划分为单词数组。
     *
     * @param source
     *            需要进行划分的原字符串
     * @param delim
     *            单词的分隔字符
     * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组。
     * @since 0.2
     */
    public static String[] split(String source, char delim) {
        return split(source, String.valueOf(delim));
    }

    /**
     * 此方法将给出的字符串source使用逗号划分为单词数组。
     *
     * @param source
     *            需要进行划分的原字符串
     * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组。
     */
    public static String[] split(String source) {
        return split(source, ",");
    }

    /**
     * 循环打印字符串数组。 字符串数组的各元素间以指定字符分隔，如果字符串中已经包含指定字符则在字符串的两端加上双引号。
     *
     * @param strings
     *            字符串数组
     * @param delim
     *            分隔符
     * @param out
     *            打印到的输出流
     */
    public static void printStrings(String[] strings, String delim, OutputStream out) {
        try {
            if (strings != null) {
                int length = strings.length - 1;
                for (int i = 0; i < length; i++) {
                    if (strings[i] != null) {
                        if (strings[i].indexOf(delim) > -1) {
                            out.write(("\"" + strings[i] + "\"" + delim).getBytes());
                        } else {
                            out.write((strings[i] + delim).getBytes());
                        }
                    } else {
                        out.write("null".getBytes());
                    }
                }
                if (strings[length] != null) {
                    if (strings[length].indexOf(delim) > -1) {
                        out.write(("\"" + strings[length] + "\"").getBytes());
                    } else {
                        out.write(strings[length].getBytes());
                    }
                } else {
                    out.write("null".getBytes());
                }
            } else {
                out.write("null".getBytes());
            }
            // out.write(Constants.LINE_SEPARATOR.getBytes());
        } catch (IOException e) {

        }
    }

    /**
     * 将字符串中的变量使用values数组中的内容进行替换。 替换的过程是不进行嵌套的，即如果替换的内容中包含变量表达式时不会替换。
     *
     * @param prefix
     *            变量前缀字符串
     * @param source
     *            带参数的原字符串
     * @param values
     *            替换用的字符串数组
     * @return 替换后的字符串。 如果前缀为null则使用“%”作为前缀；
     *         如果source或者values为null或者values的长度为0则返回source；
     *         如果values的长度大于参数的个数，多余的值将被忽略； 如果values的长度小于参数的个数，则后面的所有参数都使用最后一个值进行替换。
     * @since 0.2
     */
    public static String getReplaceString(String prefix, String source, String[] values) {
        String result = source;
        if (source == null || values == null || values.length < 1) {
            return source;
        }
        if (prefix == null) {
            prefix = "%";
        }

        for (int i = 0; i < values.length; i++) {
            String argument = prefix + Integer.toString(i + 1);
            int index = result.indexOf(argument);
            if (index != -1) {
                String temp = result.substring(0, index);
                if (i < values.length) {
                    temp += values[i];
                } else {
                    temp += values[values.length - 1];
                }
                temp += result.substring(index + 2);
                result = temp;
            }
        }
        return result;
    }

    /**
     * 字符串数组中是否包含指定的字符串。
     *
     * @param strings
     *            字符串数组
     * @param string
     *            字符串
     * @param caseSensitive
     *            是否大小写敏感
     * @return 包含时返回true，否则返回false
     * @since 0.4
     */
    public static boolean contains(String[] strings, String string, boolean caseSensitive) {
        for (int i = 0; i < strings.length; i++) {
            if (caseSensitive == true) {
                if (strings[i].equals(string)) {
                    return true;
                }
            } else {
                if (strings[i].equalsIgnoreCase(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 字符串数组中是否包含指定的字符串。大小写敏感。
     *
     * @param strings
     *            字符串数组
     * @param string
     *            字符串
     * @return 包含时返回true，否则返回false
     * @since 0.4
     */
    public static boolean contains(String[] strings, String string) {
        return contains(strings, string, true);
    }

    /**
     * 不区分大小写判定字符串数组中是否包含指定的字符串。
     *
     * @param strings
     *            字符串数组
     * @param string
     *            字符串
     * @return 包含时返回true，否则返回false
     * @since 0.4
     */
    public static boolean containsIgnoreCase(String[] strings, String string) {
        return contains(strings, string, false);
    }

    /**
     * 将字符串数组使用指定的分隔符合并成一个字符串。
     *
     * @param array
     *            字符串数组
     * @param delim
     *            分隔符，为null的时候使用""作为分隔符（即没有分隔符）
     * @return 合并后的字符串
     * @since 0.4
     */
    public static String combineStringArray(String[] array, String delim) {
        int length = array.length - 1;
        if (delim == null) {
            delim = "";
        }
        StringBuffer result = new StringBuffer(length * 8);
        for (int i = 0; i < length; i++) {
            result.append(array[i]);
            result.append(delim);
        }
        result.append(array[length]);
        return result.toString();
    }

    /**
     * 以指定的字符和长度生成一个该字符的指定长度的字符串。
     *
     * @param c
     *            指定的字符
     * @param length
     *            指定的长度
     * @return 最终生成的字符串
     * @since 0.6
     */
    public static String fillString(char c, int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            ret += c;
        }
        return ret;
    }

    /**
     * 去除左边多余的空格。
     *
     * @param value
     *            待去左边空格的字符串
     * @return 去掉左边空格后的字符串
     * @since 0.6
     */
    public static String trimLeft(String value) {
        String result = value;
        if (result == null) {
            return result;
        }
        char ch[] = result.toCharArray();
        int index = -1;
        for (int i = 0; i < ch.length; i++) {
            if (Character.isWhitespace(ch[i])) {
                index = i;
            } else {
                break;
            }
        }
        if (index != -1) {
            result = result.substring(index + 1);
        }
        return result;
    }

    /**
     * 去除右边多余的空格。
     *
     * @param value
     *            待去右边空格的字符串
     * @return 去掉右边空格后的字符串
     * @since 0.6
     */
    public static String trimRight(String value) {
        String result = value;
        if (result == null) {
            return result;
        }
        char ch[] = result.toCharArray();
        int endIndex = -1;
        for (int i = ch.length - 1; i > -1; i--) {
            if (Character.isWhitespace(ch[i])) {
                endIndex = i;
            } else {
                break;
            }
        }
        if (endIndex != -1) {
            result = result.substring(0, endIndex);
        }
        return result;
    }

    /**
     * 根据转义列表对字符串进行转义。
     *
     * @param source
     *            待转义的字符串
     * @param escapeCharMap
     *            转义列表
     * @return 转义后的字符串
     * @since 0.6
     */
    public static String escapeCharacter(String source, HashMap escapeCharMap) {
        if (source == null || source.length() == 0) {
            return source;
        }
        if (escapeCharMap.size() == 0) {
            return source;
        }
        StringBuffer sb = new StringBuffer();
        StringCharacterIterator sci = new StringCharacterIterator(source);
        for (char c = sci.first(); c != StringCharacterIterator.DONE; c = sci.next()) {
            String character = String.valueOf(c);
            if (escapeCharMap.containsKey(character)) {
                character = (String) escapeCharMap.get(character);
            }
            sb.append(character);
        }
        return sb.toString();
    }

    /**
     * 得到字符串的字节长度
     *
     * @param source
     *            字符串
     * @return 字符串的字节长度
     * @since 0.6
     */
    public static int getByteLength(String source) {
        int len = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            int highByte = c >>> 8;
            len += highByte == 0 ? 1 : 2;
        }
        return len;
    }

    /**
     * 判断字符串是否为（空或只含空格）
     *
     * @param str
     * @return boolean
     */
    /*public static boolean isNullOrEmptyString(Object str)
    
    {
    
        if (str == null) {
            return true;
        }
    
        if (str.toString().trim().length() == 0) {
            return true;
        }
        return false;
    
    }*/

    /**
     * 替换字符串
     *
     * @param line 需要替换的字符串
     * @param oldString 替换老的字符串
     * @param newString 替换新的字符串
     * @return 替换之后的字符串
     */
    public static String replace(String line, String oldString, String newString) {

        if (line == null) {

            return null;

        }

        int i = 0;

        if ((i = line.indexOf(oldString, i)) >= 0) {

            char[] line2 = line.toCharArray(); // 字符串放入数组

            char[] newString2 = newString.toCharArray(); // 要替换的字符串

            int oLength = oldString.length(); // 被替换的字符串的长度

            StringBuffer buf = new StringBuffer(line2.length);

            buf.append(line2, 0, i).append(newString2);

            i += oLength;

            int j = i;

            while ((i = line.indexOf(oldString, i)) > 0) {

                buf.append(line2, j, i - j).append(newString2);

                i += oLength;

                j = i;

            }

            buf.append(line2, j, line2.length - j);

            return buf.toString();

        }

        return line;

    }

    /**
     * 在下标 l 前插入字符串
     *
     * @param s1 源字符串
     * @param s2 需要插入的字符串
     * @param l 插入的位置
     * @return StringBuilder
     */
    public static StringBuilder getString(String s1, String s2, int l) {
        StringBuilder sb = new StringBuilder();
        sb.append(s1).insert(l, s2);
        return sb;
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param lin 源字符串
     * @param line 目标字符串
     * @return boolean true:相等 false:不相等
     */
    public static boolean equals(String lin, String line) {
        return lin.equals(line);
    }

    /**
     * 判断字符串是否包含，包含则返回下标，否则返回 -1
     *
     * @param lin 源字符串
     * @param line 需要查询的字符串
     * @return int 返回查询字符串在源字符串中的位置
     */
    public static int indexOf(String lin, String line) {
        return lin.indexOf(line);
    }

    /**
     * 判断某对象是否为空
     *
     * @param object 对象
     * @return boolean true 为空 false 不为空
     */
    public static boolean isNull(Object object) {
        if (object == null || "null".equals(object) || "".equals(object) || "\"\"".equals(object)) {
            return true;
        }
        return false;
    }

    /**
     * 判断某对象是否为空
     *
     * @param object 对象
     * @return boolean true 不为空 false 为空
     */
    public static boolean isNotEmpty(Object object) {
        if (object == null || "null".equals(object) || "".equals(object) || "\"\"".equals(object)) {
            return false;
        }
        return true;
    }

    /**
     * 全角转半角
     *
     * @param input 源字符串
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 根据补齐指定的字符串
     * @param string 字符串
     * @param x 需要补齐的字符串
     * @param length 补齐的长度
     * @return 补齐的字符串
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

    public static double getLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 1;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }

    // 求两个字符串数组的并集，利用set的元素唯一性
    public static String[] union(String[] arr1, String[] arr2) {
        Set<String> set = new HashSet<String>();
        for (String str : arr1) {
            set.add(str);
        }
        for (String str : arr2) {
            set.add(str);
        }
        String[] result = {};
        return set.toArray(result);
    }

    // 求两个数组的交集
    public static String[] intersect(String[] arr1, String[] arr2) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        List<String> list = new ArrayList<String>();
        for (String str : arr1) {
            if (!map.containsKey(str)) {
                map.put(str, Boolean.FALSE);
            }
        }
        for (String str : arr2) {
            if (map.containsKey(str)) {
                map.put(str, Boolean.TRUE);
            }
        }

        for (Iterator<Entry<String, Boolean>> it = map.entrySet().iterator(); it.hasNext();) {
            Entry<String, Boolean> e = (Entry<String, Boolean>) it.next();
            if (e.getValue().equals(Boolean.TRUE)) {
                list.add(e.getKey());
            }
        }
        return list.toArray(new String[] {});
    }

    /**
     * 获取整型集合
     *
     * @param id
     *            字符串 批量操作 请用逗号分隔 如:1,2,3
     * @return 整型集合
     */
    public static List<Integer> getIntegers(String id) {
        String[] ids = id.split(",");
        List<Integer> iList = new ArrayList<Integer>();
        for (String idTemp : ids) {
            if (!StringUtil.isNull(idTemp)) {
                iList.add(Integer.valueOf(idTemp));
            }
        }
        return iList;
    }

    /**
     * 获取整型集合
     *
     * @param id
     *            字符串 批量操作 请用逗号分隔 如:1,2,3
     * @return 字符串集合
     */
    public static List<String> getStrings(String id) {
        String[] ids = id.split(",");
        List<String> iList = new ArrayList<String>();
        for (String idTemp : ids) {
            if (!StringUtil.isNull(idTemp)) {
                iList.add(idTemp);
            }
        }
        return iList;
    }

    /**
     * 获取整型集合
     *
     * @param id
     *            字符串 批量操作 请用逗号分隔 如:1,2,3
     * @return Long集合
     */
    public static List<Long> getLongs(String id) {
        String[] ids = id.split(",");
        List<Long> iList = new ArrayList<Long>();
        for (String idTemp : ids) {
            if (!StringUtil.isNull(idTemp)) {
                iList.add(Long.valueOf(idTemp));
            }
        }
        return iList;
    }
}
