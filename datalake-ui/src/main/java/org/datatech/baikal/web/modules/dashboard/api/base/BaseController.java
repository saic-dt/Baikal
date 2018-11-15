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

package org.datatech.baikal.web.modules.dashboard.api.base;

import javax.servlet.http.HttpServletRequest;

import org.datatech.baikal.web.entity.User;
import org.datatech.baikal.web.modules.dashboard.service.impl.MonitorServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {
    /**
     * 取得租户 byte 数组
     *
     * @return
     */
    final private Logger logger = LoggerFactory.getLogger(MonitorServerImpl.class);
    @Autowired
    private HttpServletRequest requrst;

    protected byte[] getTenantNameBytes() {
        return toBytearray(((User) requrst.getSession().getAttribute("user")).getTenantName());
    }

    /**
     * 取得租户 字符串
     *
     * @return 租户名
     */
    protected String getTenantName() {
        final String vlu = ((User) requrst.getSession().getAttribute("user")).getTenantName();
        /*logger.info("tenant is "+ vlu);*/
        return vlu;
    }

    /**
     * 取得用户
     *
     * @return 租户名
     */
    protected String getUserName() {
        return ((User) requrst.getSession().getAttribute("user")).getUsername();
    }

    private byte[] toBytearray(String tenantName) {
        if (null == tenantName) {
            return new byte[0];
        }
        return tenantName.getBytes();
    }
}
