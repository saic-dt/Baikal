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

package org.datatech.baikal.web.modules.pcf.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.entity.Menu;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.web.modules.dashboard.service.SourceJdbcService;
import org.datatech.baikal.web.modules.pcf.service.MenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单实现类
 * <p>
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private SourceJdbcService sourceJdbcService;

    @Override
    public List<Menu> get() throws Exception {
        List<Menu> set = new ArrayList<>();
        List<SourceJdbcBO> list = sourceJdbcService.listInstanceSchema();
        Menu menu = null;
        for (SourceJdbcBO jdbcEntity : list) {
            menu = new Menu();
            menu.setName(jdbcEntity.getINSTANCE_NAME() + Config.BACKSLASH + jdbcEntity.getSCHEMA_NAME());
            menu.setSchema(jdbcEntity.getSCHEMA_NAME());
            menu.setInstance(jdbcEntity.getINSTANCE_NAME());
            set.add(menu);
        }
        return set;
    }
}
