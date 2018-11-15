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

package org.datatech.baikal.web.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.datatech.baikal.web.common.conf.Config;

/**
 * 数据湖zk节点初始化
 */
public class ZKDataInit {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:32788")
                .namespace(Config.ZK_NAMESPACE).retryPolicy(retryPolicy).sessionTimeoutMs(1000 * 6)
                .connectionTimeoutMs(1000 * 6).build();

        client.start();

        // 系统用户
        String sysUser = "/users/admin";
        client.create().creatingParentsIfNeeded().forPath(sysUser);
        client.setData().forPath(sysUser,
                "{\"DESCRIPTION\":\"\",\"PASSWORD\":\"4197a074fe8dd626ca8269aa84ecd689\",\"TENANT_NAME\":\"datalake\"}"
                        .getBytes());

        // 租户
        String tenantPath = "/tenant/datalake";
        client.create().creatingParentsIfNeeded().forPath(tenantPath, "{\"DESCRIPTION\":\"defaultTenant\"}".getBytes());

        // 用户、角色
        String userPath = "/tenant/datalake/cluster_users";
        String rolePath = "/tenant/datalake/cluster_roles";
        client.create().creatingParentsIfNeeded().forPath(userPath, "".getBytes());
        client.create().creatingParentsIfNeeded().forPath(rolePath, "".getBytes());

        // 数据源管理
        String sourceDbPath = "/metastore/datalake/SourceDb";
        client.create().creatingParentsIfNeeded().forPath(sourceDbPath, "".getBytes());

        String sourceJdbcPath = "/metastore/datalake/SourceJdbc";
        client.create().creatingParentsIfNeeded().forPath(sourceJdbcPath, "".getBytes());

        client.close();

    }
}
