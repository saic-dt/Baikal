package org.datatech.baikal.task.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMonitorUtil {
    private static final Logger logger = LoggerFactory.getLogger(TestMonitorUtil.class);

    @Test
    public void testGetTimestamp() {
        logger.info("timestamp from 2017-08-16 17:58:13 is [{}]", MonitorUtil.getTimestamp("2017-08-16 17:58:13"));
        logger.info("timestamp from 2017-08-17 15:45:00 is [{}]", MonitorUtil.getTimestamp("2017-08-17 15:45:00"));
        logger.info("timestamp from 2017-09-25 12:43:05 is [{}]", MonitorUtil.getTimestamp("2017-09-25 12:43:05"));
        logger.info("timestamp from 2017-09-25 00:43:05 is [{}]", MonitorUtil.getTimestamp("2017-09-25 00:43:05"));
        logger.info("current timestamp is [{}]", System.currentTimeMillis());
    }
}
