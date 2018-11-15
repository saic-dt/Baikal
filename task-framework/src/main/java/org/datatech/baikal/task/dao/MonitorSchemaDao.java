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

import org.apache.commons.httpclient.NameValuePair;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.util.MonitorUtil;
import org.datatech.baikal.task.util.RestfulUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Data Access Object for monitor schema table.
 */
@Service
public class MonitorSchemaDao {

    @Autowired
    private ZkHandler handler;

    @Autowired
    private RestfulUtil restfulUtil;

    /**
     * Insert a schema statistics record to MonitorSchema table.
     *
     * @param dateTimeStr    dateTimeStr
     * @param instanceName   instanceName
     * @param schemaName     schemaName
     * @param procType       procType
     * @param procName       procName
     * @param procStatus     procStatus
     * @param procLag        procLag
     * @param procCheckpoint procCheckpoint
     * @param procRows       procRows
     */
    public void insertSchemaStats(String dateTimeStr, String instanceName, String schemaName, String procType,
            String procName, String procStatus, String procLag, String procCheckpoint, String procRows) {
        String rowkey = String.join(Config.DELIMITER, instanceName, schemaName, MonitorUtil.getTimestamp(dateTimeStr),
                procName);
        String tenantName = handler.getTenantName();
        NameValuePair[] data = { new NameValuePair("PROC_CHECKPOINT", procCheckpoint),
                new NameValuePair("PROC_LAG", procLag), new NameValuePair("PROC_STATUS", procStatus),
                new NameValuePair("PROC_TYPE", procType), new NameValuePair("PROC_ROWS", procRows),
                new NameValuePair("RowKey", rowkey), new NameValuePair("tenantName", tenantName), };

        String target = restfulUtil.getUrl("monitorSchema/save");
        restfulUtil.callRestService(target, data);

    }
}
