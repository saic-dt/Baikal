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

import org.datatech.baikal.task.processor.AddSchemaTaskProcessor;
import org.datatech.baikal.task.processor.BaseTaskProcessor;
import org.datatech.baikal.task.processor.DeleteTableTaskProcessor;
import org.datatech.baikal.task.processor.DeployTaskProcessor;
import org.datatech.baikal.task.processor.FullDumpTaskProcessor;
import org.datatech.baikal.task.processor.MainTaskProcessor;
import org.datatech.baikal.task.processor.RefreshTableTaskProcessor;
import org.datatech.baikal.task.processor.SecondaryTaskProcessor;

/**
 * Task type enum.
 */
public enum TaskType {
    /**
     *
     */
    MAIN_TASK(MainTaskProcessor.class),

    /**
     *
     */
    SECONDARY_TASK(SecondaryTaskProcessor.class),

    /**
     *
     */
    FULLDUMP_TASK(FullDumpTaskProcessor.class),

    /**
     *
     */
    DELETE_TABLE_TASK(DeleteTableTaskProcessor.class),

    /**
     *
     */
    REFRESH_TABLE_TASK(RefreshTableTaskProcessor.class),

    /**
     *
     */
    DEPLOY_TASK(DeployTaskProcessor.class),

    /**
     *
     */
    ADD_SCHEMA_TASK(AddSchemaTaskProcessor.class);

    private Class<? extends BaseTaskProcessor> processor;

    TaskType(Class<? extends BaseTaskProcessor> processor) {
        this.processor = processor;
    }

    public Class<? extends BaseTaskProcessor> getProcessor() {
        return processor;
    }
}
