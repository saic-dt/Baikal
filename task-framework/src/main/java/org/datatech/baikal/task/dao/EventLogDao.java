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
import org.datatech.baikal.task.util.RestfulUtil;
import org.datatech.baikal.task.util.ZkHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Data Access Object for event log table.
 */
@Service
public class EventLogDao {

    @Autowired
    private ZkHandler handler;

    @Autowired
    private RestfulUtil restfulUtil;

    /**
     * insert an event log record to event table.
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @param message      message
     * @throws IOException IOException
     */
    public void insertEvent(String instanceName, String schemaName, String tableName, String message) throws Exception {

        long ts = System.currentTimeMillis();
        String tenantName = handler.getTenantName();
        NameValuePair[] data = { new NameValuePair("EVENT_ID", String.valueOf(ts)),
                new NameValuePair("PARENT_EVENT_ID", String.valueOf(ts)), new NameValuePair("MESSAGE", message),
                new NameValuePair("SOURCE_INSTANCE", instanceName), new NameValuePair("SOURCE_SCHEMA", schemaName),
                new NameValuePair("SOURCE_TABLE", tableName), new NameValuePair("tenantName", tenantName), };
        String target = restfulUtil.getUrl("event/save");
        restfulUtil.callRestService(target, data);
    }
}
