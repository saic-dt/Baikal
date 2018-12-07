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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.entity.model.DBConnectionModel;
import org.datatech.baikal.web.core.MongoDb;

/**
 * 数据库工具类
 */
public class DataBaseUtil {
    public final static Map<String, String> DB_TYPE_MAP = new ConcurrentHashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put("oracle", Enums.DbType.DB_TYPE_ORACLE.value());
            put("mysql", Enums.DbType.DB_TYPE_MYSQL.value());
            put("mssql", Enums.DbType.DB_TYPE_MSSQL.value());
            put("db2", Enums.DbType.DB_TYPE_DB2.value());
            put("mongo", Enums.DbType.DB_TYPE_MONGO.value());
        }
    };
    final static String LIKE = "%";
    /**
     * DB驱动字符串 MAP key DB_TYPE value 对应的 CLASS_NAME DB_TYPE 读取自
     * org.datatech.baikal.web.common.conf.Enums CLASS_NAME 读取自
     * org.datatech.baikal.web.common.conf.Config 配置类
     */
    public static Map<String, String> DRIVE_CLASS_MAP = new ConcurrentHashMap<String, String>() {
        private static final long serialVersionUID = 2L;
        {
            put(Enums.DbType.DB_TYPE_ORACLE.value(), Config.JDBC_CLASS_NAME_ORACLE);
            put(Enums.DbType.DB_TYPE_MYSQL.value(), Config.JDBC_CLASS_NAME_MYSQL);
            put(Enums.DbType.DB_TYPE_MSSQL.value(), Config.JDBC_CLASS_NAME_SQL_SERVER);
            put(Enums.DbType.DB_TYPE_DB2.value(), Config.JDBC_CLASS_NAME_DB2);
        }
    };
    private static Pattern pattern = Pattern
            .compile("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?");

    /**
     * 取得表中的列名
     * @param conModel 数据库连接对象
     * @return 数据库表的列名称集合
     * @throws Exception 异常
     */
    public static ArrayList<String> getColumnsName(DBConnectionModel conModel) throws Exception {
        final String className = conModel.getCln();
        final String functionName = "getColumnsName";
        final String url = conModel.getJdbcUrl();
        final String schema = conModel.getSourceSchema();
        final String tableName = conModel.getTableName();
        final String key = String.join(Config.DELIMITER, functionName, className, url, schema, tableName);
        ArrayList<String> rtArray = EhcacheUtils.getDbCache(key);
        if (rtArray != null) {
            return rtArray;
        }
        rtArray = new ArrayList<String>(20);
        Connection con = getConnentByClassPool(url, conModel.getUser(), conModel.getPassword(), className);
        final String columns = "COLUMN_NAME";
        DatabaseMetaData dbmd = con.getMetaData();
        ResultSet tableRet = dbmd.getColumns(null, conModel.getSourceSchema(), conModel.getTableName(), "%");
        while (tableRet.next()) {
            rtArray.add(tableRet.getString(columns));
        }
        EhcacheUtils.putDbCache(key, rtArray);
        tableRet.close();
        con.close();
        return rtArray;
    }

    /**
     * 取得所有表名
     *
     * @param conModel 数据库连接对象
     * @return 返回数据库表的名称集合
     * @throws Exception 异常信息
     */
    public static final ArrayList<String> getTableName(SourceJdbcBO conModel) throws Exception {
        final String className = conModel.getCLASS_NAME();
        final String functionName = "getTableName";
        final String url = conModel.getJDBC_URL();
        final String schema = conModel.getSCHEMA_NAME();
        final String key = String.join(Config.DELIMITER, functionName, className, url, schema);
        final String mate = conModel.getTableName();
        ArrayList<String> rtArray = EhcacheUtils.getDbCache(key);
        if (rtArray == null || rtArray.size() == 0) {
            rtArray = new ArrayList<>();
            if (Enums.DbType.DB_TYPE_MONGO.value().equals(conModel.getDB_TYPE())) {
                rtArray = new ArrayList<>(MongoDb.getTableName(conModel));
            } else {
                Connection con = getConnentByClassPool(url, conModel.getUSER(), conModel.getPASSWORD(), className);
                if (Config.JDBC_CLASS_NAME_MYSQL.equals(className)) {
                    rtArray = mySqlGetTableName(con);
                } else {
                    DatabaseMetaData dbmd = con.getMetaData();
                    ResultSet tableRet = dbmd.getTables(null, conModel.getSCHEMA_NAME(), LIKE,
                            new String[] { "TABLE" });
                    while (tableRet.next()) {
                        rtArray.add(tableRet.getString("TABLE_NAME"));
                    }
                    tableRet.close();
                }
                con.close();
            }
            EhcacheUtils.putDbCache(key, rtArray);
        }
        if (mate == null) {
            return rtArray;
        }
        String mates = mate.replaceAll("\\s*", "");
        if ("".equals(mates)) {
            return rtArray;
        }
        final Pattern pattern = Pattern.compile(".*(?i)" + mates + ".*");
        return (ArrayList<String>) rtArray.stream().filter(v -> stringMatch(pattern, v)).collect(Collectors.toList());
    }

    /**
     * MySql 取得表名特殊处理
     *
     * @param con 连接对象
     * @return mysql对应数据库表的名称集合
     * @throws Exception
     */
    private static ArrayList<String> mySqlGetTableName(Connection con) throws Exception {
        ArrayList<String> rtArray = new ArrayList<>();
        final String sql = "show tables;";
        PreparedStatement st = con.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            rtArray.add(rs.getString(1));
        }
        rs.close();
        st.close();
        return rtArray;
    }

    /**
     * 取得 MySql表数量
     *
     * @param con 连接对象
     * @return mysql表的记录数
     * @throws Exception
     */
    private static Integer mySqlGetTableQuantity(Connection con) throws Exception {
        final String sql = "show tables;";
        PreparedStatement st = con.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        Integer siz = 0;
        while (rs.next()) {
            ++siz;
        }
        rs.close();
        st.close();
        return siz;
    }

    /**
     * 取得 schema 下表数量
     *
     * @param con 数据库连接对象
     * @param schema 数据库schema
     * @param className 数据库驱动类
     * @return 数据库对应的表个数
     * @throws Exception 异常信息
     */
    public static Integer getTableQuantity(Connection con, String schema, String className) throws Exception {
        Integer siz = 0;
        if (Config.JDBC_CLASS_NAME_MYSQL.equals(className)) {
            siz = mySqlGetTableQuantity(con);
        } else {
            DatabaseMetaData dbmd = con.getMetaData();
            ResultSet tableRet = dbmd.getTables(null, schema, LIKE, new String[] { "TABLE" });
            while (tableRet.next()) {
                ++siz;
            }
            tableRet.close();
        }
        return siz;
    }

    /**
     * 表名过滤方法
     * @param matchString 匹配的正则表达式中的关键字
     * @param srcString 需要匹配的源数据字符串
     * @return 是否匹配 boolean
     */
    public static Boolean stringMatch(String matchString, String srcString) {
        if (matchString == null || null == srcString) {
            return true;
        } else {
            final String mate = matchString.replaceAll("\\s*", "");
            String pattern = ".*(?i)" + mate + ".*";
            return Pattern.matches(pattern, srcString);
        }
    }

    /**
     * 匹配字符串
     * @param matchPattern 匹配的正则对象
     * @param srcString 需要匹配的字符串对象
     * @return 是否匹配
     */
    public static Boolean stringMatch(Pattern matchPattern, String srcString) {
        boolean isMatch = matchPattern.matcher(srcString).matches();
        return isMatch;
    }

    /**
     * 取得关系库连接 并将连接对象持久化在池中
     *
     * @param url 数据库url
     * @param user 数据库用户名
     * @param password 数据库密码
     * @param classString 数据库驱动类
     * @return 数据库连接对象
     * @throws Exception 异常
     */
    public static Connection getConnentByClassPool(String url, String user, String password, String classString)
            throws Exception {
        Class.forName(classString);
        Connection con = DriverManager.getConnection(url, user, password);
        return con;
    }

    /**
     * 根据jdbc url 取得其中的ip ip 不能是 localhost
     *
     * @param url jdbc中的url
     * @return 返回提取出来的ip
     */
    public static String getDBipByJdbcUrl(String url) {
        Matcher m = pattern.matcher(url);
        String rtString = "";
        if (m.find()) {
            rtString = m.group();
        }
        return rtString;
    }

}