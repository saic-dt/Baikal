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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.data.Stat;
import org.bson.Document;
import org.datatech.baikal.mongosync.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import net.sf.json.JSONObject;

/** 
* ZkHandler Tester. 
*/
@ComponentScan(basePackages = { "org.datatech.baikal.mongosync.sync" })
public class ZkHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(ZkHandlerTest.class);

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        //TODO: Test goes here...
        CuratorFramework client = CuratorFrameworkFactory.builder().retryPolicy(new RetryOneTime(10))
                .namespace("datalake").ensembleProvider(new FixedEnsembleProvider("127.0.0.1:2181"))
                .connectionTimeoutMs(6000).build();
        client.start();

        List<String> tables = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            String tableName = UUID.randomUUID().toString().toUpperCase();

            String notifyPath = "/mongo-schema/datalake/local/test/notify/seq-"
                    + StringUtil.fillXInFrontOfString(i + "", "0", 9);
            String tablePath = "/metastore/datalake/meta/local/test";
            Stat stat = client.checkExists().forPath(notifyPath);
            if (stat == null) {
                JSONObject obj = new JSONObject();
                obj.put("taskType", "SECONDARY_TASK");
                obj.put("instanceName", "local");
                obj.put("schemaName", "test");
                obj.put("tableName", tableName);
                obj.put("tablePrefix", "0aaaaaaaa");
                obj.put("tenantName", "datalake");
                logger.info(obj.toString());
                client.create().creatingParentContainersIfNeeded().forPath(notifyPath, obj.toString().getBytes());
            }

            Stat tableStat = client.checkExists().forPath(tablePath + "/" + tableName);
            if (tableStat == null) {
                JSONObject tableObj = new JSONObject();
                tableObj.put("TABLE_VERSION", System.currentTimeMillis() / 1000);
                tableObj.put("SOURCE_INSTANCE", "local");
                tableObj.put("SOURCE_SCHEMA", "test");
                tableObj.put("SOURCE_TABLE", tableName);
                tableObj.put("PREFIX", "0aaaaaaaa");
                tableObj.put("TOTAL_WORK", "0");
                tableObj.put("SOFAR", "0");
                tableObj.put("META_FLAG", "0");
                client.create().creatingParentContainersIfNeeded().forPath(tablePath + "/" + tableName,
                        tableObj.toString().getBytes());
            }
            tables.add(tableName);
        }

        MongoClientURI puri = new MongoClientURI("mongodb://172.18.0.2:27017");
        MongoClient mongoClient = new MongoClient(puri);
        MongoDatabase db = mongoClient.getDatabase("test");

        MongoCollection<Document> doc = db.getCollection(tables.get(0));

        for (int i = 1; i <= 1000; i++) {
            Document document = new Document("name", StringUtil.getRandomString(5)).append("sex", Math.random() * 10)
                    .append("date", new Date()).append("age", Math.random() * 10).append("index", i);
            doc.insertOne(document);
            logger.info("table1:" + tables.get(0) + " data1:" + document);
        }

        mongoClient.close();
    }

    @Test
    public void remove() {
        logger.info("[{}]", Math.random() * 10);
    }

    @Test
    public void testInsetMongo() throws Exception {
        //TODO: Test goes here...

        List<String> tables = new ArrayList<>();
        tables.add("datalake" + Config.UNDERLINE_3 + "local" + Config.UNDERLINE_3 + "test" + Config.UNDERLINE_3
                + "AAAAAAAAAA");

        MongoClientURI puri = new MongoClientURI("mongodb://172.18.0.2:27017");
        MongoClient mongoClient = new MongoClient(puri);
        MongoDatabase db = mongoClient.getDatabase("test");
        long startTime = System.currentTimeMillis();
        for (String x : tables) {
            String[] arr = x.split(Config.UNDERLINE_3);
            MongoCollection<Document> doc = db.getCollection(arr[3]);
            for (int i = 0; i < 10; i++) {
                List<Document> list = new ArrayList<>();
                for (int j = 0; j < 1; j++) {
                    Document document = new Document();
                    document.append("name", "lisi").append("age", Math.random() * 100);
                    list.add(document);
                }
                doc.insertMany(list);

                if (i % 10 == 0) {
                    logger.info("data:" + i);
                }
            }
        }
        logger.info("speed Time:" + (System.currentTimeMillis() - startTime));

        mongoClient.close();
    }

}