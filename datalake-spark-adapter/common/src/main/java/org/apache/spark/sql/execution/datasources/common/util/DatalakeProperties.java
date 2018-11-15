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

package org.apache.spark.sql.execution.datasources.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.spark.sql.execution.datasources.common.constant.DatalakeConstant;
import org.apache.spark.sql.execution.datasources.common.logging.LogService;
import org.apache.spark.sql.execution.datasources.common.logging.LogServiceFactory;

/**
 * Load properties for Spark adapter module.
 */
public final class DatalakeProperties implements Serializable {

    private static final LogService LOGGER = LogServiceFactory.getLogService(DatalakeProperties.class.getName());
    /**
     * class instance
     */
    private static final DatalakeProperties DPINSTANCE = new DatalakeProperties();

    /**
     * properties
     */
    private Properties datalakeProperties;

    private DatalakeProperties() {
        datalakeProperties = new Properties();
        loadProperties();
    }

    public static DatalakeProperties getInstance() {
        return DPINSTANCE;
    }

    private void loadProperties() {
        String property = System.getProperty(DatalakeConstant.DATALAKE_PROPERTIES_FILE_PATH);
        if (null == property) {
            property = DatalakeConstant.DEFAULT_DATALAKE_PROPERTIES_PATH;
        }

        File file = new File(property);
        LOGGER.info("Property file path: " + file.getAbsolutePath());

        FileInputStream fileInputStream = null;
        try {
            if (file.exists()) {
                fileInputStream = new FileInputStream(file);
                datalakeProperties.load(fileInputStream);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(">>>>> File Not Found! " + DatalakeConstant.DEFAULT_DATALAKE_PROPERTIES_PATH);
        } catch (IOException e) {
            LOGGER.error(">>>>> Error while reading the file: " + DatalakeConstant.DEFAULT_DATALAKE_PROPERTIES_PATH);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    LOGGER.error(">>>>> Error while closing the file stream for file: "
                            + DatalakeConstant.DEFAULT_DATALAKE_PROPERTIES_PATH);
                }
            }
        }

        print();
    }

    private String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (null == value) {
            return defaultValue;
        }
        return value;
    }

    private String getProperty(String key) {
        return datalakeProperties.getProperty(key);
    }

    public String getDefaultHDFSRootPath() {
        String path = getProperty(DatalakeConstant.DATALAKE_HDFS_ROOT_PATH_NAME,
                DatalakeConstant.DATALAKE_DEFAULT_HDFS_ROOT_PATH);
        return path;
    }

    public String getDateFormat() {
        String dateFormat = getProperty(DatalakeConstant.DATALAKE_DATE_FORMAT_NAME,
                DatalakeConstant.DATALAKE_DEFAULT_DATE_FORMAT);
        return dateFormat;
    }

    public String getTimestampFormat() {
        String timestampFormat = getProperty(DatalakeConstant.DATALAKE_TIMESTAMP_FORMAT_NAME,
                DatalakeConstant.DATALAKE_DEFAULT_TIMESTAMP_FORMAT);
        return timestampFormat;
    }

    private void print() {
        LOGGER.info("------Using Datalake.properties --------");
        LOGGER.info(datalakeProperties.toString());
    }

}
