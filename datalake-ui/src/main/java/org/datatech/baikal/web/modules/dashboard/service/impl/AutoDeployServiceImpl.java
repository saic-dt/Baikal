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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.entity.SourceJdbc;
import org.datatech.baikal.web.entity.bean.ScheamMagBean;
import org.datatech.baikal.web.entity.bean.SourceDbBean;
import org.datatech.baikal.web.entity.model.ScheamMgRtDataModel;
import org.datatech.baikal.web.entity.model.SchemaMagModel;
import org.datatech.baikal.web.modules.dashboard.service.AutoDeployService;
import org.datatech.baikal.web.external.ZkHandler;
import org.datatech.baikal.web.utils.DataBaseUtil;
import org.datatech.baikal.web.utils.JsonIterUtil;
import org.datatech.baikal.web.utils.StringUtil;
import org.springframework.stereotype.Service;

import com.jsoniter.spi.TypeLiteral;

import net.sf.json.JSONObject;

@Service
public class AutoDeployServiceImpl implements AutoDeployService {

    final String lip = ":";
    final String arrow = "->";
    @Resource
    private ZkHandler handler;

    @Override
    public Map<String, SourceDbBean> getDataLink(String dbType, String tenantName) throws Exception {
        Map<String, SourceDbBean> resutlMap = new LinkedHashMap<String, SourceDbBean>();
        List<SourceDbBean> rtList = new ArrayList<SourceDbBean>();
        String path = Config.ZK_NODE_METASTORE + "/" + tenantName + "/SourceDb";
        CuratorFramework client = handler.getClient();

        Stat exists = client.checkExists().forPath(path);
        if (null == exists) {
            throw new Exception("未能在该租户下找到链路下拉列表数据");
        }
        List<String> osips = client.getChildren().forPath(path);
        for (String osip : osips) {
            List<String> instances = client.getChildren().forPath(path + "/" + osip);

            for (String instance : instances) {
                String content = new String(client.getData().forPath(path + "/" + osip + "/" + instance));
                JSONObject data = JSONObject.fromObject(content);
                if (StringUtil.isNotEmpty(dbType) && dbType.equals(data.get("DB_TYPE"))) {
                    SourceDbBean bean = new SourceDbBean();
                    bean.setRowKey(osip + Config.DELIMITER + instance);
                    bean.setDbType((String) data.get("DB_TYPE"));
                    bean.setOsUser((String) data.get("OS_USER"));
                    bean.setBasePath((String) data.get("BASE_PATH"));
                    bean.setGgsHome((String) data.get("GGS_HOME"));
                    bean.setDbHome((String) data.get("DB_HOME"));
                    bean.setGgUser((String) data.get("GG_USER"));
                    bean.setMgrPort((String) data.get("MGR_PORT"));
                    bean.setDbLog((String) data.get("DB_LOG"));
                    bean.setDbPort((String) data.get("DB_PORT"));
                    bean.setAdminUser((String) data.get("ADMIN_USER"));
                    bean.setRmtIp((String) data.get("RMT_IP"));
                    bean.setRmtPort((String) data.get("RMT_PORT"));
                    rtList.add(bean);
                }
            }
        }

        for (SourceDbBean bean : rtList) {
            String[] rowKey = bean.getRowKey().split(Config.DELIMITER);
            String key = rowKey[0] + lip + rowKey[1] + arrow + bean.getRmtIp();
            resutlMap.put(key, bean);
        }
        return resutlMap;
    }

