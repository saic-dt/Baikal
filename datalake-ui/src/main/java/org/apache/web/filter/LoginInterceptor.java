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

package org.apache.web.filter;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datatech.baikal.web.common.conf.Enums;
import org.datatech.baikal.web.common.validate.AjaxResponse;
import org.datatech.baikal.web.utils.JsonUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Log log = LogFactory.getLog(LoginInterceptor.class);

    /**
     * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链 如果返回true
     * 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
     * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
     *
     * @param request HttpServletRequest对象请求
     * @param response HttpServletResponse对象响应
     * @param handler 对象handler
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestURI = request.getRequestURI();
        log.debug("url:" + requestURI);
        Map<String, String[]> print = request.getParameterMap();
        log.debug("开始打印传入的参数");
        for (Object key : print.keySet()) {
            if (print.keySet() != null && !"".equals(print.keySet())) {
                for (String str : print.get(key)) {
                    log.debug(key + ":" + str);
                }
            } else {
                log.debug(key + ":");
            }
        }
        log.debug("打印参数结束");

        if (request.getSession().getAttribute("user") == null) {
            log.info("--------------------Interceptor：跳转到login页面！-----------------");
            if (request.getHeader("x-requested-with") != null
                    && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))) {
                onAccessFailErrorUsernameOrPassword(response);
                return true;
            } else {
                response.sendRedirect("http://" + request.getServerName() + ":" + request.getServerPort()
                        + request.getContextPath() + "/login.html");
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 登录已过期,请重新登录系统
     *
     * @param response HttpServletResponse响应对象
     * @return boolean
     */
    public boolean onAccessFailErrorUsernameOrPassword(HttpServletResponse response) {

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = null;
        try {
            String jsonString = JsonUtil
                    .objectToJson(AjaxResponse.fail("登录已过期,请重新登录系统", Enums.ResultCode.RESULTCODE_LOGIN_DUE.value()));
            out = response.getWriter();
            out.println(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return false;
    }

    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作 可在modelAndView中加入数据，比如当前时间
     *
     * @param request HttpServletRequest请求
     * @param response HttpServletResponse响应请求
     * @param handler 对象
     * @param modelAndView ModelAndView对象
     * @throws Exception 异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
     *
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
     *
     * @param request HttpServletRequest请求
     * @param response HttpServletResponse响应请求
     * @param handler 对象
     * @param ex 异常
     * @throws Exception 异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}