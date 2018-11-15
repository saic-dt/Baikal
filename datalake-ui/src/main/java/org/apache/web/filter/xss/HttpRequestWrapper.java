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

package org.apache.web.filter.xss;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class HttpRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String> xssMap;

    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public HttpRequestWrapper(HttpServletRequest request, Map<String, String> xssMap) {
        super(request);
        this.xssMap = xssMap;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        // 遍历每一个参数，检查是否含有
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);

    }

    /**
     * 清除恶意的XSS脚本
     * 
     * @param value
     * @return
     */
    private String cleanXSS(String value) {
        Set<String> keySet = xssMap.keySet();
        for (String key : keySet) {
            String v = xssMap.get(key);
            value = value.replaceAll(key, v);
        }
        return value;
    }
}
