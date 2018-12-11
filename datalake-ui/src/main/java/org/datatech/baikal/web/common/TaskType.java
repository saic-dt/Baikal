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

public enum TaskType {
    // taskType类型
    MAIN_TASK, SECONDARY_TASK, FULLDUMP_TASK, DELETE_TABLE_TASK, REFRESH_TABLE_TASK, DEPLOY_TASK, ADD_SCHEMA_TASK, DELETE_SANDBOX_TABLE_TASK, ADD_SANDBOX_TABLE_TASK, REFRESH_SANDBOX_TABLE_TASK;
}
