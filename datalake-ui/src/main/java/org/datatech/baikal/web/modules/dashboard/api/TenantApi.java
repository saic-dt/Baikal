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

package org.datatech.baikal.web.modules.dashboard.api;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.modules.dashboard.service.impl.TenantServiceImpl;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 多租户
 */
@Controller
@RequestMapping("/tenant")
public class TenantApi {
    @Autowired
    private TenantServiceImpl service;

    /**
     * 所有租户的清单列表
     *
     * @return AjaxResponse对象
     */
    @RequestMapping("/tenantList")
    @ResponseBody
    public AjaxResponse list() {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        try {
            return AjaxResponse.success("查询成功", service.tenantList());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e.getMessage());
        }
    }

    /**
     * @param tenantName 租户信息
     * @return AjaxResponse对象
     */
    @RequestMapping("/tenantInfo")
    @ResponseBody
    public AjaxResponse tenantInfo(String tenantName) {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        try {
            return AjaxResponse.success("查询成功", service.tenantInfo(tenantName));
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 租户创建
     *
     * @param tenantName  租户名称
     * @param desCription 描述
     * @return AjaxResponse对象
     */
    @RequestMapping("/tenantCreate")
    @ResponseBody
    public AjaxResponse tenantCreate(String tenantName, String desCription) {
        if (!Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName())) {
            return AjaxResponse.fail("你所在租户无权执行该操作");
        }
        try {
            service.tenantCreate(tenantName, desCription);
            return AjaxResponse.success("租户创建成功", tenantName);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败" + e.getMessage(), e);
        }
    }

}
