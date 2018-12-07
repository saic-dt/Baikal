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

package org.datatech.baikal.web.modules.dashboard.service;

import java.util.List;

import org.datatech.baikal.web.entity.bo.MonitorTableBO;
import org.datatech.baikal.web.core.PageModel;
import org.datatech.baikal.web.vo.MonitorTableVO;

import net.sf.json.JSONObject;

/**
 * <p>
 * 数据库监控表流量接口类
 * <p>
 */
public interface MonitorTableService {

    int save(MonitorTableBO monitorTableDO) throws Exception;

    int saveBatch(List<MonitorTableBO> list) throws Exception;

    List<MonitorTableBO> listAllByFilter(String startRowkey, String endRowkey, String tenantName) throws Exception;

    List<MonitorTableBO> listAllByFilter(String prefixFilter, String tenantName) throws Exception;

    List<MonitorTableVO> listPage(String prefixRowkey, String prefixEndRowkey, PageModel hpm, String tenantName,
            String whereSql) throws Exception;

    List<JSONObject> listAll() throws Exception;
}
