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

package org.datatech.baikal.web.modules.dashboard.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.entity.SourceDb;
import org.datatech.baikal.web.modules.dashboard.service.SourceDbService;
import org.datatech.baikal.web.modules.external.ZkHandler;
import org.datatech.baikal.web.utils.StringUtil;
import org.datatech.baikal.web.vo.DataManagerPageVO;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

@Service
public class SourceDbServiceImpl implements SourceDbService {

    // 一次请求的容量
    private static int pageContain = 100;

    @Resource
    private ZkHandler handler;

    @Override
    public DataManagerPageVO dbList(Integer pageFlg, String tenantName) throws Exception {

        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_METASTORE + "/" + tenantName + "/SourceDb";
        Stat stat = client.checkExists().forPath(path);
        List<SourceDb> entityList = new ArrayList<SourceDb>();
        DataManagerPageVO pageEntity = new DataManagerPageVO();
        if (null == stat) {
            pageEntity.setList(entityList);
            pageEntity.setCount(0);
            return pageEntity;
        }
        List<String> osips = client.getChildren().forPath(path);
        for (String osip : osips) {
            List<String> instances = client.getChildren().forPath(path + "/" + osip);
            for (String instance : instances) {
                String content = new String(client.getData().forPath(path + "/" + osip + "/" + instance));
                JSONObject data = JSONObject.fromObject(content);
                SourceDb entity = new SourceDb();
                entity.setDbType((String) data.get("DB_TYPE"));
                entity.setRowKey(osip + ":" + instance);
                entity.setDbName(instance);
                entity.setIpAddress(osip);
                entity.setMainProcPort(data.get("MGR_PORT") == null ? "" : (String) data.get("MGR_PORT"));
                entity.setBasePath(data.get("BASE_PATH") == null ? "" : (String) data.get("BASE_PATH"));
                entity.setRemoteCluster((String) data.get("RMT_IP"));
                entityList.add(entity);
            }
        }

        Collections.sort(entityList);

        if (0 >= pageFlg) {
            pageFlg = 99;
        }
        int fromIndex = pageFlg + 1 - pageContain;
        int toIndex = pageFlg + 1;
        int count = entityList.size();
        if (fromIndex > count) {
            throw new Exception("输入查询区间超过系统总数!");
        }
        if (toIndex > count) {
            toIndex = count;
        }

        List<SourceDb> subList = entityList.subList(fromIndex, toIndex);
        pageEntity.setCount(entityList.size());
        pageEntity.setList(subList);
        return pageEntity;
    }

    @Override
    public SourceDb dbEdit(String rowKey, String tenantName) throws Exception {
        CuratorFramework client = handler.getClient();
        String[] tmp = rowKey.split(":");
        String instance = Config.ZK_NODE_METASTORE + "/" + tenantName + "/SourceDb" + "/" + tmp[0] + "/" + tmp[1];
        String content = new String(client.getData().forPath(instance));
        JSONObject data = JSONObject.fromObject(content);
        SourceDb entity = new SourceDb();

        entity.setRowKey(rowKey);
        entity.setOsUser((String) data.get("OS_USER"));
        entity.setBasePath((String) data.get("BASE_PATH"));
        entity.setIpAddress(tmp[0]);
        entity.setDbHome((String) data.get("DB_HOME"));
        entity.setDbName(tmp[1]);
        entity.setMainProcPort((String) data.get("MGR_PORT"));
        entity.setDbLogPath((String) data.get("DB_LOG"));
        entity.setDbPort((String) data.get("RMT_PORT"));
        entity.setRootUser((String) data.get("ADMIN_USER"));
        entity.setLinkUser((String) data.get("GG_USER"));
        entity.setRemoteCluster((String) data.get("RMP_IP"));

        return entity;
    }

    @Override
    public void dbSave(SourceDb entity) throws Exception {
        CuratorFramework client = handler.getClient();
        JSONObject json = new JSONObject();
        String type = entity.getDbType();
        if ("MYSQL".equals(type) || "mysql".equals(type)) {
            json.element("ADMIN_USER", entity.getRootUser());
            json.element("DB_LOG", entity.getDbLogPath());
            json.element("DB_POR", entity.getDbPort());
        }
        json.element("DB_TYPE", entity.getDbType());
        // 服务器
        json.element("OS_USER", entity.getOsUser());
        json.element("BASE_PATH", entity.getBasePath());
        // 数据库
        json.element("DB_HOME", entity.getDbHome());
        // 链路
        json.element("GG_USER", entity.getLinkUser());
        json.element("MGR_PORT", entity.getMainProcPort());

        String remote = entity.getRemoteCluster();
        if (remote.length() > 0) {
            String[] split = remote.split(":");
            json.element("RMT_IP", split[0]);
            json.element("RMT_PORT", split[2]);
        } else {
            json.element("RMT_IP", entity.getIpAddress());
            json.element("RMT_PORT", entity.getMainProcPort());
        }

        String path = Config.ZK_NODE_METASTORE + "/" + entity.getTenantName() + "/SourceDb" + "/"
                + entity.getIpAddress() + "/" + entity.getDbName();
        Stat check = client.checkExists().forPath(path);
        if (null == check) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
        client.setData().forPath(path, json.toString().getBytes());
    }

    @Override
    public void dbDelete(String rowKey, String tenantName) throws Exception {
        CuratorFramework client = handler.getClient();
        String[] tmp = rowKey.split(Config.DELIMITER);
        String path = Config.ZK_NODE_METASTORE + "/" + tenantName + "/SourceDb" + "/" + tmp[0] + "/" + tmp[1];
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
    }

    @Override
    public List<String> getClusterList(String trenantName) throws Exception {
        List<String> clusterList = new ArrayList<String>();
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_METASTORE + "/" + trenantName + "/SourceDb";
        Stat check = client.checkExists().forPath(path);
        if (check != null) {
            List<String> osips = client.getChildren().forPath(path);
            for (String osip : osips) {
                List<String> instances = client.getChildren().forPath(path + "/" + osip);
                for (String instance : instances) {
                    String content = new String(client.getData().forPath(path + "/" + osip + "/" + instance));
                    if (StringUtil.isNotEmpty(content)) {
                        JSONObject data = JSONObject.fromObject(content);
                        String ip = (String) data.get("RMT_IP");
                        String sid = instance;
                        String port = (String) data.get("RMT_PORT");
                        clusterList.add(ip + ":" + sid + ":" + port);
                    }
                }
            }
        }
        return clusterList;
    }
}
