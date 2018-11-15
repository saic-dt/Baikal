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

package org.datatech.baikal.task.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.DtCmdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Utility class providing methods to invoke backend scripts which manage incremental log transfer.
 */
@Service
public class DtCmdUtil {
    private static final Logger logger = LoggerFactory.getLogger(DtCmdUtil.class);

    @Autowired
    private SystemContext sysContext;

    /**
     * Execute dt console shell script.
     *
     * @param dtCmdType    dtCmdType
     * @param dbType       dbType
     * @param osUser       osUser
     * @param osIp         osIp
     * @param instanceName instanceName
     * @param basePath     basePath
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @throws Exception Exception
     */
    public void executeConsoleCmd(DtCmdType dtCmdType, DbType dbType, String osUser, String osIp, String instanceName,
                                  String basePath, String schemaName, String tableName) throws Exception {
        // execute dt console cmd
        String dtConsoleCmd = getConsoleCmdStr();
        if (dtConsoleCmd != null) {
            String cmdStr = dtCmdType.name().toLowerCase();
            ProcessBuilder pb;
            if (tableName != null) {
                pb = new ProcessBuilder(dtConsoleCmd, cmdStr, dbType.name(), osUser, osIp, instanceName, basePath,
                        schemaName, tableName);
            } else {
                pb = new ProcessBuilder(dtConsoleCmd, cmdStr, dbType.name(), osUser, osIp, instanceName, basePath,
                        schemaName);
            }
            execute(pb);
        }
    }

    /**
     * Execute dt console shell script for R_ADD_SCHEMA.
     *
     * @param dtCmdType    dtCmdType
     * @param dbType       dbType
     * @param osUser       osUser
     * @param osIp         osIp
     * @param instanceName instanceName
     * @param basePath     basePath
     * @param schemaName   schemaName
     * @param srcDbType    srcDbType
     * @param srcIp        srcIp
     * @param srcSid       srcSid
     * @throws Exception Exception
     */
    public void executeConsoleSchemaCmd(DtCmdType dtCmdType, DbType dbType, String osUser, String osIp,
                                        String instanceName, String basePath, String schemaName, DbType srcDbType, String srcIp, String srcSid)
            throws Exception {
        // execute dt console cmd
        String dtConsoleCmd = getConsoleCmdStr();
        if (dtConsoleCmd != null) {
            String cmdStr = dtCmdType.name().toLowerCase();
            ProcessBuilder pb = new ProcessBuilder(dtConsoleCmd, cmdStr, dbType.name(), osUser, osIp, instanceName,
                    basePath, schemaName, srcDbType.name(), srcIp, srcSid);
            execute(pb);
        }
    }

    /**
     * Execute dt console shell script for E_ADD_D.
     *
     * @param dbType       dbType
     * @param osUser       osUser
     * @param osIp         osIp
     * @param instanceName instanceName
     * @param basePath     basePath
     * @param rmtIp        rmtIp
     * @param rmtPort      rmtPort
     * @throws Exception Exception
     */
    public void executeConsoleAddDbCmd(DbType dbType, String osUser, String osIp, String instanceName, String basePath,
            String rmtIp, String rmtPort) throws Exception {
        // execute dt console E_ADD_D cmd
        String dtConsoleCmd = getConsoleCmdStr();
        if (dtConsoleCmd != null) {
            String cmdStr = DtCmdType.E_ADD_D.name().toLowerCase();
            ProcessBuilder pb = new ProcessBuilder(dtConsoleCmd, cmdStr, dbType.name(), osUser, osIp, instanceName,
                    basePath, rmtIp, rmtPort);
            execute(pb);
        }
    }

    /**
     * Execute command and log output.
     *
     * @param pb a ProcessBuilder object
     * @throws Exception Exception
     */
    public void execute(ProcessBuilder pb) throws Exception {
        logger.info("start invoking dt cmd [{}]", pb.command().toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStream stdout = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        String line;
        while ((line = reader.readLine()) != null) {
            logger.info("stdout: [{}]", line);
        }
    }

    /**
     * Get dt command full path string.
     *
     * @param cmd command string
     * @return dt command full path string
     * @throws Exception Exception
     */
    public String getCmdStr(String cmd) throws Exception {
        String hostname = sysContext.getPartialHostName();
        String cfgDtCmdPath = String.join(".", Config.CFG_DT_CMD_PATH, hostname);
        String dtCmdPath = Config.getStringProperty(cfgDtCmdPath, null);
        if (dtCmdPath != null) {
            String command = String.join(File.separator, dtCmdPath, cmd);
            if (System.getProperty("os.name").contains("Windows")) {
                // add suffix to dbconsole script on windows
                command += ".cmd";
            }
            return command;
        } else {
            logger.warn("dt cmd path not configured for running [{}]", cmd);
            return null;
        }
    }

    private String getConsoleCmdStr() throws Exception {
        String cmd = Config.getStringProperty(Config.CFG_DT_CMD_CONSOLE, Config.DEFAULT_DT_CMD_CONSOLE);
        return getCmdStr(cmd);
    }
}
