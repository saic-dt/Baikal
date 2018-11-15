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
import org.datatech.baikal.web.entity.Menu;
import org.datatech.baikal.web.modules.pcf.service.MenuService;
import org.datatech.baikal.web.utils.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;

/**
 * <p>
 * Description
 * <p>
 */
@Controller
@RequestMapping("/menu")
public class MenuApi {

    @Resource
    private MenuService menuService;

    @RequestMapping("/get")
    @ResponseBody
    public AjaxResponse get() {
        try {
            List<Menu> set = menuService.get();
            JSONObject obj = new JSONObject();
            obj.put("elements", set);
            obj.put("isAdmin", Config.DEFAULT_TBL_NAMESPACE.equals(SecurityUtils.getTenantName()));
            return AjaxResponse.success("获取菜单成功", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("获取菜单失败", e);
        }
    }
}
