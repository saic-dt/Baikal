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

package org.datatech.baikal.web.utils;

import javax.servlet.http.HttpServletRequest;

import org.datatech.baikal.web.entity.User;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 获取登录用户名和组合
 */
public class SecurityUtils {

    private SecurityUtils() {

    }

    /**
     * 取得租户 字符串
     * 
     * @return 租户名称
     */
    public static String getTenantName() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        final String vlu = ((User) request.getSession().getAttribute("user")).getTenantName();
        return vlu;
    }

    /**
     * 取得租户 字符串
     * 
     * @return 用户名称
     */
    public static String getUserName() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        final String vlu = ((User) request.getSession().getAttribute("user")).getUsername();
        return vlu;
    }
}
