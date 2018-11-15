package org.datatech.baikal.task.processor;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.datatech.baikal.task.Main;
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
public class TestSecondaryTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TestSecondaryTaskProcessor.class);

    @Autowired
    private DbHelper dbHelper;

    @Test
    public void testJdbcMeta() throws Exception {
        String instanceName = "smpv";
        String schemaName = "hr";
        String tableName = "employees";
        Pair<List<String>, List<String>> p = dbHelper.getOracleTableSchema(instanceName, schemaName, tableName);
        logger.info("column types: [{}], columns: [{}]", p.getLeft(), p.getRight());
        p = dbHelper.getTableSchema(instanceName, schemaName, tableName);
        logger.info("column types: [{}], columns: [{}]", p.getLeft(), p.getRight());

        instanceName = "db2";
        schemaName = "db2inst1";
        tableName = "employee";
        p = dbHelper.getTableSchema(instanceName, schemaName, tableName);
        logger.info("column types: [{}], columns: [{}]", p.getLeft(), p.getRight());
    }
}
