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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 */
public class DateUtil {

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate 需要转化的时间字符串
     * @return 转化为Date类型的时间
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     *
     * @return 返回当前服务器时间
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 得到年
     * @return 返回当前年份
     */
    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 得到月
     * @return 返回单钱月份
     */
    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到现在分钟
     *
     * @return 返回特定格式的当前时间字符串
     */
    public static String getTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat
     *            yyyyMMddhhmmss
     * @return 返回用户指定时间格式的服务器时间字符串
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到二个日期间的间隔天数
     * @param sj1 起始时间
     * @param sj2 截止时间
     * @return 返回两个时间时间天数
     */
    public static long getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
        return day;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     * @param nowdate 时间
     * @param delay 前移后移的天数
     * @return 返回指定时间前移或者后移的时间字符串
     */
    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     * @param nowdate 时间
     * @param delay 前移后移的天数
     * @return 返回指定时间前移或者后移的时间
     */
    public static Date getNextDay(Date nowdate, int delay) {
        try {
            long myTime = (nowdate.getTime() / 1000) + delay * 24 * 60 * 60;
            nowdate.setTime(myTime * 1000);
            return nowdate;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否润年
     *
     * @param ddate 时间字符串
     * @return 是否是闰年 true 是 false 不是
     */
    public static boolean isLeapYear(String ddate) {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0) {
            return true;
        } else if ((year % 4) == 0) {
            if ((year % 100) == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate 时间字符串
     * @return 返回星期几的字符串
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = DateUtil.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1 两个时间的相隔天数的起始时间
     * @param date2 两个时间的相隔天数的截止时间
     * @return 返回连个时间的相隔天数
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || "".equals(date1)) {
            return 0;
        }
        if (date2 == null || "".equals(date2)) {
            return 0;
        }
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * @param nowTimeStamp 当前时间戳
     * @param lastUpdateTimeStamp 最近一次更新密码的时间戳
     * @return days 两个时间戳相差天数
     * 通过两个long类型日期判断两个日期相差的天数
     */
    public static long getDifferentDaysByMillisecond(long nowTimeStamp, long lastUpdateTimeStamp) {
        String s = longToDate(nowTimeStamp);
        String s1 = longToDate(lastUpdateTimeStamp);
        long days = DateUtil.getDays(s, s1);
        return days;
    }

    /**
     * long类型时间戳转String类型
     * @param lo long类型时间戳
     * @return String类型时间戳
     */

    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    /**
     * 格式化日期
     *
     * @param dateStr
     *            字符型日期
     * @param format
     *            格式
     * @return 返回日期
     */

    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            java.text.DateFormat df = new SimpleDateFormat(format);
            // 存在问题
            String dt = (dateStr).replaceAll("-", "/");
            if ((!"".equals(dt)) && (dt.length() < format.length())) {
                dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]", "0");
            }
            date = (Date) df.parse(dt);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * 按照给定的格式化字符串格式化日期
     * @param date 时间
     * @param formatStr 时间格式
     * @return 返回时间格式化之后的字符串
     */
    public static String formatDate(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    /**
     * 按照给定的格式化字符串解析日期
     * @param dateStr 时间字符串
     * @param formatStr 时间格式
     * @return 格式化之后的时间
     * @throws ParseException 格式化异常
     */
    public static Date parseDateFormat(String dateStr, String formatStr) throws ParseException {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        date = sdf.parse(dateStr);
        return date;
    }

    /**
     * 从字符串中分析日期
     * @param dateStr 时间字符串
     * @return 格式化之后的时间
     */
    public static Date parseDate(String dateStr) {
        Date date = null;
        try {
            String[] dateArray = dateStr.split("\\D+"); // +防止多个非数字字符在一起时导致解析错误
            int dateLen = dateArray.length;
            int dateStrLen = dateStr.length();
            if (dateLen > 0) {
                if (dateLen == 1 && dateStrLen > 4) {
                    if (dateStrLen == "yyyyMMddHHmmss".length()) {
                        // 如果字符串长度为14位并且不包含其他非数字字符，则按照（yyyyMMddHHmmss）格式解析
                        date = parseDateFormat(dateStr, "yyyyMMddHHmmss");
                    } else if (dateStrLen == "yyyyMMddHHmm".length()) {
                        date = parseDateFormat(dateStr, "yyyyMMddHHmm");
                    } else if (dateStrLen == "yyyyMMddHH".length()) {
                        date = parseDateFormat(dateStr, "yyyyMMddHH");
                    } else if (dateStrLen == "yyyyMMdd".length()) {
                        date = parseDateFormat(dateStr, "yyyyMMdd");
                    } else if (dateStrLen == "yyyyMM".length()) {
                        date = parseDateFormat(dateStr, "yyyyMM");
                    }
                } else {
                    String fDateStr = dateArray[0];
                    for (int i = 1; i < dateLen; i++) {
                        // 左补齐是防止十位数省略的情况
                        fDateStr += leftPad(dateArray[i], "0", 2);
                    }

                    if (dateStr.trim().matches("^\\d{1,2}:\\d{1,2}(:\\d{1,2})?$")) {
                        // 补充年月日3个字段
                        dateLen += 3;
                        fDateStr = formatDate(new Date(), "yyyyMMdd") + fDateStr;
                    }

                    date = parseDate(fDateStr, "yyyyMMddHHmmss".substring(0, (dateLen - 1) * 2 + 4));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * 左对齐
     * @param str 字符串
     * @param pad 补齐字符串
     * @param len 需要的字符串长度
     * @return 返回指定长度的补齐字符串
     */
    public static String leftPad(String str, String pad, int len) {
        String newStr = (str == null ? "" : str);
        while (newStr.length() < len) {
            newStr = pad + newStr;
        }
        if (newStr.length() > len) {
            newStr = newStr.substring(newStr.length() - len);
        }
        return newStr;
    }

    /**
     * 获得该月第一天
     * 
     * @param year 年份
     * @param month 月份
     * @return 返回年份月份的第一天
     */
    public static String getFirstDayOfMonth(int year, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获得该月最后一天
     * 
     * @param year 年份
     * @param month 月份
     * @return 返回年份月份的最后一天
     */
    public static String getLastDayOfMonth(int year, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }
}
