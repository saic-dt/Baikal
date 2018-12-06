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

package org.datatech.baikal.web;

import org.datatech.baikal.web.filter.LoginInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册拦截器
 */
@SpringBootConfiguration
public class WebAppConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patterns = new ArrayList<>();
        patterns.add("/login.html");
        patterns.add("/loginByUser");
        patterns.add("/hostList");
        patterns.add("/images/**");
        patterns.add("/css/**");
        patterns.add("/js/**");
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }
}