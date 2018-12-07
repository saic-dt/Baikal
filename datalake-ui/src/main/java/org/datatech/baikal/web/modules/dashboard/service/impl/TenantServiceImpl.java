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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.common.exp.BizException;
import org.datatech.baikal.web.entity.User;
import org.datatech.baikal.web.entity.bean.TenantBean;
import org.datatech.baikal.web.modules.dashboard.service.OperationLogService;
import org.datatech.baikal.web.modules.dashboard.service.TenantService;
import org.datatech.baikal.web.core.ZkHandler;
import org.datatech.baikal.web.utils.JsonIterUtil;
import org.datatech.baikal.web.utils.JsonUtil;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.datatech.baikal.web.utils.StringUtil;
import org.datatech.baikal.web.utils.security.Md5Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

/**
 */
@Service
public class TenantServiceImpl implements TenantService {
    @Resource
    private ZkHandler handler;
    @Resource
    private OperationLogService operationLogService;
    @Value("${datalake-ui.regex}")
    private String regex;

    public static boolean checkPassword(String regex, String userName, String password) throws IOException {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches() && !password.contains(userName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 租户列表
     * @return 租户列表
     */
    @Override
    public List<TenantBean> tenantList() throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_TENANT;
        List<String> tenants = client.getChildren().forPath(path);
        List<TenantBean> tenantList = new ArrayList<>();
        for (String tenant : tenants) {
            String content = new String(client.getData().forPath(path + "/" + tenant));
            if (StringUtil.isNotEmpty(content)) {
                JSONObject data = JSONObject.fromObject(content);
                TenantBean entity = new TenantBean();
                entity.setTenantName(tenant);
                entity.setDesCription((String) data.get("DESCRIPTION"));
                tenantList.add(entity);
            }
        }
        return tenantList;
    }

    /**
     * 租户信息
     *
     * @param tenantName 租户名
     * @return 租户实体
     */
    @Override
    public TenantBean tenantInfo(String tenantName) throws Exception {
        CuratorFramework client = handler.getClient();
        String tenantPath = Config.ZK_NODE_TENANT + "/" + tenantName;
        List<String> tenants = client.getChildren().forPath(tenantPath);
        List<JSONObject> userList = new ArrayList<JSONObject>();
        for (String tenant : tenants) {
            List<String> users = client.getChildren().forPath(tenantPath + "/" + tenant + "/users");
            for (String user : users) {
                JSONObject json = new JSONObject();
                json.element("username", user);
                json.element("tenantName", tenant);
                userList.add(json);
            }
        }
        return null;
    }

    @Override
    public List<JSONObject> userList() throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_USER;
        List<String> users = client.getChildren().forPath(path);
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (String user : users) {
            String data = new String(client.getData().forPath(path + "/" + user));
            JSONObject content = JsonIterUtil.json2Object(data, JSONObject.class);
            JSONObject json = new JSONObject();
            json.element("username", user);
            json.element("password", (String) content.get("PASSWORD"));
            json.element("tenantName", (String) content.get("TENANT_NAME"));
            json.element("rowKey", user);
            list.add(json);
        }
        return list;
    }

    /**
     * 租户创建
     * @param tenantName 租户名
     * @param desCription 描述
     */
    @Override
    public void tenantCreate(String tenantName, String desCription) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_TENANT;
        Stat check = client.checkExists().forPath(path + "/" + tenantName);
        if (null != check) {
            throw new Exception("该租户已经存在!");
        }
        client.create().creatingParentsIfNeeded().forPath(path + "/" + tenantName);
        JSONObject json = new JSONObject();

