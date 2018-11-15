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

import org.datatech.baikal.task.Config;

/**
 * Database type.
 */
public enum DbType {
    /**
     * Oracle database type with its JDBC driver class.
     */
    ORACLE(Config.JDBC_CLASS_NAME_ORACLE),
    /**
     * DB2 database type with its JDBC driver class.
     */
    DB2(Config.JDBC_CLASS_NAME_DB2),
    /**
     * MySQL database type with its JDBC driver class.
     */
    MYSQL(Config.JDBC_CLASS_NAME_MYSQL),
    /**
     * Microsoft SQL Server database type with its JDBC driver class.
     */
    MSSQL(Config.JDBC_CLASS_NAME_MSSQL),
    /**
     * Mongo database type with its JDBC driver class.
     */
    MONGO(Config.JDBC_CLASS_NAME_MONGO),
    /**
     * Apache Ingite database type with its JDBC driver class.
     */
    IGNITE(Config.JDBC_CLASS_NAME_IGNITE),
    /**
     * Apache HBase without JDBC driver class.
     */
    HBASE(null);

    private String className;

    /**
     * Database type constructor.
     *
     * @param className JDBC driver class string.
     */
    DbType(String className) {
        this.className = className;
    }

    /**
     * Get JDBC driver class string.
     *
     * @return JDBC driver class string.
     */
    public String getClassName() {
        return className;
    }
}
