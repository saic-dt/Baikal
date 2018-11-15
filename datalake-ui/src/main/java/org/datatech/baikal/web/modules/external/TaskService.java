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
import org.datatech.baikal.web.entity.bean.TenantBean;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.modules.dashboard.service.TenantService;
import org.datatech.baikal.web.utils.TaskTool.TaskTool;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("taskJob")
public class TaskService {

    private static final Log log = LogFactory.getLog(TaskService.class);
    private final String loggerFormate = "Refresh db %s.%s  ing...";
    @Resource
    private TenantService tenantService;
    @Resource
    private SourceJdbcService sourceJdbcService;

    @Scheduled(cron = "${datalake-ui.table.refresh.count.cycle}")
    public void tableRefCount() {
        List<SourceJdbcBO> oracleList = new ArrayList<>();
        try {
            List<TenantBean> tenantList = tenantService.tenantList();
            tenantList.forEach(v -> oracleList.addAll(queryAll(v.getTenantName())));
            log.info("Refresh table count ing ...");
            for (SourceJdbcBO sourceJdbc : oracleList) {
                log.info(String.format(loggerFormate, sourceJdbc.getINSTANCE_NAME(), sourceJdbc.getSCHEMA_NAME()));
                intoConntionPool(sourceJdbc);
            }
            log.info("Refresh table count end ...");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    /**
     * 初始化数据库表记录数
     *
     * @param sourceJdbc
     */
    private void intoConntionPool(SourceJdbcBO sourceJdbc) {
        TaskTool.getDbTableCount(sourceJdbc, true);
    }

    /**
     * 根据租户获取源数据库信息
     *
     * @param tenantName 租户
     * @return
     */
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
