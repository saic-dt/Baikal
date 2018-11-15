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

import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.SourceDb;
import org.datatech.baikal.task.dao.SourceDbDao;
import org.datatech.baikal.task.util.DtCmdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Task to execute deployment scripts.
 */
@Service
public class DeployTaskProcessor extends BaseTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DeployTaskProcessor.class);

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
        SourceDb sourceDb = task.getSourceDb();
        sourceDbDao.insert(task.getInstanceName(), task.getSchemaName(), sourceDb);
        String cmd = Config.getStringProperty(Config.CFG_DT_CMD_INSTALL, Config.DEFAULT_DT_CMD_INSTALL);
        String dtInstallCmd = dtCmdUtil.getCmdStr(cmd);
        if (dtInstallCmd != null) {
            ProcessBuilder pb = null;
            DbType dbType = sourceDb.getDB_TYPE();
            if (dbType != null) {
                String osUser = sourceDb.getOS_USER();
                String osIp = sourceDb.getOS_IP();
                String basePath = sourceDb.getBASE_PATH();
                String dbHome = sourceDb.getDB_HOME();
                String dbSid = sourceDb.getDB_SID();
                String ggUser = sourceDb.getGG_USER();
                String ggPass = sourceDb.getGG_PASS();
                String mgrPort = sourceDb.getMGR_PORT();
                switch (dbType) {
                case DB2:
                case ORACLE:
                    pb = new ProcessBuilder(dtInstallCmd, dbType.name(), osUser, osIp, basePath, dbHome, dbSid, ggUser,
                            ggPass, mgrPort);
                    break;
                case MYSQL:
                    pb = new ProcessBuilder(dtInstallCmd, dbType.name(), osUser, osIp, basePath, dbHome, dbSid, ggUser,
                            ggPass, mgrPort, sourceDb.getDB_LOG(), sourceDb.getDB_PORT(), sourceDb.getADMIN_USER(),
                            sourceDb.getADMIN_PASS());
                    break;
                case HBASE:
                    pb = new ProcessBuilder(dtInstallCmd, dbType.name(), osUser, osIp, basePath, dbSid, mgrPort,
                            sourceDb.getKerberos(), sourceDb.getKerberosKeytabFile(), sourceDb.getKerberosPrinciple());
                    break;
                default:
                    break;
                }
                if (pb != null) {
                    dtCmdUtil.execute(pb);
                }
                String rmtIp = sourceDb.getRMT_IP();
                String rmtPort = sourceDb.getRMT_PORT();
                if ((rmtIp != null) && (rmtPort != null)) {
                    dtCmdUtil.executeConsoleAddDbCmd(dbType, osUser, osIp, dbSid, basePath, rmtIp, rmtPort);
                } else {
                    logger.warn("rmt ip or port not valid, rmtIp: [{}], rmtPort: [{}]", rmtIp, rmtPort);
                }
            } else {
                logger.warn("db type is not valid in deploy task message");
            }
        }
    }
}
