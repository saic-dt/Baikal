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

package org.datatech.baikal.task.dao;

import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.SourceJdbc;
import org.datatech.baikal.task.util.Bytes;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Data Access Object for source JDBC table.
 */
@Service
public class SourceJdbcDao {
    private static final Logger logger = LoggerFactory.getLogger(SourceJdbcDao.class);

    @Autowired
    private ZkHandler handler;

    /**
     * Retrieve a source jdbc object from SourceJdbc table.
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @return a source jdbc object
     * @throws Exception Exception
     */
    public SourceJdbc get(String instanceName, String schemaName) throws Exception {
        String sourceJdbcPath = handler.getSourceJdbcPath(instanceName, schemaName);
        String sourceJdbcValue = Bytes.toString(handler.getClient().getData().forPath(sourceJdbcPath));
        logger.info("sourceJdbcValue [{}]", sourceJdbcValue);
        try {
            SourceJdbc sourceJdbc = new SourceJdbc();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(sourceJdbcValue).getAsJsonObject();
            sourceJdbc.setINSTANCE_NAME(instanceName);
            sourceJdbc.setSCHEMA_NAME(schemaName);
            sourceJdbc.setJDBC_URL((obj.get("JDBC_URL") == null) ? null : obj.get("JDBC_URL").toString());
            String dbTypeStr = obj.get("DB_TYPE").toString().trim().replaceAll("\"", "");
            sourceJdbc.setDB_TYPE(DbType.valueOf(dbTypeStr));
            sourceJdbc.setUSER((obj.get("USER") == null) ? null : obj.get("USER").toString());
            sourceJdbc.setPASSWORD((obj.get("PASSWORD") == null) ? null : obj.get("PASSWORD").toString());
            sourceJdbc.setOS_IP((obj.get("OS_IP") == null) ? null : obj.get("OS_IP").toString());
            sourceJdbc.setRMT_IP((obj.get("RMT_IP") == null) ? null : obj.get("RMT_IP").toString());
            sourceJdbc.setUSE_CANAL(
                    (obj.get("USE_CANAL") == null) ? false : Boolean.valueOf(obj.get("USE_CANAL").toString()));
            return sourceJdbc;
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }
        return null;
    }
}