    @Override
    public ScheamMgRtDataModel searchSourceJdbc(final String startRowKey, final Integer pageSize, final Integer pageFlg,
            final String tenantName) throws Exception {
        List<SourceJdbc> sourceJdbcList = new ArrayList<SourceJdbc>();
        String path = Config.ZK_NODE_METASTORE + "/" + tenantName + "/SourceJdbc";
        CuratorFramework client = handler.getClient();

        Stat exists = client.checkExists().forPath(path);
        if (null == exists) {
            return new ScheamMgRtDataModel();
        }

        List<String> instances = client.getChildren().forPath(path);
        for (String instance : instances) {
            List<String> schemas = client.getChildren().forPath(path + "/" + instance);
            for (String schema : schemas) {
                String content = new String(client.getData().forPath(path + "/" + instance + "/" + schema));
                JSONObject data = JSONObject.fromObject(content);
                SourceJdbc entity = new SourceJdbc();
                entity.setRow_key(instance + Config.DELIMITER + schema);
                entity.setDb_type((String) data.get("DB_TYPE"));
                entity.setInstance_name(instance);
                entity.setJdbc_url((String) data.get("JDBC_URL"));
                entity.setOs_ip((String) data.get("OS_IP"));
                entity.setPassword((String) data.get("PASSWORD"));
                entity.setRmt_ip((String) data.get("RMT_IP"));
                entity.setSchema_name(schema);
                entity.setUser((String) data.get("USER"));
                sourceJdbcList.add(entity);
            }
        }
        List<SchemaMagModel> rtList = new ArrayList<SchemaMagModel>();
        for (SourceJdbc entity : sourceJdbcList) {
            SchemaMagModel model = new SchemaMagModel();
            model.setDbType(entity.getDb_type());
            model.setDbName(entity.getInstance_name());
            model.setSchema(entity.getSchema_name());
            model.setIp(entity.getOs_ip());
            model.setTargetCluster(entity.getRmt_ip());
            model.setRowKey(entity.getRow_key());
            rtList.add(model);
        }
        ScheamMgRtDataModel model = new ScheamMgRtDataModel();
        model.setCount(rtList.size());
        model.setList(rtList);
        return model;
    }

    @Override
    public void delete(final String rowkey, String tenantName) throws Exception {
        CuratorFramework client = handler.getClient();
        String[] keys = rowkey.split(Config.DELIMITER);
        String path = Config.ZK_NODE_METASTORE + "/" + tenantName + "/SourceJdbc/" + keys[0] + "/" + keys[1];
        Stat stat = client.checkExists().forPath(path);
        if (null == stat) {
            return;
        }
        client.delete().guaranteed().forPath(path);
    }

    @Override
    public Map<String, String> getData(final String rowkey, final String tableName) throws Exception {
        CuratorFramework client = handler.getClient();
        String[] keys = rowkey.split(Config.DELIMITER);
        String path = Config.ZK_NODE_METASTORE + "/" + tableName + "/SourceJdbc" + keys[0] + "/" + keys[1];
        String content = new String(client.getData().forPath(path));
        Map<String, String> json2Map = JsonIterUtil.json2Map(content, new TypeLiteral<Map<String, String>>() {
        });
        return json2Map;
    }

    @Override
    public void save(ScheamMagBean sourceJdbc) throws Exception {
        CuratorFramework client = handler.getClient();
        JSONObject json = new JSONObject();
        if (sourceJdbc.getDbType().indexOf(Config.LINE_3) > 0) {
            json.element("DB_TYPE", sourceJdbc.getDbType().split(Config.LINE_3)[0]);
            json.element("USE_CANAL", Boolean.TRUE);
        } else {
            json.element("DB_TYPE", sourceJdbc.getDbType());
            json.element("USE_CANAL", Boolean.FALSE);
        }
        json.element("JDBC_URL", sourceJdbc.getJdbcUrl());
        json.element("USER", sourceJdbc.getUser());
        json.element("PASSWORD", sourceJdbc.getPassword());
        json.element("OS_IP", DataBaseUtil.getDBipByJdbcUrl(sourceJdbc.getJdbcUrl()));
        json.element("RMT_IP", sourceJdbc.getRmtIp());
        // TODO: 6/29/18 mongo使用的方法 可能后期需要修改
        if (StringUtil.isNull(sourceJdbc.getDbName())) {
            sourceJdbc.setDbName(sourceJdbc.getSchema());
        }
        String path = Config.ZK_NODE_METASTORE + "/" + sourceJdbc.getTenantName() + "/SourceJdbc" + "/"
                + sourceJdbc.getDbName() + "/" + sourceJdbc.getSchema();
        Stat stat = client.checkExists().forPath(path);
        if (null == stat) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
        client.setData().forPath(path, json.toString().getBytes());
    }

}
