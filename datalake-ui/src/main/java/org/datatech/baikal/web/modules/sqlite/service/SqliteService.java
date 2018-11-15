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

package org.datatech.baikal.web.modules.sqlite.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * <p>
 * sqlite数据库的接口类
 * <p>
 */
public interface SqliteService {

    Connection getMonitorConnection(String tenantName) throws Exception;

    Connection getMonitorConnection(String tenantName, String yyyyMMdd) throws Exception;

    Connection getEventConnection(String tenantName) throws Exception;

    Connection getOperationLogConnection(String yyyyMMdd) throws Exception;

    void close(Connection connection, Statement statement, ResultSet set) throws Exception;
}