        if (null == desCription) {
            desCription = "";
        }
        json.element("DESCRIPTION", desCription);
        client.setData().forPath(path + "/" + tenantName, json.toString().getBytes());

        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】新增了租户【" + tenantName + "】描述：" + desCription;
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_CREATE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addUser(User user) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_USER + "/" + user.getUsername();
        Stat check = client.checkExists().forPath(path);
        String userName = user.getUsername();
        BASE64Decoder decoder = new BASE64Decoder();
        String password = new String(decoder.decodeBuffer(user.getPassword()));
        String combine = userName + password;
        ArrayList<String> list = new ArrayList<>();
        long addUserTime = System.currentTimeMillis();
        if (null == check) {
            if (checkPassword(regex, userName, password)) {
                JSONObject json = new JSONObject();
                user.setPassword(Md5Utils.hash(Md5Utils.hash(Md5Utils.hash(combine))));
                list.add(user.getPassword());
                client.create().forPath(path);
                user.setLastMotifyPasswordTime(addUserTime);
                json.element("PASSWORD", user.getPassword());
                json.element("TENANT_NAME", user.getTenantName());
                json.element("UPDATE_PASSWORD", list);
                json.element("LAST_MOTIFY_TIME", addUserTime);
                client.setData().forPath(path, json.toString().getBytes());
            } else {
                BizException.throwWhenFalse(false, "请输入正确密码,密码必须包含大小写字母，数字,长度至少10位且不能包含用户名");
            }
        } else {
            String content = new String(client.getData().forPath(path));
            JSONObject json = JSONObject.fromObject(content);
            String passwd = (String) json.get("PASSWORD");
            String tenantName = (String) json.get("TENANT_NAME");
            String[] oldPassword = JsonUtil
                    .getStringArray4Json(JSONObject.fromObject(content).get("UPDATE_PASSWORD").toString());
            ArrayList<String> passWordGroup = new ArrayList<>(Arrays.asList(oldPassword));
            long lastMotifyTime = (long) json.get("LAST_MOTIFY_TIME");
            String deCodePassword = Md5Utils.hash(Md5Utils.hash(Md5Utils.hash(combine)));
            if (StringUtil.isNull(password) && !tenantName.equals(user.getTenantName())) {
                json.element("PASSWORD", passwd);
                json.element("TENANT_NAME", user.getTenantName());
                json.element("UPDATE_PASSWORD", passWordGroup);
                json.element("LAST_MOTIFY_TIME", lastMotifyTime);
                client.setData().forPath(path, json.toString().getBytes());
            }
            if (StringUtil.isNotEmpty(password)) {
                System.out.println(user.getPassword());
                System.out.println(user.getTenantName());
                if (checkPassword(regex, userName, password)) {
                    if (!passWordGroup.contains(deCodePassword)) {
                        if (passWordGroup.size() < Config.PASSWORD_GROUP_SIZE) {
                            passWordGroup.add(deCodePassword);
                            json.element("PASSWORD", deCodePassword);
                            json.element("TENANT_NAME", user.getTenantName());
                            System.out.println(user.getTenantName());
                            json.element("UPDATE_PASSWORD", passWordGroup);
                            json.element("LAST_MOTIFY_TIME", addUserTime);
                        } else {
                            json.element("PASSWORD", deCodePassword);
                            json.element("TENANT_NAME", user.getTenantName());
                            passWordGroup.remove(0);
                            passWordGroup.add(deCodePassword);
                            json.element("UPDATE_PASSWORD", passWordGroup);
                            json.element("LAST_MOTIFY_TIME", addUserTime);
                        }
                    } else {
                        BizException.throwWhenFalse(false, "新密码不能与前六次密码相同");
                    }
                    client.setData().forPath(path, json.toString().getBytes());
                } else {
                    BizException.throwWhenFalse(false, "请输入正确密码,密码必须包含大小写字母，数字,长度至少10位且不能包含用户名");
                }
            } else {
                if (StringUtil.isNotEmpty(content)) {
                    JSONObject obj = (JSONObject) JsonUtil.getObject4JsonString(content, JSONObject.class);
                    user.setPassword(obj.getString("PASSWORD"));
                }
            }
        }
        try {
            String operationType = "";
            if (check == null) {
                operationType = Enums.OperationLogType.OPERATIONLOGTYPE_CREATE.value();
            } else {
                operationType = Enums.OperationLogType.OPERATIONLOGTYPE_UPDATE.value();
            }
            String message = "用户【" + SecurityUtils.getUserName() + "】" + operationType + "系统用户【" + user.getUsername()
                    + "】对应的租户：【" + user.getTenantName() + "】";
            operationLogService.save(operationType, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public JSONObject getUser(String rowkey) throws Exception {
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_USER + "/" + rowkey;
        JSONObject user = new JSONObject();
        Stat stat = client.checkExists().forPath(path);
        if (null == stat) {
            user.element("username", "");
            return user;
        }
        String content = new String(client.getData().forPath(path));
        JSONObject data = JsonIterUtil.json2Object(content, JSONObject.class);
        user.element("rowKey", rowkey);
        user.element("username", rowkey);
        user.element("password", (String) data.get("PASSWORD"));
        user.element("tenantName", (String) data.get("TENANT_NAME"));
        return user;
    }

    @Override
    public void deleteUser(String[] rowKey) throws Exception {
        CuratorFramework client = handler.getClient();
        StringBuffer sb = new StringBuffer();
        for (String row : rowKey) {
            String path = Config.ZK_NODE_USER + "/" + row;
            System.out.println("path=" + path);
            client.delete().guaranteed().forPath(path);
            sb.append(row).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        //加入操作日志
        try {
            String message = "用户【" + SecurityUtils.getUserName() + "】删除了系统用户【" + sb.toString() + "】";
            operationLogService.save(Enums.OperationLogType.OPERATIONLOGTYPE_DELETE.value(), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
