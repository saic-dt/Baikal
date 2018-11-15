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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.modules.dashboard.service.OperationLogService;
import org.datatech.baikal.web.modules.dashboard.service.UserRoleService;
import org.datatech.baikal.web.modules.external.ZkHandler;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

@Service
public class UserRoleImpl implements UserRoleService {

    @Resource
    private ZkHandler handler;

    @Resource
    private OperationLogService operationLogService;

    /**
     * 对字符串中匹配的子串进行删除
     *
     * @param source  源字符串
     * @param element 删除的字符串
     * @return 删除后的字符串
     */
    public static String removeElement(String source, String element) {
        String[] split = source.split(",");
        if (0 == split.length) {
            return source;
        }
        StringBuilder sb = new StringBuilder();
        for (String string : split) {
            if (string.equals(element)) {
                continue;
            }
            sb.append(string);
            sb.append(",");
        }
        String s = sb.toString();
        if (0 == s.length()) {
            return s;
        }
        s = s.substring(0, s.length() - 1);
        return s;
    }

    /**
     * 获取用户列表
     *
     * @return json对象集合
     * @throws Exception 异常
     */
    @Override
    public List<JSONObject> getUserList() throws Exception {
        String path = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users";
        List<JSONObject> userList = new ArrayList<JSONObject>();
        CuratorFramework client = handler.getClient();

        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            return userList;
        }

