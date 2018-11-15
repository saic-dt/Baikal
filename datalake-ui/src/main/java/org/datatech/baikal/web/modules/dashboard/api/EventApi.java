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

import java.util.List;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.entity.bo.EventBO;
import org.datatech.baikal.web.modules.dashboard.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 事件管理
 */
@Controller
@RequestMapping("/event")
public class EventApi {

    @Resource
    private EventService eventService;

    /**
     * 获取首页Dashboard下方列表 instance/schema
     *
     * @param eventBO 事件传输对象
     * @return AjaxResponse 对象
     */
    @RequestMapping(value = { "/save" }, method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse save(EventBO eventBO) {
        try {
            int count = eventService.save(eventBO);
            return AjaxResponse.success("保存成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("保存失败", e);
        }
    }

    /**
     * 获取首页Dashboard下方列表 instance/schema
     *
     * @param list 事件传输对象集合
     * @return AjaxResponse 对象
     */
    @RequestMapping(value = { "/saveBatch" }, method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse saveBatch(@RequestBody List<EventBO> list) {
        try {
            int count = eventService.saveBatch(list);
            return AjaxResponse.success("批量保存成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("批量保存失败", e);
        }
    }
}
