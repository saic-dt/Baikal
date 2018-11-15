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

import net.sf.json.JSONObject;

public interface UserRoleService {

    List<JSONObject> getUserList() throws Exception;

    List<JSONObject> getRoleList() throws Exception;

    String userRoleList(String rowKey) throws Exception;

    void userDelete(String... rows) throws Exception;

    void userEdit(String rowKey, String roleList) throws Exception;

    boolean userAdd(String paramRowKey, String roleList) throws Exception;

    boolean roleDeleteCheck(String[] rowKeys) throws Exception;

    void roleDelete(String... rows) throws Exception;

    boolean roleAdd(String paramRowKey, String description) throws Exception;

}
