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

/**
 * Synchronization monitoring status of source database schema.
 */
public class MonitorSchema {
    private String rowKey;
    private String procType;
    private String procName;
    private String procStatus;
    private int procLag;
    private int procCheckpoint;
    private int procRows;

    /**
     * Get row key.
     *
     * @return Row key.
     */
    public String getRowKey() {
        return rowKey;
    }

    /**
     * Get process type.
     *
     * @return Process type.
     */
    public String getProcType() {
        return procType;
    }

    /**
     * Get process name.
     *
     * @return Process name.
     */
    public String getProcName() {
        return procName;
    }

    /**
     * Get process status.
     *
     * @return Process status.
     */
    public String getProcStatus() {
        return procStatus;
    }

    /**
     * Get process lag time.
     *
     * @return Process lag time.
     */
    public int getProcLag() {
        return procLag;
    }

    /**
     * Get process checkpoint count.
     *
     * @return Process checkpoint count.
     */
    public int getProcCheckpoint() {
        return procCheckpoint;
    }

    /**
     * Get processed row count.
     *
     * @return Processed row count.
     */
    public int getProcRows() {
        return procRows;
    }
}
