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

package org.datatech.baikal.mongosync.util;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.datatech.baikal.mongosync.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing method to access HDFS.
 */
public class HadoopUtils {
    private static final Logger logger = LoggerFactory.getLogger(HadoopUtils.class);
    private final static String TRUE = "true";
    private static Configuration conf = null;

    private static void initConnection() throws IOException {

        conf = new Configuration();
        if (Constants.KRB_ENABLE) {
            final String outdir = System.getProperty("loader.path");
            File core = new File(outdir + "core-site.xml");
            File hdfs = new File(outdir + "hdfs-site.xml");
            conf.addResource(core.toURI().toURL());
            conf.addResource(hdfs.toURI().toURL());
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(Constants.KRB_PRINCIPAL, Constants.KRB_KEYTAB_PATH);
        }
        if (TRUE.equals(Constants.TEST_MODEL)) {
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
            conf.set("fs.defaultFS", Constants.FS_DEFAULTFS);
            conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        }

    }

    /**
     * Get configuration to access HDFS
     * @return Configuration Configuration
     */
    public static Configuration getConf() {
        if (conf == null) {
            try {
                initConnection();
            } catch (IOException e) {
                logger.error("Exception occurred.", e);
                conf = null;
            }
        }
        return conf;
    }
}
