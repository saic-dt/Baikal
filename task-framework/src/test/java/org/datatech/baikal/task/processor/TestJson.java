package org.datatech.baikal.task.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.datatech.baikal.task.util.RestfulUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestJson {
    private static final Logger logger = LoggerFactory.getLogger(TestJson.class);

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("instanceName", "instance1");
        json.put("schemaName", "schema1");
        json.put("tableName", "table1");
        json.put("status", true);
        String notifyMessage = JSON.toJSONString(json);
        logger.info(notifyMessage);
    }
}
