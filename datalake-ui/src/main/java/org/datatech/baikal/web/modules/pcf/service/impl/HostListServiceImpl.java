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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.datatech.baikal.web.entity.model.HostListModel;
import org.datatech.baikal.web.core.Constants;
import org.datatech.baikal.web.modules.pcf.service.HostListService;
import org.datatech.baikal.web.utils.HostListUtil;
import org.datatech.baikal.web.utils.JsonUtil;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class HostListServiceImpl implements HostListService {
    @Override
    public HostListModel gethostList() {
        if (HostListUtil.HOST_LIST == null) {
            StringBuffer sbf = new StringBuffer();
            BufferedReader brname;
            try {
                File file = new File(Constants.HOST_SETTINGS_PATH);
                if (!file.exists()) {
                    return new HostListModel();
                }
                brname = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                // 读取NAMEID对应值
                String sname;
                while ((sname = brname.readLine()) != null) {
                    sbf.append(sname);
                }
                brname.close();
                HostListUtil.HOST_LIST = JsonUtil.jsonToPojo(sbf.toString(), HostListModel.class);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return HostListUtil.HOST_LIST;
    }
}
