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
package org.datatech.baikal.task.processor;

import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.EventType;
import org.datatech.baikal.task.util.ZkHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Task to refresh the schema of specific table schema in sandbox.
 */
@Service
public class RefreshSandBoxTableProcessor extends BaseTaskProcessor {
    @Autowired
    private ZkHandler zkHandler;

    /**
     * Reload source table schema and recreate table on sandbox.
     *
     * @param task task
     * @throws Exception Exception
     */
    @Override
    public void execute(BaseTask task) throws Exception {
        String tenantName = zkHandler.getTenantName();
        String instanceName = task.getInstanceName();
        String schemaName = task.getSchemaName();
        String tableName = task.getTableName();
        String tableNewName = tenantName + Config.FIELD_SEPARATOR + instanceName + Config.FIELD_SEPARATOR + schemaName
                + Config.FIELD_SEPARATOR + tableName;
        String sandBoxName = task.getSandBoxName();
        JSONObject json = new JSONObject();
        json.put("tenantName", tenantName);
        json.put("instanceName", instanceName);
        json.put("schemaName", schemaName);
        json.put("tableNewName", tableNewName);
        json.put("sandBoxName", sandBoxName);
        json.put("taskType", EventType.REFRESH_SANDBOX_TABLE_TASK);
        String notifyMessage = JSON.toJSONString(json);
        zkHandler.queueMessage(Config.NOTIFY_PATH, notifyMessage.getBytes());
    }
}
