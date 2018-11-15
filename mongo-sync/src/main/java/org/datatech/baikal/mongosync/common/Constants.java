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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Constants definition.
 */
@Component
public class Constants {

    private final static Long DEF_HADOOP_BLOCK_SIZE = 64L;
    private final static Integer DEF_SYNC_CYCLE_SECOND = 5;
    private final static Integer DEF_MAX_POOL_SIZE = 10;
    public static String FS_DEFAULTFS;
    public static String ZOOKEEPER_QUORUM;
    public static Long HADOOP_BLOCK_SIZE;
    public static String OPLOG_COLLECTION_NAME;
    public static Integer SYNC_CYCLE_SECOND;
    public static Integer MAX_POOL_SIZE;
    public static Boolean KRB_ENABLE;
    public static String KRB_PRINCIPAL;
    public static String KRB_KEYTAB_PATH;
    public static String TEST_MODEL;
    public static Boolean CIPHER_ENABLE;
    public static Integer BATCH_SAVE_HDFS_SIZE;
    private static Logger logger = LoggerFactory.getLogger(Constants.class);

    private static long getHadoopBlockSize(String data) {
        if (data == null || "".equals(data) || !Pattens.NUMBER_PATTERN.matcher(data).matches()) {
            logger.warn("cannot get hadoop.block.size config ! using def hadoop.block.size config "
                    + DEF_HADOOP_BLOCK_SIZE + "MB ");
            return DEF_HADOOP_BLOCK_SIZE * 1024 * 1024 - 1024;
        } else {
            return Long.valueOf(data) * 1024 * 1024 - 1024;
        }
    }

    private static Integer getSyncCycleSecond(String data) {
        if (data == null || "".equals(data) || !Pattens.NUMBER_PATTERN.matcher(data).matches()) {
            return DEF_SYNC_CYCLE_SECOND;
        } else {
            return Integer.valueOf(data);
        }

    }

    private static Integer getMaxPoolSize(String data) {
        if (data == null || "".equals(data) || !Pattens.NUMBER_PATTERN.matcher(data).matches()) {
            return DEF_MAX_POOL_SIZE;
        } else {
            return Integer.valueOf(data);
        }
    }

    @Value("${mongo-sync.batch.save.hdfs.size}")
    public void setBatchSaveHdfsSize(Integer batchSaveHdfsSize) {
        Constants.BATCH_SAVE_HDFS_SIZE = batchSaveHdfsSize;
    }

    @Value("${mongo-sync.fs.defaultFS}")
    public void setFsDefaultfs(String fsDefaultfs) {
        Constants.FS_DEFAULTFS = fsDefaultfs;
    }

    @Value("${mongo-sync.zookeeper.quorum}")
    public void setZookeeperQuorum(String zookeeperQuorum) {
        Constants.ZOOKEEPER_QUORUM = zookeeperQuorum;
    }

    @Value("${mongo-sync.hadoop.block.size}")
    public void setHadoopBlockSize(Long hadoopBlockSize) {
        Constants.HADOOP_BLOCK_SIZE = getHadoopBlockSize(hadoopBlockSize + "");
    }

    @Value("${mongo-sync.oplog.collection.name}")
    public void setOplogCollectionName(String oplogCollectionName) {
        Constants.OPLOG_COLLECTION_NAME = oplogCollectionName;
    }

    @Value("${mongo-sync.sync.cycle.second}")
    public void setSyncCycleSecond(Integer syncCycleSecond) {
        Constants.SYNC_CYCLE_SECOND = getSyncCycleSecond(syncCycleSecond + "");
    }

    @Value("${mongo-sync.max.pool.size}")
    public void setMaxPoolSize(Integer maxPoolSize) {
        Constants.MAX_POOL_SIZE = getMaxPoolSize(maxPoolSize + "");
    }

    @Value("${mongo-sync.krb.enable}")
    public void setKrbEnable(Boolean krbEnable) {
        Constants.KRB_ENABLE = krbEnable;
    }

    @Value("${mongo-sync.test.model}")
    public void setTestModel(String testModel) {
        Constants.TEST_MODEL = testModel;
    }

    @Value("${mongo-sync.krb.principal}")
    public void setKrbPrincipal(String krbPrincipal) {
        Constants.KRB_PRINCIPAL = krbPrincipal;
    }

    @Value("${mongo-sync.krb.keytab.path}")
    public void setKrbKeytabPath(String krbKeytabPath) {
        Constants.KRB_KEYTAB_PATH = krbKeytabPath;
    }

}
