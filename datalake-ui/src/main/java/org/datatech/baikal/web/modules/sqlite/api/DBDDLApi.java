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

package org.datatech.baikal.web.modules.sqlite.api;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.modules.sqlite.service.DBDDLService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ddl接口api
 */
@Controller
@RequestMapping("/ddl")
public class DBDDLApi {

    @Resource
    private DBDDLService dbddlService;

    /**
     * 创建数据库表 instance/schema
     *
     * @param tenantName 租户名称
     * @return AjaxResponse对象
     */
    @RequestMapping("/create")
    @ResponseBody
    public AjaxResponse creatTable(String tenantName) {
        try {
            dbddlService.createMonitorDB(tenantName);
            dbddlService.createEventDB(tenantName);
            return AjaxResponse.success("创建成功", "");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("创建失败", e);
        }
    }
}
