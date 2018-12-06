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

import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.modules.dashboard.service.OperationLogService;
import org.datatech.baikal.web.external.PageModel;
import org.datatech.baikal.web.vo.OperationLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 操作日志管理
 */
@Controller
@RequestMapping("/operationLog")
public class OperationLogApi {

    @Autowired
    private OperationLogService operationLogService;

    @RequestMapping("/getList")
    @ResponseBody
    public AjaxResponse getList(OperationLogVO operationLogVO, PageModel hpm) {
        try {
            List<OperationLogVO> list = operationLogService.getList(operationLogVO, hpm);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(e);
        }
    }

    @RequestMapping("/getOperationType")
    @ResponseBody
    public AjaxResponse getOperationType() {
        try {
            return AjaxResponse.success("查询成功", Enums.OperationLogType.keys);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(e);
        }
    }

    @RequestMapping("/getDbType")
    @ResponseBody
    public AjaxResponse getDbType() {
        try {
            return AjaxResponse.success("查询成功", Enums.DbType.keys);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail(e);
        }
    }
}
