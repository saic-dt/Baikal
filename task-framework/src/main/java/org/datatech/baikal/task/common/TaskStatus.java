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
 * Task execution status.
 */
public class TaskStatus {
    private BaseTask failedTask;
    private int totalTasks;
    private int totalTasksOnHost;

    public BaseTask getFailedTask() {
        return failedTask;
    }

    public void setFailedTask(BaseTask failedTask) {
        this.failedTask = failedTask;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getTotalTasksOnHost() {
        return totalTasksOnHost;
    }

    public void setTotalTasksOnHost(int totalTasksOnHost) {
        this.totalTasksOnHost = totalTasksOnHost;
    }
}
