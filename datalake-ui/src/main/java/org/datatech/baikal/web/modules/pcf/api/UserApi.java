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

package org.datatech.baikal.web.modules.pcf.api;

import java.util.List;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.entity.User;
import org.datatech.baikal.web.modules.dashboard.service.impl.TenantServiceImpl;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;

/**
 * 数据源管理页面接口
 */
@Controller
@RequestMapping("/user")
public class UserApi {

    @Resource
    private TenantServiceImpl tenantService;

    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResponse deleteUser(String[] rowKey) {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        try {
            tenantService.deleteUser(rowKey);
            return AjaxResponse.success("删除成功", true);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("删除失败" + e.getMessage());
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public AjaxResponse userList() {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        List<JSONObject> userList;
        try {
            userList = tenantService.userList();
            return AjaxResponse.success("success", userList);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("failure", e);
        }
    }

    @RequestMapping("/add")
    @ResponseBody
    public AjaxResponse addUser(User user) {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        try {
            tenantService.addUser(user);
            return AjaxResponse.success("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(e.getMessage(), e);
        }
    }

    @RequestMapping("/get")
    @ResponseBody
    public AjaxResponse getUser(String rowkey) {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        try {
            return AjaxResponse.success("OK", tenantService.getUser(rowkey));
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("NOT OK", e.getMessage());
        }
    }
}
