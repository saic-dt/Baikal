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

package org.datatech.baikal.web.common;

import com.google.gson.Gson;

public class BaseTask {

    private TaskType taskType;
    private String instanceName;
    private String schemaName;
    private String tableName;
    private String seqNode;
    private String tablePrefix;
    private String sandBoxName;
    private int retryCount;
    private int waitCycles;

    public BaseTask() {
    }

    public String getSandBoxName() {
        return sandBoxName;
    }

    public void setSandBoxName(String sandBoxName) {
        this.sandBoxName = sandBoxName;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSeqNode() {
        return seqNode;
    }

    public void setSeqNode(String seqNode) {
        this.seqNode = seqNode;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getWaitCycles() {
        return waitCycles;
    }

    public void setWaitCycles(int waitCycles) {
        this.waitCycles = waitCycles;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
