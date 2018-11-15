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

package org.datatech.baikal.merge.constants;

public class ZooKeeperConstants {

    public static final String DATALAKE_ROOT = "/datalake";

    public static final String DATALAKE_MERGE_LOCK = "/datalake/merge";

    public static final String DATALAKE_LAST_SUCCESSFUL_MERGE = "/datalake/last_successful_merge";

    public static final String DATALAKE_MERGE_SKIP_DB_LIST = "/datalake/merge_skip_db_list";

    public static final String DATALAKE_MERGE_SKIP_TBL_LIST = "/datalake/merge_skip_tbl_list";

    public static final String DATALAKE_HIVE_JDBC_URL = "/datalake/config/hive.jdbc.url";

    public static final String DATALAKE_HIVE_JDBC_USER = "/datalake/config/hive.jdbc.user";

    public static final String DATALAKE_HIVE_JDBC_PASSWORD = "/datalake/config/hive.jdbc.password";

    public static final String SOURCEJDBC_NODE = "/SourceJdbc";

    public static final String METASTORE_NODE = "/metastore";

    public static final String META_NODE = "/meta";

    public static final String HIVE_METASTORE_JDBC_URL = "/datalake/config/hive.metastore.jdbc.url";

    public static final String HIVE_METASTORE_JDBC_USER = "/datalake/config/hive.metastore.jdbc.user";

    public static final String HIVE_METASTORE_JDBC_PASSWORD = "/datalake/config/hive.metastore.jdbc.password";

}
