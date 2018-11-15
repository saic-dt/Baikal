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
package org.datatech.baikal.task.common;

import com.google.gson.Gson;

/**
 * Task information that pass between frontend web application and task management module.
 */
public class BaseTask {

    private TaskType taskType;
    private String tenantName;
    private String instanceName;
    private String schemaName;
    private String tableName;
    private String seqNode;
    private String tablePrefix;
    private int retryCount;
    private int waitCycles;
    private SourceDb sourceDb;
    private String sandBoxName;

    public BaseTask() {
    }

    public BaseTask(String instanceName, String schemaName, String tableName, String sandBoxName) {
        this.instanceName = instanceName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.sandBoxName = sandBoxName;
    }

    /**
     * Get task type.
     *
     * @return Task type.
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * Set task type.
     *
     * @param taskType Task type.
     */
    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    /**
     * Get instance name.
     *
     * @return Instance name.
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Set instance name.
     *
     * @param instanceName instance name.
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * Get schema name.
     *
     * @return Schema name.
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Set schema name.
     *
     * @param schemaName Schema name.
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Get table name.
     *
     * @return Table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Set table name.
     *
     * @param tableName Table name.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get Zookeeper sequence node name.
     *
     * @return Zookeeper sequence node name.
     */
    public String getSeqNode() {
        return seqNode;
    }

    /**
     * Set Zookeeper sequence node name.
     *
     * @param seqNode Zookeeper sequence node name.
     */
    public void setSeqNode(String seqNode) {
        this.seqNode = seqNode;
    }

    /**
     * Get table prefix string.
     *
     * @return Table prefix string.
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * Set table prefix string.
     *
     * @param tablePrefix Table prefix string.
     */
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    /**
     * Get task retry count.
     *
     * @return Task retry count.
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Set task retry count.
     *
     * @param retryCount Task retry count.
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * Get task wait cycle count.
     *
     * @return Task wait cycle count.
     */
    public int getWaitCycles() {
        return waitCycles;
    }

    /**
     * Set task wait cycle count.
     *
     * @param waitCycles Task wait cycle count.
     */
    public void setWaitCycles(int waitCycles) {
        this.waitCycles = waitCycles;
    }

    /**
     * Get source database information.
     *
     * @return Source database information.
     */
    public SourceDb getSourceDb() {
        return sourceDb;
    }

    /**
     * Set source database information.
     *
     * @param sourceDb Source database information.
     */
    public void setSourceDb(SourceDb sourceDb) {
        this.sourceDb = sourceDb;
    }

    /**
     * Get tenant name.
     *
     * @return Tenant name
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * Set tenant name.
     *
     * @param tenantName Tenant name.
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /**
     * Get sandbox name.
     *
     * @return sandBoxName
     */
    public String getSandBoxName() {
        return sandBoxName;
    }

    /**
     * Set sandbox name.
     *
     * @param sandBoxName sandbox name.
     */
    public void setSandBoxName(String sandBoxName) {
        this.sandBoxName = sandBoxName;
    }

    /**
     * Get json string of task object.
     *
     * @return Json string of task object.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
