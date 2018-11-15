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

package org.datatech.baikal.web.modules.external;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.entity.bean.TenantBean;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.modules.dashboard.service.TenantService;
import org.datatech.baikal.web.utils.TaskTool.TaskTool;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动服务
 */
@Component
public class StartService implements ApplicationListener<ContextRefreshedEvent> {

    private static final Log log = LogFactory.getLog(StartService.class);

    @Resource
    private SourceJdbcService sourceJdbcService;

    @Resource
    private TenantService tenantService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        if (!Config.startIntoFlg) {

            List<SourceJdbcBO> oracleList = new ArrayList<>();
            try {
                List<TenantBean> tenantList = tenantService.tenantList();
                tenantList.forEach(v -> oracleList.addAll(queryAll(v.getTenantName())));
                log.info("start init database connection pool...");
                for (SourceJdbcBO sourceJdbc : oracleList) {
                    log.info(String.format("create pool %s.%s  ing...", sourceJdbc.getINSTANCE_NAME(),
                            sourceJdbc.getSCHEMA_NAME()));
                    intoConntionPool(sourceJdbc);
                }
                log.info("end init database connection pool");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Config.startIntoFlg = false;
    }

    private void intoConntionPool(SourceJdbcBO sourceJdbc) {
        TaskTool.getDbTableCount(sourceJdbc, true);
    }

    private List<SourceJdbcBO> queryAll(String tenantName) {
        List<SourceJdbcBO> list = new ArrayList<>();
        try {
            list = sourceJdbcService.list(tenantName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
