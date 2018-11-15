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

import org.datatech.baikal.web.entity.bo.MetaBO;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.vo.SourceDataFilterVO;
import org.datatech.baikal.web.vo.SourceDataVO;

import net.sf.json.JSONObject;

/**
 * <p>
 * meta信息接口类
 * <p>
 */
public interface MetaService {

    List<MetaBO> listInstanceSchema() throws Exception;

    List<MetaBO> list() throws Exception;

    int getTableCount(SourceJdbcBO sourceJdbcBO, String tenantName) throws Exception;

    List<MetaBO> listAllByInstanceSchema(SourceDataFilterVO sd) throws Exception;

    List<JSONObject> listAllByInstanceSchemaSyncData(SourceDataFilterVO sd) throws Exception;

    MetaBO get(SourceDataVO sourceData) throws Exception;

    void save(String s, String tenantName, SourceDataVO sourceData) throws Exception;

    List<MetaBO> listAllByInstanceSchemaNotDelete(String source_instance, String source_schema) throws Exception;

    List<MetaBO> listDeteteTables(String source_instance, String source_schema) throws Exception;
}
