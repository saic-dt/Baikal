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

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.datatech.baikal.common.exp.BizException;
import org.datatech.baikal.web.core.Constants;
import org.datatech.baikal.web.modules.sqlite.service.DBDDLService;
import org.datatech.baikal.web.modules.sqlite.service.SqliteService;
import org.datatech.baikal.web.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 * 创建表结构
 */
@Service
public class SqliteServiceImpl implements SqliteService {

    private static String PRE_URL = "jdbc:sqlite:";
    @Resource
    private DBDDLService dbddlService;

    @Override
    public Connection getMonitorConnection(String tenantName) throws Exception {
        String url = Constants.SQLITE_DBPATH.replace("{tenantName}", tenantName).replace("{?}",
                new SimpleDateFormat("yyyyMMdd").format(new Date()));
        return getConnection(url);
    }

    @Override
    public Connection getMonitorConnection(String tenantName, String yyyyMMdd) throws Exception {
        String url = Constants.SQLITE_DBPATH.replace("{tenantName}", tenantName).replace("{?}", yyyyMMdd);
        return getConnection(url);
    }

    private Connection getConnection(String url) throws Exception {
        File db = new File(url);
        boolean isExists = true;
        if (!db.exists()) {
            isExists = false;
        }
        Connection connection = new SqlitePool().getConnection(PRE_URL + url, Constants.SQLITE_USERNAME,
                Constants.SQLITE_PASSWORD, Constants.SQLITE_CLASSNAME);
        createTable(connection, url, isExists, "ddl/ddl_monitor.sql");
        return connection;
    }

    @Override
    public Connection getEventConnection(String tenantName) throws Exception {
        String url = Constants.SQLITE_EVENT_DBFILE.replace("{tenantName}", tenantName).replace("{?}",
                new SimpleDateFormat("yyyy").format(new Date()));
        File db = new File(url);
        boolean isExists = true;
        if (!db.exists()) {
            isExists = false;
        }
        Connection connection = new SqlitePool().getConnection(PRE_URL + url, Constants.SQLITE_USERNAME,
                Constants.SQLITE_PASSWORD, Constants.SQLITE_CLASSNAME);
        createTable(connection, url, isExists, "ddl/ddl_event.sql");
        return connection;
    }

    @Override
    public Connection getOperationLogConnection(String yyyyMMdd) throws Exception {
        if (StringUtil.isNull(yyyyMMdd)) {
            yyyyMMdd = new SimpleDateFormat("yyyy").format(new Date());
        }
        String url = Constants.SQLITE_OPERATION_LOG_DBFILE.replace("{?}", yyyyMMdd);
        File db = new File(url);
        boolean isExists = true;
        if (!db.exists()) {
            isExists = false;
        }
        Connection connection = new SqlitePool().getConnection(PRE_URL + url, Constants.SQLITE_USERNAME,
                Constants.SQLITE_PASSWORD, Constants.SQLITE_CLASSNAME);
        createTable(connection, url, isExists, "ddl/ddl_operation_log.sql");
        return connection;
    }

    private void createTable(Connection connection, String url, Boolean isExists, String path) throws Exception {
        if (isExists && SqlitePool.urlSet.contains(url)) {
            return;
        }
        List<String> tables = dbddlService.getTablesSqlByFile(path);
        Statement state = null;
        try {
            state = connection.createStatement();
            for (String sql : tables) {
                state.addBatch(sql);
            }
            state.executeBatch();
            SqlitePool.urlSet.add(url);
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            close(null, state, null);
        }
    }

    @Override
    public void close(Connection connection, Statement statement, ResultSet set) {
        if (set != null) {
            try {
                set.close();
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
}
