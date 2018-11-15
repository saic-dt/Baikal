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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.SourceJdbc;
import org.datatech.baikal.task.dao.MetaConfigDao;
import org.datatech.baikal.task.dao.SourceJdbcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Utility class providing methods to manage tables and databases on data lake.
 */
@Service
public class HiveUtil {
    private static final Logger logger = LoggerFactory.getLogger(HiveUtil.class);
    public boolean failedflag = false;
    @Autowired
    private DbHelper dbHelper;
    @Autowired
    private MetaConfigDao metaConfigDao;
    @Autowired
    private ZkHandler handler;
    @Autowired
    private SourceJdbcDao sourceJdbcDao;
    @Value("${tenant.name:@null}")
    private String tenantName;

    /**
     * create hive table using custom storage handler.
     * @param instanceName instanceName
     * @param schemaName schemaName
     * @param tableName tableName
     * @throws Exception Exception
     */
    /*public void createHiveTable(String instanceName, String schemaName,
                              String tableName) throws Exception {
    String hbaseRowkeyPrefix = metaConfigDao.getTablePrefix(instanceName, schemaName, tableName);
    createHiveTable(instanceName, schemaName, tableName, hbaseRowkeyPrefix);
    }*/

    /**
     * create hive table using custom storage handler.
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @param location     location
     * @throws Exception Exception
     */
    public void createHiveTable(String instanceName, String schemaName, String tableName, String location)
            throws Exception {
        // not implemented
        logger.info("skip create hive table");
    }

    public void createSparkDatabase(String instanceName, String schemaName, String tableName) throws Exception {
        String databaseName = getDatabaseName(instanceName, schemaName);
        String ddl = String.format("CREATE DATABASE IF NOT EXISTS %s", databaseName + "___s");
        launchSparkTask(instanceName, schemaName, tableName, ddl);
    }

    public String getNamespace() {
        return (tenantName == null) ? Config.DEFAULT_TBL_NAMESPACE : tenantName;
    }

    public void createSparkTable(String instanceName, String schemaName, String tableName, String tablePrefix)
            throws Exception {
        dropSparkTable(instanceName, schemaName, tableName);
        String sourcePK;
        List<Pair<String, String>> columnList = new ArrayList<>();
        SourceJdbc sourceJdbc = sourceJdbcDao.get(instanceName, schemaName);
        if (sourceJdbc.getDB_TYPE().name().equals(Config.DB_TYPE_MONGO)) {
            sourcePK = "_id";
            columnList = MongoUtil.getColumnList(sourceJdbc, instanceName, schemaName, tableName);
            logger.info("columnName [{}]", columnList);
        } else {
            sourcePK = metaConfigDao.getSoucePk(instanceName, schemaName, tableName);
            columnList = dbHelper.getColumnList(instanceName, schemaName, tableName);
        }

        StringBuffer dbColumns = new StringBuffer();
        for (int i = 0; i < columnList.size(); i++) {
            Pair<String, String> p = columnList.get(i);
            if (i == columnList.size() - 1) {
                dbColumns.append(p.getLeft() + " " + p.getRight());
            } else {
                dbColumns.append(p.getLeft() + " " + p.getRight() + ",");
            }
        }
        String databaseName = getDatabaseName(instanceName, schemaName);
        String tenantName = getNamespace();

        String ddl = String.format(
                "CREATE TABLE if not exists %s.%s (%s) " + " using org.apache.spark.sql.execution.datasources.sparquet"
                        + " options(\"tenant\" \'%s\'," + "\"instance\" \"%s\", " + "\"schema\" \"%s\", "
                        + "\"table\" \"%s\", " + "\"prefix\" \"%s\", " + "\"sourcepk\" \"%s\") ",
                databaseName + "___s", tableName, dbColumns, tenantName, instanceName, schemaName, tableName,
                tablePrefix, sourcePK);
        logger.info("execute spark ddl, ddl is [{}]", ddl);
        launchSparkTask(instanceName, schemaName, tableName, ddl);
        logger.info("Spark table created [{}.{}]", schemaName, tableName);
    }

    public void dropSparkTable(String instanceName, String schemaName, String tableName) throws Exception {
        String databaseName = getDatabaseName(instanceName, schemaName);
        String ddl = String.format("DROP TABLE IF EXISTS %s.%s", databaseName + "___s", tableName);
        launchSparkTask(instanceName, schemaName, tableName, ddl);
        logger.info("dropped hive table [{}.{}]", databaseName + "___s", tableName);
    }

    /**
     * launch spark task to execute spark jobs
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @param sqlddl       sqlddl
     * @throws Exception exception
     */

    public void launchSparkTask(String instanceName, String schemaName, String tableName, String sqlddl)
            throws Exception {
        String sparkTaskJarName = handler.getParquetJarName();
        logger.info("sparkTaskJarName [{}]", sparkTaskJarName);

        SparkLauncher launcher = new SparkLauncher();
        launcher.setAppResource(sparkTaskJarName);
        launcher.setMainClass("org.datatech.baikal.fulldump.CreateSparkTable");
        launcher.addAppArgs(sqlddl, "", "");
        launcher.setMaster("local[*]");
        SparkAppHandle handle = launcher.startApplication();
        int retrytimes = 0;
        while (handle.getState() != SparkAppHandle.State.FINISHED) {
            retrytimes++;
            Thread.sleep(5000L);
            logger.info("applicationId is [{}]", handle.getAppId());
            logger.info("current state [{}]", handle.getState());
            if ((handle.getAppId() == null && handle.getState() == SparkAppHandle.State.FAILED) && retrytimes > 8) {
                logger.info("can not start spark job for creating spark table. Creating spark table failed. ");
                metaConfigDao.updateMetaFlag(instanceName, schemaName, tableName, Config.META_FLAG_TASK_FAILED);
                failedflag = true;
                break;
            }
        }
        logger.info("launcher over");
    }

    /**
     * drop hive table.
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @throws Exception Exception
     */
    public void dropHiveTable(String instanceName, String schemaName, String tableName) throws Exception {
        // not implemented
        logger.info("skip drop hive table [{}.{}]", schemaName, tableName);
    }

    public String getDatabaseName(String instanceName, String schemaName) {
        return String.join(Config.HBASE_TABLENAME_DELIMITER, getNamespace(), instanceName, schemaName);
    }

    public String getParquetLocation(String tenantName, String instanceName, String schemaName, String tableName,
            String tablePrefix, String tableVersion) {
        Configuration conf = new org.apache.hadoop.conf.Configuration();
        String hostName = conf.get("fs.defaultFS");
        logger.info("hostName [{}]", hostName);
        String location = hostName + "/datalake/" + tenantName + "/" + instanceName + "/" + schemaName + "/" + tableName
                + "/" + tablePrefix + "/" + tableVersion;
        return location;
    }
}
