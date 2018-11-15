package org.datatech.baikal.task;

import java.lang.reflect.Method;

import org.datatech.baikal.task.dao.MonitorSchemaDao;
import org.datatech.baikal.task.dao.MonitorTableDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class TestFramework {
    private static final Logger logger = LoggerFactory.getLogger(TestFramework.class);

    @Autowired
    private Framework framework;

    @Autowired
    private MonitorSchemaDao monitorSchemaDao;

    @Autowired
    private MonitorTableDao monitorTableDao;

    @Test
    public void testProcessDtStats() throws Exception {
        Method method = Framework.class.getDeclaredMethod("processDtStats");
        method.setAccessible(true);
        method.invoke(framework);
    }
}
