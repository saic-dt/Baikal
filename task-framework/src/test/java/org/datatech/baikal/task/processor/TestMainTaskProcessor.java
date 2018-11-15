package org.datatech.baikal.task.processor;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.datatech.baikal.task.Main;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.util.DbHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class TestMainTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TestMainTaskProcessor.class);

    @Autowired
    private MainTaskProcessor processor;

    @Autowired
    private SecondaryTaskProcessor processor1;

    @Autowired
    private DbHelper dbHelper;
    @Autowired
    private MainTaskProcessor mainTaskProcessor;

    @Test
    public void testGetHiveDdl() throws Exception {
        Method method = MainTaskProcessor.class.getDeclaredMethod("getHiveDdl", String.class, String.class,
                String.class, String.class);
        method.setAccessible(true);
        String ddl = (String) method.invoke(processor, "smpv", "hr", "job_history", "00000000");
        logger.info("return ddl is [{}]", ddl);
    }

    @Test
    public void testFullDump() throws Exception {
        Method method = SecondaryTaskProcessor.class.getDeclaredMethod("doFullDump", String.class, String.class,
                String.class, String.class, boolean.class);
        method.setAccessible(true);
        method.invoke(processor1, "smpv", "hr", "employees", "00000000", true);
    }

    @Test
    public void testGetOracleSchema() throws Exception {
        MainTaskProcessor processor = new MainTaskProcessor();
        Pair<List<String>, List<String>> p = dbHelper.getTableSchema("smpv", "hr", "employees");
        logger.info("oracle column type list is [{}], column list is [{}]", p.getLeft(), p.getRight());
        p.getRight().replaceAll(x -> "c:" + x);
        logger.info("replaced column list is [{}]", p.getRight());
    }

    @Test
    public void testCreate() throws Exception {
        BaseTask baseTask = new BaseTask("instance1", "schema1", "table1", "sandbox1");
        processor.execute(baseTask);
    }

    @Test
    public void testMainTask() throws Exception {
        BaseTask baseTask = new BaseTask("instance1", "schema1", "table1", "sandbox1");
        mainTaskProcessor.execute(baseTask);
    }
}
