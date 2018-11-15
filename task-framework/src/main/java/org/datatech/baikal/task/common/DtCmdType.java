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
 * Command type enum for backend synchronization control.
 */
public enum DtCmdType {
    /**
     * Add transfer for extract process.
     */
    E_ADD_TRAN,

    /**
     * Add semaphore to extract process.
     */
    E_SEM_TRAN,

    /**
     * Add table to replicate process.
     */
    R_ADD_TABLE,

    /**
     * Delete table to replicate process.
     */
    R_DEL_TABLE,

    /**
     * Add database to extract process.
     */
    E_ADD_D,

    /**
     * Add schema to extract process.
     */
    E_ADD_SCHEMA,

    /**
     * Add schema to replicate process.
     */
    R_ADD_SCHEMA
}
