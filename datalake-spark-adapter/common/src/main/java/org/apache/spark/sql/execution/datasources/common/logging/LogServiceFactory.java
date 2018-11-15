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

package org.apache.spark.sql.execution.datasources.common.logging;

/**
 * Log service factory for custom log service.
 */
public final class LogServiceFactory {
    private LogServiceFactory() {
    }

    public static LogService getLogService(final String className) {
        return new DatalakeLogService(className);
    }

    public static LogService getLogService() {
        return new DatalakeLogService();
    }
}
