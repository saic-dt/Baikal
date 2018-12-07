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

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.entity.User;
import org.datatech.baikal.web.entity.model.HostListModel;
import org.datatech.baikal.web.modules.dashboard.api.base.BaseController;
import org.datatech.baikal.web.core.Constants;
import org.datatech.baikal.web.modules.pcf.service.HostListService;
import org.datatech.baikal.web.modules.pcf.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * Description
 * <p>
 */
@Controller
public class LoginApi extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private HostListService hostlistService;

    @RequestMapping("/loginByUser")
    @ResponseBody
    public AjaxResponse login(HttpServletRequest req, User user, HttpServletResponse rep) {
        try {
            // 跨域登陆
            rep.setHeader("Access-Control-Allow-Origin", "*");
            rep.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            rep.setHeader("Access-Control-Max-Age", "3600");
            rep.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            Pair<User, String> pair = userService.login(user);
            String resultCode = pair.getRight();
            //判断错误提示信息
            if (Enums.ResultCode.RESULTCODE_RESETPASSWORD.value().equals(resultCode)) {
                return AjaxResponse.fail("强制登录修改密码", resultCode);
            }
            if (Enums.ResultCode.RESULTCODE_DISABLED.value().equals(resultCode)) {
                return AjaxResponse.fail("用户已被禁止登录", resultCode);
            }
            if (Enums.ResultCode.RESULTCODE_PASSWORD_ERROR.value().equals(resultCode)) {
                return AjaxResponse.fail("密码输入有误,请重试", resultCode);
            }
            if (Enums.ResultCode.RESULTCODE_REMINDER_MESSAGE.value().equals(resultCode.split(",")[0])) {
                String[] split = resultCode.split(",");
                resultCode = split[0];
                String retryDay = split[1];
                User u = pair.getLeft();
                u.setEsurl(Constants.ES_URL);
                req.getSession().setAttribute("user", u);
                return AjaxResponse.success("距离密码到期还剩" + retryDay + "天,请及时修改密码", resultCode, u);
            }
            User u = pair.getLeft();
            u.setEsurl(Constants.ES_URL);
            req.getSession().setAttribute("user", u);
            return AjaxResponse.success("登录成功", u);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("登录失败", e);
        }
    }

    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getSession().invalidate();
            response.sendRedirect("http://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath() + "/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新密码
     * @param user 用户实体
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return AjaxResponse对象
     */

    @RequestMapping("/updatePassword")
    @ResponseBody
    public AjaxResponse updatePasswd(User user, String oldPassword, String newPassword) {
        try {
            System.out.println(user.getUsername() + "||" + oldPassword + "||" + newPassword);
            Boolean updateStatus = userService.updatePassword(user, oldPassword, newPassword);
            return AjaxResponse.success("密码更新成功", updateStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.fail("密码更新失败", e);
        }
    }

    /**
     * 取得集群地址
     * @return 获取节点列表
     */
    @RequestMapping("/hostList")
    @ResponseBody
    public HostListModel hostList() {
        return hostlistService.gethostList();
    }

}
