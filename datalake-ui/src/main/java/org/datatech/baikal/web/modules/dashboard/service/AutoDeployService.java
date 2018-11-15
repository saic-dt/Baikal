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

import java.util.Map;

import org.datatech.baikal.web.entity.bean.ScheamMagBean;
import org.datatech.baikal.web.entity.bean.SourceDbBean;
import org.datatech.baikal.web.entity.model.ScheamMgRtDataModel;

public interface AutoDeployService {

    Map<String, SourceDbBean> getDataLink(String dbType, String tenantName) throws Exception;

    ScheamMgRtDataModel searchSourceJdbc(final String startKey, final Integer pageSize, final Integer pageFlg,
            final String tenantName) throws Exception;

    void delete(String rowkey, final String tenantName) throws Exception;

    Map<String, String> getData(String rowkey, String tableName) throws Exception;

    void save(ScheamMagBean sourceJdbc) throws Exception;
}
