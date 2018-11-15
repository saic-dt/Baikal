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
 * Event log information send from task management module to frontend web application.
 */
public class EventLog {
    private String rowKey;
    private String eventId;
    private String parentEventId;
    private String sourceInstance;
    private String sourceSchema;
    private String sourceTable;
    private String message;

    /**
     * Constructor to create an event log.
     *
     * @param rowKey         Row key of the event.
     * @param eventId        Event Id.
     * @param parentEventId  Parent event Id.
     * @param sourceInstance Related source database instance name of the event.
     * @param sourceSchema   Related source database schema name of the event.
     * @param sourceTable    Related source table name of the event.
     * @param message        Event message.
     */
    public EventLog(String rowKey, String eventId, String parentEventId, String sourceInstance, String sourceSchema,
            String sourceTable, String message) {
        this.rowKey = rowKey;
        this.eventId = eventId;
        this.parentEventId = parentEventId;
        this.sourceInstance = sourceInstance;
        this.sourceSchema = sourceSchema;
        this.sourceTable = sourceTable;
        this.message = message;
    }

    /**
     * Get row key of the event.
     *
     * @return Row key of the event.
     */
    public String getRowKey() {
        return rowKey;
    }

    /**
     * Get event Id.
     *
     * @return Event Id.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Get parent event Id.
     *
     * @return Parent event Id.
     */
    public String getParentEventId() {
        return parentEventId;
    }

    /**
     * Get related source database instance name of the event.
     *
     * @return Related source database instance name of the event.
     */
    public String getSourceInstance() {
        return sourceInstance;
    }

    /**
     * Get related source database schema name of the event.
     *
     * @return Related source database schema name of the event.
     */
    public String getSourceSchema() {
        return sourceSchema;
    }

    /**
     * Get related source table name of the event.
     *
     * @return Related source table name of the event.
     */
    public String getSourceTable() {
        return sourceTable;
    }

    /**
     * Get Event message.
     *
     * @return Event message.
     */
    public String getMessage() {
        return message;
    }
}
