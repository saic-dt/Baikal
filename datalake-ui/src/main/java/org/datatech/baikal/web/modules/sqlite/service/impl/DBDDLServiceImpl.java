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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.derby.iapi.services.property.PropertyUtil;
import org.datatech.baikal.web.common.exp.BizException;
import org.datatech.baikal.web.modules.sqlite.service.DBDDLService;
import org.datatech.baikal.web.modules.sqlite.service.SqliteService;
import org.springframework.stereotype.Service;

/**
 * 创建表结构
 */
@Service
public class DBDDLServiceImpl implements DBDDLService {

    @Resource
    private SqliteService sqliteService;

    @Override
    public void createMonitorDB(String tenantName) throws Exception {
        List<String> tables = getTablesSqlByFile("ddl/ddl_monitor.sql");
        tables.forEach(x -> System.out.println(x));
        Statement state = null;
        Connection connection = null;
        try {
            connection = sqliteService.getMonitorConnection(tenantName);
            state = connection.createStatement();
            for (String sql : tables) {
                state.addBatch(sql);
            }
            state.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(null, state, null);
        }
    }

    @Override
    public void createEventDB(String tenantName) throws Exception {

        // event
        List<String> events = getTablesSqlByFile("ddl/ddl_event.sql");
        Statement stateEvent = null;
        Connection connectionEvent = null;
        try {
            connectionEvent = sqliteService.getEventConnection(tenantName);
            stateEvent = connectionEvent.createStatement();
            for (String sql : events) {
                stateEvent.addBatch(sql);
            }
            stateEvent.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            sqliteService.close(null, stateEvent, null);
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    @Override
    public List<String> getTablesSqlByFile(String fileName) {
        List<String> list = new ArrayList<>();
        InputStream in = null;
        try {
            in = PropertyUtil.class.getClassLoader().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("-") || line.trim().startsWith("#")) {
                    continue;
                }
                if (!"".equals(line)) {
                    sb.append(line + "\n");
                    if (line.endsWith(";")) {
                        if (sb.length() > 0) {
                            list.add(sb.toString());
                            sb.delete(0, sb.length());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            BizException.throwWhenFalse(false, e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return list;
    }
}
