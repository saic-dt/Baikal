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

package org.datatech.baikal.web.modules.sqlite.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

public class SqlitePool {

    /**
     * 用于存放已经创建完表的URL
     */
    public static Set<String> urlSet = new ConcurrentSkipListSet<>();

    public Connection getConnection(String url, String user, String password, String classString) throws SQLException {
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        // 开启全局缓存
        sqLiteConfig.setSharedCache(true);
        sqLiteConfig.setOpenMode(SQLiteOpenMode.SHAREDCACHE);
        sqLiteConfig.setCacheSize(20000);
        sqLiteConfig.setReadUncommited(true);
        Properties properties = sqLiteConfig.toProperties();
        properties.setProperty("username", user);
        properties.setProperty("password", password);
        // 开启全局缓存结束
        try {
            Class.forName(classString);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(url, sqLiteConfig.toProperties());
        Statement statement = connection.createStatement();
        //todo 这里统一加上开启脏读模式，否则只需要读取Connection 写入不需要加入 满足两个条件 1 开启全局缓存 2 开启脏读模式
        statement.executeUpdate("PRAGMA read_uncommitted = TRUE");
        statement.close();
        return connection;
    }
}
