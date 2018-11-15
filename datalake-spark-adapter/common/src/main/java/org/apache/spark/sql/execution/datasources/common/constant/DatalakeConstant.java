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

package org.apache.spark.sql.execution.datasources.common.constant;

/**
 * Constants definition.
 */
public class DatalakeConstant {
    public static final String DECRYPT_CHARSET = "iso-8859-1";

    public static final String DATALAKE_PROPERTIES_FILE_PATH = "datalake.properties.filepath";

    public static final String DATALAKE_HDFS_ROOT_PATH_NAME = "datalake.hdfs.root.path";
    public static final String DATALAKE_DEFAULT_HDFS_ROOT_PATH = "hdfs://localhost:9000/datalake";

    public static final String DATALAKE_DATE_FORMAT_NAME = "datalake.date.format";
    public static final String DATALAKE_DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATALAKE_TIMESTAMP_FORMAT_NAME = "datalake.timestamp.format";
    public static final String DATALAKE_DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATALAKE_PROPERTIES_PATH = "/conf/datalake.properties";

}
