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
import java.util.Set;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.modules.dashboard.service.DashboardService;
import org.datatech.baikal.web.modules.dashboard.service.LinkMonitorService;
import org.datatech.baikal.web.modules.external.PageModel;
import org.datatech.baikal.web.vo.EventVO;
import org.datatech.baikal.web.vo.MonitorTableVO;
import org.datatech.baikal.web.vo.SourceDataFilterVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;

/**
 * 首页 同步 链路 dashboard展示
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardApi {

    /**
     *
     */
    @Resource
    private DashboardService dashboardService;
    @Resource
    private LinkMonitorService linkMonitorService;

    /**
     * 获取首页Dashboard下方列表 instance/schema
     *
     * @return AjaxResponse对象
     */
    @RequestMapping("/getSchemaList")
    @ResponseBody
    public AjaxResponse getSchemaList() {
        try {
            List<JSONObject> list = dashboardService.getSchemaList();
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 获取事件列表信息 包括所有schema/instance
     *
     * @return AjaxResponse 返回对象
     */
    @RequestMapping("/getSchemaEventList")
    @ResponseBody
    public AjaxResponse getSchemaEventList() {
        try {
            List<EventVO> list = dashboardService.getSchemaEventList();
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 获取已同步未同步列表
     *
     * @param sdf source_schema; source_instance; keyword;
     * @return AjaxResponse 对象
     */
    @RequestMapping("/getSchemaTableList")
    @ResponseBody
    public AjaxResponse getSchemaTableList(SourceDataFilterVO sdf) {
        try {
            List<JSONObject> list = dashboardService.getSchemaTableList(sdf);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 获取已同步未同步列表
     *
     * @param sdf source_schema; source_instance; keyword;
     * @return AjaxResponse对象
     */
    @RequestMapping("/getSchemaSyncTableList")
    @ResponseBody
    public AjaxResponse getSchemaSyncTableList(SourceDataFilterVO sdf) {
        try {
            List<JSONObject> list = dashboardService.getSchemaSyncTableList(sdf);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/startSync")
    @ResponseBody
    public AjaxResponse startSync(SourceDataVO sourceData) {
        try {
            String rowkey = dashboardService.startSync(sourceData);
            return AjaxResponse.success("同步成功", rowkey);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("同步失败", e);
        }
    }

    @RequestMapping("/repStartSync")
    @ResponseBody
    public AjaxResponse repStartSync(SourceDataVO sourceData) {
        try {
            String rowkey = dashboardService.repStartSync(sourceData);
            return AjaxResponse.success("发送zk重新同步指令成功", rowkey);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("发送zk重新同步指令失败", e);
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResponse delete(String rowkey) {
        try {
            dashboardService.delete(rowkey);
            return AjaxResponse.success("发送zk删除成功", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("发送zk删除失败", e);
        }
    }

    @RequestMapping("/getOGGTrend")
    @ResponseBody
    public AjaxResponse getOGGTrend(String source_schema, String source_instance) {
        try {
            JSONObject jsonObject = dashboardService.getOGGTrend(source_schema, source_instance);
            return AjaxResponse.success("查询成功", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/getTableLSchemaInstance")
    @ResponseBody
    public AjaxResponse getTableLSchemaInstance(SourceDataVO sourceData) {
        try {
            Set set = dashboardService.getTableLSchemaInstance(sourceData);
            return AjaxResponse.success("查询成功", set);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/getMonitorTableListByPage")
    @ResponseBody
    public AjaxResponse getMonitorTableListByPage(SourceDataFilterVO sdf, PageModel hpm) {
        try {
            List<MonitorTableVO> list = linkMonitorService.getMonitorTableListByPage(sdf, hpm);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }
}
