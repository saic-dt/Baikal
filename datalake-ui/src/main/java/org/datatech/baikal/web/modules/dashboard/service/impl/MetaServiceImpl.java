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
import java.util.List;

import javax.annotation.Resource;

import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.entity.bo.MetaBO;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.MetaService;
import org.datatech.baikal.web.external.ZkHandler;
import org.datatech.baikal.web.utils.JsonIterUtil;
import org.datatech.baikal.web.utils.JsonUtil;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.web.utils.StringUtil;
import org.datatech.baikal.web.vo.SourceDataFilterVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

/**
 */
@Service
public class MetaServiceImpl implements MetaService {

    @Resource
    private ZkHandler zkHandler;

    @Override
    public List<MetaBO> listInstanceSchema() throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<MetaBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/meta";
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> instances = zkHandler.getClient().getChildren().forPath(path);
        MetaBO configBO = null;
        for (String instance : instances) {
            List<String> schemas = zkHandler.getClient().getChildren().forPath(path + "/" + instance);
            for (String schema : schemas) {
                configBO = new MetaBO();
                configBO.setSOURCE_SCHEMA(schema);
                configBO.setSOURCE_INSTANCE(instance);
                list.add(configBO);
            }
        }
        return list;
    }

    @Override
    public List<MetaBO> list() throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<MetaBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/meta";
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> instances = zkHandler.getClient().getChildren().forPath(path);
        for (String instance : instances) {
            List<String> schemas = zkHandler.getClient().getChildren().forPath(path + "/" + instance);
            for (String schema : schemas) {
                List<String> tables = zkHandler.getClient().getChildren().forPath(path + "/" + instance + "/" + schema);
                for (String table : tables) {
                    String content = new String(zkHandler.getClient().getData()
                            .forPath(path + "/" + instance + "/" + schema + "/" + table));
                    if (StringUtil.isNotEmpty(content)) {
                        MetaBO configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
                        configBO.setSOURCE_SCHEMA(schema);
                        configBO.setSOURCE_INSTANCE(instance);
                        configBO.setSOURCE_TABLE(table);
                        list.add(configBO);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public int getTableCount(SourceJdbcBO sourceJdbcBO, String tenantName) throws Exception {
        int count = 0;
        String path = "/metastore/" + tenantName + "/meta/" + sourceJdbcBO.getINSTANCE_NAME() + Config.BACKSLASH
                + sourceJdbcBO.getSCHEMA_NAME();
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return count;
        }
        List<String> tables = zkHandler.getClient().getChildren().forPath(path);
        for (String table : tables) {
            String content = new String(zkHandler.getClient().getData().forPath(path + Config.BACKSLASH + table));
            if (StringUtil.isNotEmpty(content)) {
                MetaBO configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
                if (Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value().equals(configBO.getMETA_FLAG())
                        || Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION.value().equals(configBO.getMETA_FLAG())) {
                    if (!StringUtil.isNull(configBO.getSOFAR()) && !StringUtil.isNull(configBO.getTOTAL_WORK())) {
                        if (configBO.getSOFAR().equals(configBO.getTOTAL_WORK())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public List<MetaBO> listAllByInstanceSchema(SourceDataFilterVO sd) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<MetaBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/meta/" + sd.getSource_instance() + Config.BACKSLASH
                + sd.getSource_schema();
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> tables = zkHandler.getClient().getChildren().forPath(path);
        for (String table : tables) {
            String content = new String(zkHandler.getClient().getData().forPath(path + Config.BACKSLASH + table));
            if (StringUtil.isNotEmpty(content)) {
                MetaBO configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
                if (!Enums.MetaFlag.META_FLAG_DELETE.value().equals(configBO.getMETA_FLAG())) {
                    configBO.setSOURCE_SCHEMA(sd.getSource_schema());
                    configBO.setSOURCE_INSTANCE(sd.getSource_instance());
                    configBO.setSOURCE_TABLE(table);
                    list.add(configBO);
                }
            }
        }
        return list;
    }

    @Override
    public List<JSONObject> listAllByInstanceSchemaSyncData(SourceDataFilterVO sd) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<JSONObject> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/meta/" + sd.getSource_instance() + Config.BACKSLASH
                + sd.getSource_schema();
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> tables = zkHandler.getClient().getChildren().forPath(path);
        for (String table : tables) {
            String content = new String(zkHandler.getClient().getData().forPath(path + Config.BACKSLASH + table));
            JSONObject jsonObject = JsonIterUtil.json2Object(content.toLowerCase(), JSONObject.class);
            if (jsonObject.get("meta_flag") == null) {
                continue;
            }
            if (Enums.MetaFlag.sync.contains(jsonObject.getString("meta_flag"))) {
                jsonObject.put("source_schema", sd.getSource_schema());
                jsonObject.put("source_instance", sd.getSource_instance());
                jsonObject.put("source_table", table);
                jsonObject.put("row_key",
                        String.join(Config.DELIMITER, sd.getSource_instance(), sd.getSource_schema(), table));
                list.add(jsonObject);

            }
        }
        return list;
    }

    @Override
    public MetaBO get(SourceDataVO sourceData) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        MetaBO configBO = null;
        String path = "/metastore/" + tenantName + "/meta" + "/" + sourceData.getSource_instance() + "/"
                + sourceData.getSource_schema() + "/" + sourceData.getSource_table();
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return configBO;
        }
        String content = new String(zkHandler.getClient().getData().forPath(path));
        if (StringUtil.isNotEmpty(content)) {
            configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
            configBO.setSOURCE_INSTANCE(sourceData.getSource_instance());
            configBO.setSOURCE_SCHEMA(sourceData.getSource_schema());
            configBO.setSOURCE_TABLE(sourceData.getSource_table());
        }
        return configBO;
    }

    @Override
    public void save(String data, String tenantName, SourceDataVO sourceData) throws Exception {
        String path = "/metastore/" + tenantName + "/meta/" + sourceData.getSource_instance() + "/"
                + sourceData.getSource_schema() + "/" + sourceData.getSource_table();
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (check == null) {
            zkHandler.getClient().create().creatingParentsIfNeeded().forPath(path);
        }
        zkHandler.getClient().setData().forPath(path, data.getBytes());
    }

    @Override
    public List<MetaBO> listAllByInstanceSchemaNotDelete(String instance, String schema) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<MetaBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/meta/" + instance + Config.BACKSLASH + schema;
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> tables = zkHandler.getClient().getChildren().forPath(path);
        list = new ArrayList<>(tables.size());
        for (String table : tables) {
            String content = new String(zkHandler.getClient().getData().forPath(path + Config.BACKSLASH + table));
            if (StringUtil.isNotEmpty(content)) {
                MetaBO configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
                if (!Enums.MetaFlag.META_FLAG_DELETE.value().equals(configBO.getMETA_FLAG())) {
                    configBO.setSOURCE_SCHEMA(schema);
                    configBO.setSOURCE_INSTANCE(instance);
                    configBO.setSOURCE_TABLE(table);
                    list.add(configBO);
                }
            }
        }
        return list;
    }

    @Override
    public List<MetaBO> listDeteteTables(String instance, String schema) throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<MetaBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/meta/" + instance + Config.BACKSLASH + schema;
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> tables = zkHandler.getClient().getChildren().forPath(path);
        for (String table : tables) {
            String content = new String(zkHandler.getClient().getData().forPath(path + Config.BACKSLASH + table));
            if (StringUtil.isNotEmpty(content)) {
                MetaBO configBO = (MetaBO) JsonUtil.getObject4JsonString(content, MetaBO.class);
                if (Enums.MetaFlag.META_FLAG_DELETE.value().equals(configBO.getMETA_FLAG())) {
                    configBO.setSOURCE_SCHEMA(schema);
                    configBO.setSOURCE_INSTANCE(instance);
                    configBO.setSOURCE_TABLE(table);
                    list.add(configBO);
                }
            }
        }
        return list;
    }
}
