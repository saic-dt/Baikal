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

import org.datatech.baikal.web.entity.User;
import org.datatech.baikal.web.entity.bean.TenantBean;

import net.sf.json.JSONObject;

public interface TenantService {

    List<TenantBean> tenantList() throws Exception;

    TenantBean tenantInfo(String userName) throws Exception;

    void tenantCreate(String tenantName, String desCription) throws Exception;

    List<JSONObject> userList() throws Exception;

    void addUser(User user) throws Exception;

    JSONObject getUser(String rowkey) throws Exception;

    void deleteUser(String[] rowKey) throws Exception;
}
