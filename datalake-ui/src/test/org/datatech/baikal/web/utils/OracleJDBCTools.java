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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.junit.Test;

/**
 * JDBC 的工具类
 * 
 * 其中包含: 获取数据库连接, 关闭数据库资源等方法.
 */
public class OracleJDBCTools {

    public static Connection oracleConn = null;

    public static void releaseDB(ResultSet resultSet, Statement statement, Connection connection) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/XE", "datalake",
                "oracle");
        PreparedStatement preparedStatement = null;

        for (int i = 30002; i > 30000; i--) {
            try {
                String tableName = UUID.randomUUID().toString().toUpperCase().replaceAll("\\d+", "").replace("-", "")
                        .substring(0, 5);
                String sql = "CREATE TABLE " + tableName + i + "(\n" + "id NUMBER NOT NULL,\n"
                        + "name VARCHAR2(20BYTE),\n" + "stat NUMBER" + ") tablespace  datalake";
                // System.out.println(sql);
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
                System.out.println(" tableName :" + tableName + i);
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
