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

package org.datatech.baikal.web.entity.model;

import java.util.HashMap;

public class AclSelectListModel {
    private String name;
    private HashMap<String, Object> dataMap;
    /**
     * 单选标记 单选1 多选2
     */
    private String radioFlg;

    public AclSelectListModel() {
        super();
    }

    public AclSelectListModel(String name, HashMap<String, Object> dataMap, String radioFlg) {
        super();
        this.name = name;
        this.dataMap = dataMap;
        this.radioFlg = radioFlg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String getRadioFlg() {
        return radioFlg;
    }

    public void setRadioFlg(String radioFlg) {
        this.radioFlg = radioFlg;
    }
}
