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

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.datatech.baikal.common.Bytes;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.SourceDb;
import org.datatech.baikal.task.util.ZkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Data Access Object for source database table.
 */
@Service
public class SourceDbDao {
    private static final Logger logger = LoggerFactory.getLogger(SourceDbDao.class);

    @Autowired
    private ZkHandler handler;

    /**
     * Insert a source db record.
     *
     * @param instanceName Instance Name
     * @param schemaName   Schema Name
     * @param sourceDb     Source Db
     * @throws Exception Exception
     */
    public void insert(String instanceName, String schemaName, SourceDb sourceDb) throws Exception {
        String sourceDBPath = handler.getSourceDBPath(instanceName, schemaName);
        String sourceDBPathNode = handler.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .forPath(sourceDBPath, Bytes.toBytes(sourceDb.toJson()));
        logger.info("SourceDB insert completed, sourceDBPathNode: [{}]", sourceDBPathNode);
    }

    /**
     * Retrieve a source db record.
     *
     * @param osIp         osIp
     * @param instanceName instanceName
     * @return a source db record
     * @throws Exception Exception
     */
    public SourceDb get(String osIp, String instanceName) throws Exception {
        if (osIp != null) {
            osIp = osIp.replaceAll("\"", "");
        }
        String sourceDBPath = handler.getSourceDBPath(osIp, instanceName);
        String sourceDBValue = Bytes.toString(handler.getClient().getData().forPath(sourceDBPath));
        logger.info("sourceDBValue [{}]", sourceDBValue);
        try {
            SourceDb sourceDB = new SourceDb();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(sourceDBValue).getAsJsonObject();
            sourceDB.setDB_SID(instanceName);
            String dbTypeStr = obj.get("DB_TYPE").toString().trim().replaceAll("\"", "");
            sourceDB.setDB_TYPE(DbType.valueOf(dbTypeStr));
            sourceDB.setOS_IP(osIp);
            sourceDB.setOS_USER((obj.get("OS_USER") == null) ? null : obj.get("OS_USER").toString());
            sourceDB.setBASE_PATH((obj.get("BASE_PATH") == null) ? null : obj.get("BASE_PATH").toString());
            sourceDB.setDB_HOME((obj.get("DB_HOME") == null) ? null : obj.get("DB_HOME").toString());
            sourceDB.setGG_USER((obj.get("GG_USER") == null) ? null : obj.get("GG_USER").toString());
            sourceDB.setGG_PASS((obj.get("GG_PASS") == null) ? null : obj.get("GG_PASS").toString());
            sourceDB.setMGR_PORT((obj.get("MGR_PORT") == null) ? null : obj.get("MGR_PORT").toString());
            sourceDB.setDB_LOG((obj.get("DB_LOG") == null) ? null : obj.get("DB_LOG").toString());
            sourceDB.setDB_PORT((obj.get("DB_PORT") == null) ? null : obj.get("DB_PORT").toString());
            sourceDB.setADMIN_USER((obj.get("ADMIN_USER") == null) ? null : obj.get("ADMIN_USER").toString());
            sourceDB.setADMIN_PASS((obj.get("ADMIN_PASS") == null) ? null : obj.get("ADMIN_PASS").toString());
            sourceDB.setRMT_IP((obj.get("RMT_IP") == null) ? null : obj.get("RMT_IP").toString());
            sourceDB.setRMT_PORT((obj.get("RMT_PORT") == null) ? null : obj.get("RMT_PORT").toString());
            return sourceDB;
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }
        return null;
    }

    public ArrayList<SourceDb> getAll() throws Exception {
        ArrayList<SourceDb> sourceDbs = new ArrayList<SourceDb>();
        String sourceDBOSIpPath = handler.getSourceDBOSIpPath();
        List<String> osIps = handler.getClient().getChildren().forPath(sourceDBOSIpPath);
        logger.info("OS IPs [{}]", osIps);
        for (String osIp : osIps) {
            String sourceDBInstancePath = handler.getSourceDBInstancePath(osIp);
            List<String> instanceNames = handler.getClient().getChildren().forPath(sourceDBInstancePath);
            for (String instanceName : instanceNames) {
                SourceDb sourceDb = get(osIp, instanceName);
                sourceDbs.add(sourceDb);
            }
        }
        return sourceDbs;
    }
}
