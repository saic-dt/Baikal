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
 * Synchronization monitoring status of source database table.
 */
public class MonitorTable {
    private String rowKey;
    private String insertRows;
    private String updateRows;
    private String deleteRows;
    private String discardRows;
    private String totalRows;

    /**
     * Get row key.
     *
     * @return Row key.
     */
    public String getRowKey() {
        return rowKey;
    }

    /**
     * Get number of inserted rows.
     *
     * @return Number of inserted rows.
     */
    public String getInsertRows() {
        return insertRows;
    }

    /**
     * Get number of updated rows.
     *
     * @return Number of updated rows.
     */
    public String getUpdateRows() {
        return updateRows;
    }

    /**
     * Get number of deleted rows.
     *
     * @return Number of deleted rows.
     */
    public String getDeleteRows() {
        return deleteRows;
    }

    /**
     * Get number of discarded rows.
     *
     * @return Number of discarded rows.
     */
    public String getDiscardRows() {
        return discardRows;
    }

    /**
     * Get number of total rows.
     *
     * @return Number of total rows.
     */
    public String getTotalRows() {
        return totalRows;
    }
}
