package org.datatech.baikal.task.processor;

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.datatech.baikal.task.Config;
import org.datatech.baikal.task.common.BaseTask;
import org.datatech.baikal.task.util.ZkHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

public class TestZKNodeCreate {
    private ZkHandler zkHandler;
    @Value("${zookeeper.quorum}")
    private String zookeeperQuorum;

    @Test
    public void createNode(BaseTask task) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder().retryPolicy(new RetryOneTime(10))
                .namespace(Config.ZK_NAMESPACE).ensembleProvider(new FixedEnsembleProvider(zookeeperQuorum))
                .connectionTimeoutMs(0).build();
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(
                "/sandbox" + "/" + task.getSandBoxName(),
                ("{\"jdbcUrl\":\"jdbc:ignite:thin://172.17.0.2:10800/\", \"user\":\"\", \"password\":\"\", \"driverClass\":\"org.apache.ignite.IgniteJdbcThinDriver\"}")
                        .getBytes());

    }

}
