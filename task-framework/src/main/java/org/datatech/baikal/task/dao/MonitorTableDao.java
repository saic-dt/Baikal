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
package org.datatech.baikal.task.dao;

import java.io.IOException;

import org.apache.commons.httpclient.NameValuePair;
import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.task.common.MonitorTable;
import org.datatech.baikal.task.util.MonitorUtil;
import org.datatech.baikal.task.util.RestfulUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Data Access Object for monitor table.
 */
@Service
public class MonitorTableDao {

    @Autowired
    private ZkHandler handler;

    @Autowired
    private RestfulUtil restfulUtil;

    /**
     * Insert a table statistics record to MonitorTable table.
     *
     * @param dateTimeStr  dateTimeStr
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @param insertRows   insertRows
     * @param updateRows   updateRows
     * @param deleteRows   deleteRows
     * @param discardRows  discardRows
     * @param totalRows    totalRows
     */
    public void insertTableStats(String dateTimeStr, String instanceName, String schemaName, String tableName,
            String insertRows, String updateRows, String deleteRows, String discardRows, String totalRows) {
        String rowkey = String.join(Configuration.DELIMITER, instanceName, schemaName,
                MonitorUtil.getTimestamp(dateTimeStr), tableName);
        String tenantName = handler.getTenantName();
        NameValuePair[] data = { new NameValuePair("RowKey", rowkey), new NameValuePair("DELETE_ROWS", deleteRows),
                new NameValuePair("DISCARD_ROWS", discardRows), new NameValuePair("INSERT_ROWS", insertRows),
                new NameValuePair("TOTAL_ROWS", totalRows), new NameValuePair("UPDATE_ROWS", updateRows),
                new NameValuePair("tenantName", tenantName), };

        String target = restfulUtil.getUrl("monitorTable/save");
        restfulUtil.callRestService(target, data);
    }

    /**
     * Retrieve a table statistics record from MonitorTable table.
     *
     * @param dateTimeStr  dateTimeStr
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @return a table statistics record
     * @throws IOException IOException
     */
    public MonitorTable getTableStats(String dateTimeStr, String instanceName, String schemaName, String tableName)
            throws IOException {
        String rowkey = String.join(Configuration.DELIMITER, instanceName, schemaName,
                MonitorUtil.getTimestamp(dateTimeStr), tableName);
        return new MonitorTable();
    }
}
