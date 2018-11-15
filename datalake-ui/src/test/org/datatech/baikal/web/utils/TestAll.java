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

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestAll {
    private static String path = "D:\\tool\\project\\DynamicTable\\src\\cn\\test\\entity";
    private static String pkname = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://192.168.1.220:3306/Person";
    private static String[] classNames = new String[] { "ShipStopping", "ArriveShip", "TBLUserType" };
    private static Map<String, String> fkTableNamesAndPk = new HashMap<String, String>();

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Connection conn = null;
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        ResultSet crs = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, "admin", "123");
            String catalog = conn.getCatalog(); // catalog 其实也就是数据库名
            metaData = conn.getMetaData();
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            // 获取表
            rs = metaData.getTables(null, "%", "%", new String[] { "TABLE" });
            while (rs.next()) {
                String tablename = rs.getString("TABLE_NAME");
                String classname = getClassNameByTableName(tablename);
                StringBuffer sb = new StringBuffer();
                StringBuffer sbpackage = new StringBuffer();
                sbpackage.append("package cn.test.entity;\r\n\r\n");
                sbpackage.append("import javax.persistence.Column;\r\n");
                sbpackage.append("import javax.persistence.Entity;\r\n");
                sbpackage.append("import javax.persistence.GeneratedValue;\r\n");
                sbpackage.append("import javax.persistence.Id;\r\n");
                sbpackage.append("import javax.persistence.Table;\r\n\r\n");
                sb.append("\r\n@Entity\r\n");
                sb.append("@Table(name = \"" + tablename + "\")\r\n");
                sb.append("public class " + classname + " implements java.io.Serializable {\r\n");
                // 获取当前表的列
                crs = metaData.getColumns(null, "%", tablename, "%");
                // 获取被引用的表，它的主键就是当前表的外键
                fkTableNamesAndPk.clear();
                ResultSet foreignKeyResultSet = metaData.getImportedKeys(catalog, null, tablename);
                while (foreignKeyResultSet.next()) {
                    String pkTablenName = foreignKeyResultSet.getString("PKTABLE_NAME"); // 外键表
                    String fkColumnName = foreignKeyResultSet.getString("FKCOLUMN_NAME"); // 外键
                    if (!fkTableNamesAndPk.containsKey(fkColumnName))
                        fkTableNamesAndPk.put(fkColumnName, pkTablenName);
                }
                // foreignKeyResultSet.close();
                while (crs.next()) {
                    String columnname = crs.getString("COLUMN_NAME");
                    String columntype = crs.getString("TYPE_NAME");
                    System.out.println("--------------------------" + columntype);
                    if (existFKColumn(columnname)) {
                        String fkclassname = getClassNameByTableName(fkTableNamesAndPk.get(columnname));
                        sbpackage.append("import " + pkname + "." + fkclassname + ";\r\n");
                        sb.append("\t/**    */\r\n");
                        sb.append("\tprivate " + fkclassname + " " + columnname + ";\r\n");
                    } else {
                        sb.append("\t/**    */\r\n");
                        sb.append("\tprivate " + getFieldType(columntype, sbpackage) + " " + columnname + ";\r\n");
                    }
                }
                sb.append("}");
                File file = new File(dirFile, classname + ".java");
                if (file.exists()) {
                    file.delete();
                }
                getTitle(sbpackage, classname);
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(sbpackage.toString().getBytes());
                outputStream.write(sb.toString().getBytes());
                outputStream.close();
                System.out.println(classname + " create success ... ");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch (Exception e2) {
            }
        }
    }

    /**
     * 根据表名获取类名称
     * 
     * @param tablename
     * @return
     */
    private static String getClassNameByTableName(String tablename) {
        String classname = getClassName(tablename);
        for (String name : classNames) {
            if (name.toLowerCase().equals(tablename.toLowerCase())) {
                classname = name;
            }
        }
        return classname;
    }

    private static boolean existFKColumn(String columnname) {
        if (fkTableNamesAndPk != null) {
            if (fkTableNamesAndPk.containsKey(columnname))
                return true;
        }
        return false;
    }

    /**
     * 适合表名为单个单词， 例如：表名是TBLUSER 类名是TBLUser;当表名是USER 类名是User;当表面是USERTYPE(两个单词)
     * 时，类名是Usertype,如果要 UserType，将期望的类名添加到classNames字段中（与数据库表名一致 不区分大小写）。
     * 
     * @param tablename 表名称
     * @return 类的名称
     */
    public static String getClassName(String tablename) {
        String res = tablename.toLowerCase();
        if (tablename.startsWith("TBL")) {
            return tablename.substring(0, 4) + res.substring(4);
        }
        return tablename.substring(0, 1).toUpperCase() + res.substring(1);
    }

    /**
     * 设置字段类型 MySql数据类型
     * 
     * @param columnType
     *            列类型字符串
     * @param sbpackage
     *            封装包信息
     * @return 字符串
     */
    public static String getFieldType(String columnType, StringBuffer sbpackage) {
        /*
         * tinyblob tinyblob byte[] tinytext varchar java.lang.string blob blob byte[]
         * text varchar java.lang.string mediumblob mediumblob byte[] mediumtext varchar
         * java.lang.string longblob longblob byte[] longtext varchar java.lang.string
         * enum('value1','value2',...) char java.lang.string set('value1','value2',...)
         * char java.lang.string
         */
        columnType = columnType.toLowerCase();
        if (columnType.equals("varchar") || columnType.equals("nvarchar") || columnType.equals("char")
        // || columnType.equals("tinytext")
        // || columnType.equals("text")
        // || columnType.equals("mediumtext")
        // || columnType.equals("longtext")
        ) {
            return "String";
        } else if (columnType.equals("tinyblob") || columnType.equals("blob") || columnType.equals("mediumblob")
                || columnType.equals("longblob")) {
            return "byte[]1111";
        } else if (columnType.equals("datetime") || columnType.equals("date") || columnType.equals("timestamp")
                || columnType.equals("time") || columnType.equals("year")) {
            sbpackage.append("import java.util.Date;\r\n");
            return "Date";
        } else if (columnType.equals("bit") || columnType.equals("int") || columnType.equals("tinyint")
                || columnType.equals("smallint")
        // ||columnType.equals("bool")
        // ||columnType.equals("mediumint")
        // ||columnType.equals("bigint")
        ) {
            return "int";
        } else if (columnType.equals("float")) {
            return "Float";
        } else if (columnType.equals("double")) {
            return "Double";
        } else if (columnType.equals("decimal")) {
            // sbpackage.append("import java.math.BigDecimal;\r\n");
            // return "BigDecimal";
        }
        return "ErrorType";
    }

    /**
     * 设置类标题注释
     * 
     * @param sbpackage 包
     * @param className 类名称
     */
    public static void getTitle(StringBuffer sbpackage, String className) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        sbpackage.append("\r\n/**\r\n");
        sbpackage.append("*\r\n");
        sbpackage.append("* 标题: " + className + "<br/>\r\n");
        sbpackage.append("* 说明: <br/>\r\n");
        sbpackage.append("*\r\n");
        sbpackage.append("* 作成信息: DATE: " + format.format(new Date()) + " NAME: author\r\n");
        sbpackage.append("*\r\n");
        sbpackage.append("* 修改信息<br/>\r\n");
        sbpackage.append("* 修改日期 修改者 修改ID 修改内容<br/>\r\n");
        sbpackage.append("*\r\n");
        sbpackage.append("*/\r\n");
    }

}