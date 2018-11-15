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
package org.datatech.baikal.task.processor;

import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.DtCmdType;
import org.datatech.baikal.task.common.SourceDb;
import org.datatech.baikal.task.dao.SourceDbDao;
import org.datatech.baikal.task.util.DtCmdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Task to deploy new database schema.
 */
@Service
public class AddSchemaTaskProcessor extends BaseTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AddSchemaTaskProcessor.class);

    @Autowired
    private SourceDbDao sourceDbDao;

    @Autowired
    private DtCmdUtil dtCmdUtil;

    /**
     * Perform deploy task to execute dbinstall and dbconsole command.
     *
     * @param task task
     * @throws Exception Exception
     */
    @Override
    public void execute(BaseTask task) throws Exception {
        String instanceName = task.getInstanceName();
        String schemaName = task.getSchemaName();
        SourceDb esourceDb = task.getSourceDb();
        String dbSid = esourceDb.getDB_SID();
        String osIp = esourceDb.getOS_IP();
        String rmtIp = esourceDb.getRMT_IP();
        DbType dbType = esourceDb.getDB_TYPE();
        String osUser = esourceDb.getOS_USER();
        String basePath = esourceDb.getBASE_PATH();
        SourceDb rsourceDb = sourceDbDao.get(rmtIp, instanceName);
        String rosUser = rsourceDb.getOS_USER();
        String rbasePath = rsourceDb.getBASE_PATH();
        dtCmdUtil.executeConsoleSchemaCmd(DtCmdType.R_ADD_SCHEMA, DbType.HBASE, rosUser, rmtIp, instanceName,
                rbasePath, schemaName, dbType, osIp, dbSid);
        dtCmdUtil.executeConsoleCmd(DtCmdType.E_ADD_SCHEMA, dbType, osUser, osIp, dbSid, basePath, schemaName, null);

    }
}
