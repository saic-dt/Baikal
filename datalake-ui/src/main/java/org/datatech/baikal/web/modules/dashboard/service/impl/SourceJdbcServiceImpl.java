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
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.modules.external.ZkHandler;
import org.datatech.baikal.web.utils.DataBaseUtil;
import org.datatech.baikal.web.utils.JsonUtil;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.web.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class SourceJdbcServiceImpl implements SourceJdbcService {

    @Resource
    private ZkHandler zkHandler;

    @Override
    public List<SourceJdbcBO> listInstanceSchema() throws Exception {
        String tenantName = SecurityUtils.getTenantName();
        List<SourceJdbcBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/SourceJdbc";
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> instances = zkHandler.getClient().getChildren().forPath(path);
        SourceJdbcBO sourceJdbcBO = null;
        for (String instance : instances) {
            List<String> schemas = zkHandler.getClient().getChildren().forPath(path + "/" + instance);
            for (String schema : schemas) {
                sourceJdbcBO = new SourceJdbcBO();
                sourceJdbcBO.setINSTANCE_NAME(instance);
                sourceJdbcBO.setSCHEMA_NAME(schema);
                list.add(sourceJdbcBO);
            }
        }
        return list;
    }

    @Override
    public List<SourceJdbcBO> list() throws Exception {
        return list(SecurityUtils.getTenantName());
    }

    @Override
    public List<SourceJdbcBO> list(String tenantName) throws Exception {
        List<SourceJdbcBO> list = new ArrayList<>();
        String path = "/metastore/" + tenantName + "/SourceJdbc";
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        if (null == check) {
            return list;
        }
        List<String> instances = zkHandler.getClient().getChildren().forPath(path);
        SourceJdbcBO sourceJdbcBO = null;
        for (String instance : instances) {
            List<String> schemas = zkHandler.getClient().getChildren().forPath(path + "/" + instance);
            for (String schema : schemas) {
                String content = new String(
                        zkHandler.getClient().getData().forPath(path + "/" + instance + "/" + schema));
                if (StringUtil.isNotEmpty(content)) {
                    sourceJdbcBO = (SourceJdbcBO) JsonUtil.getObject4JsonString(content, SourceJdbcBO.class);
                    sourceJdbcBO.setTenantName(tenantName);
                    sourceJdbcBO.setCLASS_NAME(DataBaseUtil.DRIVE_CLASS_MAP
                            .get(sourceJdbcBO.getDB_TYPE() == null ? null : sourceJdbcBO.getDB_TYPE().toUpperCase()));
                    sourceJdbcBO.setINSTANCE_NAME(instance);
                    sourceJdbcBO.setSCHEMA_NAME(schema);
                    list.add(sourceJdbcBO);
                }
            }
        }
        return list;
    }

    @Override
    public SourceJdbcBO getSourceJdbcByInstanceSchema(String source_schema, String source_instance, String tenantName)
            throws Exception {
        String path = "/metastore/" + tenantName + "/SourceJdbc";
        Stat check = zkHandler.getClient().checkExists().forPath(path);
        SourceJdbcBO sourceJdbcBO = null;
        if (null == check) {
            return sourceJdbcBO;
        }
        String content = new String(
                zkHandler.getClient().getData().forPath(path + "/" + source_instance + "/" + source_schema));
        if (StringUtil.isNotEmpty(content)) {
            sourceJdbcBO = (SourceJdbcBO) JsonUtil.getObject4JsonString(content, SourceJdbcBO.class);
            sourceJdbcBO.setCLASS_NAME(DataBaseUtil.DRIVE_CLASS_MAP
                    .get(sourceJdbcBO.getDB_TYPE() == null ? null : sourceJdbcBO.getDB_TYPE().toUpperCase()));
            sourceJdbcBO.setINSTANCE_NAME(source_instance);
            sourceJdbcBO.setSCHEMA_NAME(source_schema);
        }
        return sourceJdbcBO;
    }
}
