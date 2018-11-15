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

import org.datatech.baikal.web.entity.bo.MonitorSchemaBO;
import org.datatech.baikal.web.vo.MonitorSchemaVO;

/**
 * <p>
 * 数据库数据监控
 * <p>
 */
public interface MonitorSchemaService {

    int save(MonitorSchemaBO monitorSchemaDO) throws Exception;

    int saveBatch(List<MonitorSchemaBO> list) throws Exception;

    List<MonitorSchemaVO> queryAllByFilter(String tenantName, String startRowkey, String endRowkey) throws Exception;

    List<MonitorSchemaVO> queryAllByFilterYesterday(String tenantName, String startRowkey, String endRowkey)
            throws Exception;
}
