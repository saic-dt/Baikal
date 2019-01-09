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
 * Utility class for string operation.
 */
public class StringUtil {
    private final static Pattern LOCALPATTERN = Pattern.compile("_");

    private StringUtil() {
    }

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

    public static int getCountByStr(String str, char index) {
        int total = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == index) {
                total++;
            }
        }
        return total;
    }

    public static String[] split(String source, char delim) {
        return split(source, String.valueOf(delim));
    }

    public static String[] split(String source) {
        return split(source, ",");
    }

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

    public static boolean contains(String[] strings, String string) {
        return contains(strings, string, true);
    }

    public static boolean containsIgnoreCase(String[] strings, String string) {
        return contains(strings, string, false);
    }

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

    public static String fillString(char c, int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            ret += c;
        }
        return ret;
    }

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

    public static int getByteLength(String source) {
        int len = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            int highByte = c >>> 8;
            len += highByte == 0 ? 1 : 2;
        }
        return len;
    }

    public static String replace(String line, String oldString, String newString) {
        if (line == null) {
            return null;
        }

        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
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

    public static StringBuilder getString(String s1, String s2, int l) {
        StringBuilder sb = new StringBuilder();
        sb.append(s1).insert(l, s2);
        return sb;
    }

    public static boolean equals(String lin, String line) {
        return lin.equals(line);
    }

    public static int indexOf(String lin, String line) {
        return lin.indexOf(line);
    }

    public static boolean isNull(Object object) {
        if (object == null || "null".equals(object) || "".equals(object) || "\"\"".equals(object)) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Object object) {
        if (object == null || "null".equals(object) || "".equals(object) || "\"\"".equals(object)) {
            return false;
        }
        return true;
    }

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

        for (int i = 0; i < s.length(); i++) {
            String temp = s.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 1;
            } else {
                valueLength += 1;
            }
        }
        return Math.ceil(valueLength);
    }

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
