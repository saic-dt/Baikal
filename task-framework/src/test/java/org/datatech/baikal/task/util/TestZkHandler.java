package org.datatech.baikal.task.util;

import java.util.Collections;
import java.util.List;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.datatech.baikal.common.Bytes;
import org.datatech.baikal.common.Configuration;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class TestZkHandler {
    private static final Logger logger = LoggerFactory.getLogger(TestZkHandler.class);

    @Autowired
    private ZkHandler handler;

    @Test
    public void testQueueDequeue() throws Throwable {
        for (int i = 0; i < 10; i++) {
            String work = new String("testWork [" + i + "]");
            handler.queueMessage("/queue/main_task", Bytes.toBytes(work));
            logger.info("Queued [{}]", i);
        }

        byte[] msg = handler.dequeueMessage("/queue/main_task");
        while (msg != null) {
            logger.info("received element is [{}]", new String(msg));
            msg = handler.dequeueMessage("/queue/main_task");
        }
    }

    @Test
    public void testZkNode() throws Throwable {
        String seqPath = handler.getClient().create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/schema/smpv/dol/notify/seq-");
        int node = Integer.parseInt(ZKPaths.getNodeFromPath(seqPath).substring(4));
        logger.info("create eph seq return is [{}], node is [{}]", seqPath, node);

        List<String> nodes = handler.getClient().getChildren().forPath("/schema/smpv/dol/notify");
        Collections.sort(nodes);

        logger.info("notify seq size is [{}], first node is [{}]", nodes.size(),
                nodes.size() == 0 ? null : nodes.get(0));

        String nodePath = ZKPaths.makePath("/schema/smpv/dol/notify", nodes.get(0));
        handler.getClient().delete().forPath(nodePath);
    }

    @Test
    public void testZkProperty() throws Throwable {
        int maxTask = Config.getIntProperty(Config.CFG_MAX_TASK, Config.DEFAULT_MAX_TASK);
        int maxTaskPerHost = Config.getIntProperty(Config.CFG_MAX_TASK_PER_HOST, Config.DEFAULT_MAX_TASK_PER_HOST);
        logger.info("zookeeper property is [{}], [{}]", maxTask, maxTaskPerHost);
    }

    @Test
    public void testZkWatch() throws Throwable {
        CuratorWatcher watcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                logger.info("event received [{}]", event);
                logger.info("notify size is [{}]",
                        handler.getClient().getChildren().forPath("/schema/smpv/dol/notify").size());
                handler.getClient().getChildren().usingWatcher(this).forPath("/schema/smpv/dol/notify");
            }
        };
        handler.getClient().getChildren().usingWatcher(watcher).forPath("/schema/smpv/dol/notify");
        testZkNode();
        // Thread.sleep(100000);
        // try "create -s /datalake/schema/smpv/dol/notify/seq- test" using zkCli
    }

    @Test
    public void testZkSeqPath() throws Throwable {
        // following code is the previous logic to determine whether application adapter has been notified
        String instanceName = "smpv";
        String schemaName = "dol";
        String seqNode = "seq-0000000033";

        String notifyPath = ZKPaths.makePath(Configuration.PATH_SCHEMA, instanceName, schemaName, Config.PATH_NOTIFY);
        List<String> nodes = handler.getClient().getChildren().forPath(notifyPath);
        if (nodes.size() > 0) {
            Collections.sort(nodes, Collections.reverseOrder());
            int maxSeq = ProcessorUtil.getSeqFromPath(nodes.get(0));
            int taskSeq = ProcessorUtil.getSeqFromPath(seqNode);
            int taskCount = 0;
            for (String node : nodes) {
                int lastSeq = ProcessorUtil.getSeqFromPath(node);
                if (lastSeq >= taskSeq) {
                    taskCount++;
                } else {
                    break;
                }
            }
            logger.info("taskCount is [{}], maxSeq is [{}], taskSeq is [{}]", taskCount, maxSeq, taskSeq);
            if ((taskCount == 0) || (taskCount < (maxSeq - taskSeq + 1))) {
                logger.info("application adapter has been refreshed");
            } else {
                // application adapter not refreshed, put task back to queue
                // handler.queueTask(task.toBytes());
                logger.info("application adapter not refreshed, put task back to queue");
            }
        }
    }
}