        List<String> users = client.getChildren().forPath(path);
        for (String user : users) {
            String data = new String(client.getData().forPath(path + "/" + user));
            JSONObject content = JSONObject.fromObject(data);
            JSONObject json = new JSONObject();
            json.element("rowKey", user);
            json.element("roleList", content.get("ROLE_LIST"));
            userList.add(json);
        }
        return userList;
    }

    /**
     * 角色列表
     *
     * @return json对象集合
     * @throws Exception 异常
     */
    @Override
    public List<JSONObject> getRoleList() throws Exception {
        CuratorFramework client = handler.getClient();
        List<JSONObject> roleList = new ArrayList<JSONObject>();
        String path = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_roles";
        Stat check = client.checkExists().forPath(path);
        if (check == null) {
            return roleList;
        }
        List<String> roles = client.getChildren().forPath(path);
        for (String role : roles) {
            String data = new String(client.getData().forPath(path + "/" + role));
            JSONObject content = JSONObject.fromObject(data);
            JSONObject json = new JSONObject();
            json.element("rowKey", role);
            // json.element("userList", content.get("USER_LIST"));
            json.element("description", content.get("DESCRIPTION"));
            roleList.add(json);
        }
        return roleList;
    }

    /**
     * 用户角色
     *
     * @param rowKey 主键
     * @return 用户角色字符串
     * @throws Exception 异常
     */
    @Override
    public String userRoleList(String rowKey) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users";
        String content = new String(client.getData().forPath(path + "/" + rowKey));
        return (String) JSONObject.fromObject(content).get("ROLE_LIST");
    }

    /**
     * 批量删除用户
     *
     * @param rows 用户数组
     * @throws Exception 异常
     */
    @Override
    public void userDelete(String... rows) throws Exception {
        CuratorFramework client = handler.getClient();
        String userPath = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users";
        StringBuffer users = new StringBuffer();
        for (String user : rows) {
            client.delete().guaranteed().forPath(userPath + "/" + user);
            users.append(user).append(",");
        }
        if (users.length() > 0) {
            users.deleteCharAt(users.length() - 1);
        }
        // 加入操作日志
        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】删除了用户【" + users.toString() + "】";
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_DELETE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 编辑用户
     *
     * @param rowKey   主键
     * @param roleList 角色列表
     * @throws Exception 异常
     */
    @Override
    public void userEdit(String rowKey, String roleList) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users";
        JSONObject json = new JSONObject();
        json.element("ROLE_LIST", roleList);
        client.setData().withVersion(-1).forPath(path + "/" + rowKey, json.toString().getBytes());
        // 加入操作日志
        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】修改了用户【" + rowKey + "】选择了角色：" + roleList;
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_UPDATE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 添加用户
     *
     * @param paramRowKey 主键
     * @param roleList    角色列表
     * @return 是否添加成功
     * @throws Exception 异常
     */
    @Override
    public boolean userAdd(String paramRowKey, String roleList) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users" + "/"
                + paramRowKey;
        Stat check = client.checkExists().forPath(path);
        if (null != check) {
            return false;
        }
        JSONObject json = new JSONObject();
        if (null == roleList || "".equals(roleList)) {
            json.element("ROLE_LIST", "");
        }
        json.element("ROLE_LIST", roleList);
        client.create().creatingParentsIfNeeded().forPath(path, json.toString().getBytes());
        // 加入操作日志
        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】新增了用户【" + paramRowKey + "】选择了角色：" + roleList;
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_CREATE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * 角色删除验证
     *
     * @param rowKeys 主键数组
     * @return 是否能够删除
     * @throws Exception 异常
     */
    @Override
    public boolean roleDeleteCheck(String[] rowKeys) throws Exception {
        boolean flag = false;
        CuratorFramework client = handler.getClient();
        String userPath = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users";
        Stat check = client.checkExists().forPath(userPath);
        if (check == null) {
            return flag;
        }
        List<String> users = client.getChildren().forPath(userPath);

        Set<String> allUserRoles = new HashSet<String>();
        for (String user : users) {
            String content = new String(client.getData().forPath(userPath + "/" + user));
            JSONObject fromObject = JSONObject.fromObject(content);
            String userRoleList = (String) fromObject.get("ROLE_LIST");
            String[] split = userRoleList.split(",");
            for (String role : split) {
                allUserRoles.add(role);
            }
        }

        Set<String> selectedRoles = new HashSet<String>();
        for (String role : rowKeys) {
            selectedRoles.add(role);
        }

        selectedRoles.retainAll(allUserRoles);
        if (0 < selectedRoles.size()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 角色删除
     *
     * @param rows 角色列表
     * @throws Exception 异常
     */
    @Override
    public void roleDelete(String... rows) throws Exception {

        CuratorFramework client = handler.getClient();
        String rolePath = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_roles";
        String userPath = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_users";
        Stat check = client.checkExists().forPath(userPath);
        List<String> users = new ArrayList<>();
        if (check != null) {
            users = client.getChildren().forPath(userPath);
        }
        StringBuffer roles = new StringBuffer();
        for (String role : rows) {
            client.delete().guaranteed().forPath(rolePath + "/" + role);
            for (String user : users) {
                String content = new String(client.getData().forPath(userPath + "/" + user));
                JSONObject fromObject = JSONObject.fromObject(content);
                String roleList = (String) fromObject.get("ROLE_LIST");
                fromObject.element("ROLE_LIST", removeElement(roleList, role));
                client.setData().forPath(userPath + "/" + user, fromObject.toString().getBytes());
            }
            roles.append(role).append(",");
        }
        if (roles.length() > 0) {
            roles.deleteCharAt(roles.length() - 1);
        }

        // 加入操作日志
        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】删除了角色【" + roles + "】";
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_DELETE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 添加角色
     *
     * @param paramRowKey 主键
     * @param description 描述
     * @return 是否添加成功
     * @throws Exception 异常
     */
    @Override
    public boolean roleAdd(String paramRowKey, String description) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_TENANT + "/" + SecurityUtils.getTenantName() + "/cluster_roles" + "/"
                + paramRowKey;
        Stat check = client.checkExists().forPath(path);
        if (null != check) {
            return false;
        }
        JSONObject json = new JSONObject();
        json.element("DESCRIPTION", description);
        client.create().creatingParentsIfNeeded().forPath(path, json.toString().getBytes());

        // 加入操作日志
        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】新增角色【" + paramRowKey + "】描述：" + description;
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_CREATE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
