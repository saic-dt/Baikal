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

import org.datatech.baikal.util.JsonUtil;
import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.entity.model.LinkMonitorStateModel;
import org.datatech.baikal.web.modules.dashboard.service.DashboardService;
import org.datatech.baikal.web.modules.dashboard.service.MonitorService;
import org.datatech.baikal.web.modules.dashboard.service.impl.LinkMonitorServiceImpl;
import org.datatech.baikal.web.vo.EventVO;
import org.datatech.baikal.web.vo.MonitorTableVO;
import org.datatech.baikal.web.vo.SourceDataFilterVO;
import org.datatech.baikal.web.vo.SourceDataVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;

/**
 * 用于页面ajax定时调度后台接口专用api
 */
@Controller
public class ScheduleApi {

    @Resource
    private DashboardService dashboardService;

    @Resource
    private LinkMonitorServiceImpl linkMonitorService;

    /**
     * 监控页面对象
     */
    @Resource
    private MonitorService monitorService;

    @RequestMapping("/dashScreen")
    @ResponseBody
    public AjaxResponse dashScreen(String particleSize) {
        try {
            JSONObject object = monitorService.getSchemaBigDashboard(toInt(particleSize));
            return AjaxResponse.success("查询成功", object);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/dashSocket")
    @ResponseBody
    public AjaxResponse dashSocket(String data, String particleSize) {
        try {
            @SuppressWarnings("unchecked")
            List<SourceDataVO> paramList = JsonUtil.getList4Json(data, SourceDataVO.class);
            List<JSONObject> list = monitorService.getSchemaSmallDashboard(paramList, toInt(particleSize));
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/eventDetail")
    @ResponseBody
    public AjaxResponse eventDetail(SourceDataVO obj) {
        try {
            List<EventVO> list = dashboardService.getSchemaEventDetailList(obj);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/event")
    @ResponseBody
    public AjaxResponse event() {
        try {
            List<EventVO> list = dashboardService.getSchemaEventList();
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 数据链接监控页面
     * <p>
     * 同步增量行数趋势
     *
     * @param sourceData 数据源信息
     *                   source_schema
     *                   source_instance
     *                   source_table
     *                   source_tables
     *                   keyword
     *                   particleSize
     *                   start = 0
     *                   rows = 20
     * @return AjaxResponse 返回对象
     */
    @RequestMapping("/getGoldGateDashboard")
    @ResponseBody
    public AjaxResponse getGoldGateDashboard(SourceDataFilterVO sourceData) {
        Integer particleSize = sourceData.getParticleSize();
        if (null == particleSize) {
            particleSize = 10;
            sourceData.setParticleSize(particleSize);
        }
        try {
            List<JSONObject> list = linkMonitorService.getGoldGateDashboard(sourceData);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 数据链接监控页面
     * <p>
     * 进程延时趋势
     *
     * @param obj source_schema 数据库
     *            source_instance 数据库实例
     *            source_table 数据库表名称
     * @return AjaxResponse对象
     */
    @RequestMapping("/goldGateProcTrend")
    @ResponseBody
    public AjaxResponse goldGateProcTrend(SourceDataFilterVO obj) {
        Integer particleSize = obj.getParticleSize();
        if (null == particleSize) {
            particleSize = 10;
            obj.setParticleSize(particleSize);
        }
        try {
            JSONObject reObj = linkMonitorService.getGoldGateProcTrendDashboard(obj);
            return AjaxResponse.success("查询成功", reObj);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 数据链接监控页面
     * <p>
     * 进程状态 goldGateProcState
     * source_schema=oa_parent
     * source_instance=oa_parent
     * particleSize=2
     *
     * @param obj source_schema 数据库
     *            source_instance 数据库实例
     *            source_table 数据库表名称
     *            particleSize 页数大小
     * @return AjaxResponse对象
     */
    @RequestMapping("/goldGateProcState")
    @ResponseBody
    public AjaxResponse goldGateProcState(SourceDataFilterVO obj) {
        try {
            LinkMonitorStateModel reObj = linkMonitorService.getLatestState(obj);
            return AjaxResponse.success("查询成功", reObj);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    /**
     * 数据链接监控页面
     * <p>
     * 流量列表
     *
     * @param obj source_schema 数据库
     *            source_instance 数据库实例
     *            source_table 数据库表名称
     *            particleSize 页数大小
     * @return AjaxResponse对象
     */
    @RequestMapping("/getMonitorTableRowsList")
    @ResponseBody
    public AjaxResponse getMonitorTableRowsList(SourceDataFilterVO obj) {
        try {
            List<MonitorTableVO> list = linkMonitorService.getMonitorTableRowsList(obj);
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/ogg")
    @ResponseBody
    public AjaxResponse ogg(SourceDataVO sourceData) {
        try {
            JSONObject reObj = dashboardService.getOGGTrendCurrentTime(sourceData);
            return AjaxResponse.success("查询成功", reObj);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/syncDataTransfer")
    @ResponseBody
    public AjaxResponse syncDataTransfer(SourceDataFilterVO obj) {
        try {
            List<JSONObject> list = dashboardService.getSyncDataTransferList(obj, toInt(obj.getParticleSize()));
            return AjaxResponse.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    @RequestMapping("/syncData")
    @ResponseBody
    public AjaxResponse syncData(String rowkey) {
        try {
            JSONObject obj = dashboardService.syncData(rowkey);
            return AjaxResponse.success("查询成功", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("查询失败", e);
        }
    }

    private Integer toInt(String particleSize) {
        if (null == particleSize || "".equals(particleSize)) {
            return 10;
        } else {
            return Integer.valueOf(particleSize);
        }
    }

    private Integer toInt(Integer particleSize) {
        if (null == particleSize) {
            return 10;
        } else {
            return Integer.valueOf(particleSize);
        }
    }
}
