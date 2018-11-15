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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TestDemo {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // String url =
            // "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf8";
            String url = "jdbc:oracle:thin:@//localhost:1521/XE";
            String user = "datalake";
            String pass = "oracle";
            conn = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
        String sql = "select * from test_multi_pri";
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData data = rs.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                // 获得所有列的数目及实际列数
                int columnCount = data.getColumnCount();
                // 获得指定列的列名
                String columnName = data.getColumnName(i);
                // 获得指定列的列值
                // String columnValue = rs.getString(i);
                // 获得指定列的数据类型
                int columnType = data.getColumnType(i);
                // 获得指定列的数据类型名
                String columnTypeName = data.getColumnTypeName(i);
                // 所在的Catalog名字
                String catalogName = data.getCatalogName(i);
                // 对应数据类型的类
                String columnClassName = data.getColumnClassName(i);
                // 在数据库中类型的最大字符个数
                int columnDisplaySize = data.getColumnDisplaySize(i);
                // 默认的列的标题
                String columnLabel = data.getColumnLabel(i);
                // 获得列的模式
                String schemaName = data.getSchemaName(i);
                // 某列类型的精确度(类型的长度)
                int precision = data.getPrecision(i);
                // 小数点后的位数
                int scale = data.getScale(i);
                // 获取某列对应的表名
                String tableName = data.getTableName(i);
                // 是否自动递增
                boolean isAutoInctement = data.isAutoIncrement(i);
                // 在数据库中是否为货币型
                boolean isCurrency = data.isCurrency(i);
                // 是否为空
                int isNullable = data.isNullable(i);
                // 是否为只读
                boolean isReadOnly = data.isReadOnly(i);
                // 能否出现在where中
                boolean isSearchable = data.isSearchable(i);
                System.out.println(columnCount);
                System.out.println("获得列" + i + "的字段名称:" + columnName);
                // System.out.println("获得列" + i + "的字段值:" + columnValue);
                System.out.println("获得列" + i + "的类型,返回SqlType中的编号:" + columnType);
                System.out.println("获得列" + i + "的数据类型名:" + columnTypeName);
                System.out.println("获得列" + i + "所在的Catalog名字:" + catalogName);
                System.out.println("获得列" + i + "对应数据类型的类:" + columnClassName);
                System.out.println("获得列" + i + "在数据库中类型的最大字符个数:" + columnDisplaySize);
                System.out.println("获得列" + i + "的默认的列的标题:" + columnLabel);
                System.out.println("获得列" + i + "的模式:" + schemaName);
                System.out.println("获得列" + i + "类型的精确度(类型的长度):" + precision);
                System.out.println("获得列" + i + "小数点后的位数:" + scale);
                System.out.println("获得列" + i + "对应的表名:" + tableName);
                System.out.println("获得列" + i + "是否自动递增:" + isAutoInctement);
                System.out.println("获得列" + i + "在数据库中是否为货币型:" + isCurrency);
                System.out.println("获得列" + i + "是否为空:" + isNullable);
                System.out.println("获得列" + i + "是否为只读:" + isReadOnly);
                System.out.println("获得列" + i + "能否出现在where中:" + isSearchable);
            }

            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet tableRet = dbmd.getColumns("", "DATALAKE", "TEST_MULTI_PRI", "%");
            while (tableRet.next()) {
                System.out.println("COLUMN_NAME:" + tableRet.getString("COLUMN_NAME"));
                System.out.println("COLUMN_SIZE:" + tableRet.getString("COLUMN_SIZE"));
                System.out.println("TABLE_CAT:" + tableRet.getString("TABLE_CAT"));
                System.out.println("TABLE_SCHEM:" + tableRet.getString("TABLE_SCHEM"));
                System.out.println("TABLE_NAME:" + tableRet.getString("TABLE_NAME"));
                System.out.println("DATA_TYPE:" + tableRet.getInt("DATA_TYPE"));
                System.out.println("TYPE_NAME:" + tableRet.getString("TYPE_NAME"));
                System.out.println("DECIMAL_DIGITS:" + tableRet.getInt("DECIMAL_DIGITS"));
                System.out.println("NUM_PREC_RADIX:" + tableRet.getInt("NUM_PREC_RADIX"));
                System.out.println("NULLABLE:" + tableRet.getString("NULLABLE"));
                System.out.println("COLUMN_DEF:" + tableRet.getString("COLUMN_DEF"));
                System.out.println("SQL_DATA_TYPE:" + tableRet.getInt("SQL_DATA_TYPE"));
                System.out.println("SQL_DATETIME_SUB:" + tableRet.getString("SQL_DATETIME_SUB"));
                System.out.println("CHAR_OCTET_LENGTH:" + tableRet.getString("CHAR_OCTET_LENGTH"));
                System.out.println("ORDINAL_POSITION:" + tableRet.getString("ORDINAL_POSITION"));
                System.out.println("IS_NULLABLE:" + tableRet.getString("IS_NULLABLE"));
                System.out.println("SCOPE_CATALOG:" + tableRet.getString("SCOPE_CATALOG"));
                System.out.println("SCOPE_SCHEMA:" + tableRet.getString("SCOPE_SCHEMA"));
                System.out.println("SCOPE_TABLE:" + tableRet.getString("SCOPE_TABLE"));
                System.out.println("SOURCE_DATA_TYPE:" + tableRet.getString("SOURCE_DATA_TYPE"));
                System.out.println("IS_GENERATEDCOLUMN:" + tableRet.getString("IS_GENERATEDCOLUMN"));
                System.out.println("");
            }

        } catch (SQLException e) {
            System.out.println("数据库连接失败");
        }
    }
}