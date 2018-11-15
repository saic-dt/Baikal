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

package org.apache.spark.sql.execution.datasources.common.logging;

import org.apache.log4j.Logger;

/**
 * Custom log service implementation.
 */
public final class DatalakeLogService implements LogService {

    private Logger logger;

    public DatalakeLogService(String className) {
        logger = Logger.getLogger(className);
    }

    public DatalakeLogService() {
        this("Datalake");
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isEnabledFor(org.apache.log4j.Level.WARN);
    }

    public void debug(String message) {
        if (logger.isDebugEnabled()) {
            logMessage(Level.DEBUG, null, message);
        }
    }

    public void error(String message) {
        logMessage(Level.ERROR, null, message);
    }

    public void error(Throwable throwable, String message) {
        logMessage(Level.ERROR, throwable, message);
    }

    public void error(Throwable throwable) {
        logMessage(Level.ERROR, throwable, "");
    }

    public void info(String message) {
        if (logger.isInfoEnabled()) {
            logMessage(Level.INFO, null, message);
        }
    }

    /**
     * Utility Method to log the the Message.
     */
    private void logMessage(Level logLevel, Throwable throwable, String message) {
        try {
            //Append the partition id and query id if exist
            StringBuilder buff = new StringBuilder(Thread.currentThread().getName());
            buff.append(" ");
            buff.append(message);
            message = buff.toString();
            if (Level.ERROR.toString().equalsIgnoreCase(logLevel.toString())) {
                logErrorMessage(throwable, message);
            } else if (Level.DEBUG.toString().equalsIgnoreCase(logLevel.toString())) {
                logDebugMessage(throwable, message);
            } else if (Level.INFO.toString().equalsIgnoreCase(logLevel.toString())) {
                logInfoMessage(throwable, message);
            } else if (Level.WARN.toString().equalsIgnoreCase(logLevel.toString())) {
                logWarnMessage(throwable, message);
            }

        } catch (Throwable t) {
            logger.error(t);
        }
    }

    private void logErrorMessage(Throwable throwable, String message) {

        if (null == throwable) {
            logger.error(message);
        } else {
            logger.error(message, throwable);
        }
    }

    private void logInfoMessage(Throwable throwable, String message) {

        if (null == throwable) {
            logger.info(message);
        } else {
            logger.info(message, throwable);
        }
    }

    private void logDebugMessage(Throwable throwable, String message) {

        if (null == throwable) {
            logger.debug(message);
        } else {
            logger.debug(message, throwable);
        }
    }

    private void logWarnMessage(Throwable throwable, String message) {

        if (null == throwable) {
            logger.warn(message);
        } else {
            logger.warn(message, throwable);
        }
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void warn(String message) {
        if (isWarnEnabled()) {
            logMessage(Level.WARN, null, message);
        }
    }

    enum Level {

        NONE(0), DEBUG(1), INFO(2), ERROR(3), WARN(4);

        /**
         * Constructor.
         *
         * @param level
         */
        Level(final int level) {
        }
    }
}
