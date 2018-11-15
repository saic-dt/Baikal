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

package org.datatech.baikal.mongosync.common;

/**
 * Enum definitions for MongoDB operation type and database type.
 */
public class Enums {

    /**
     * MongoDB operation type enum.
     */
    public enum OpType {

        /**
         * Insert operation.
         */
        INSERT("i", "INSERT"),
        /**
         * Update operation.
         */
        UPDATE("u", "UPDATE"),
        /**
         * Delete operation.
         */
        DELETE("d", "DELETE"),
        /**
         * Drop operation.
         */
        DROP("c", "DROP");

        private String value;
        private String name;

        private OpType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum DataBaseType {

        /**
         * MongoDB database type enum
         */
        MONGO("MONGO", "MONGO");

        private String value;
        private String name;

        private DataBaseType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

}
