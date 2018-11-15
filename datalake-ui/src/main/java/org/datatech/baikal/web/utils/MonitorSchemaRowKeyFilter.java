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

import java.util.List;
import java.util.stream.Collectors;

import org.datatech.baikal.web.common.conf.Config;
import org.datatech.baikal.web.vo.MonitorSchemaVO;

/**
 * MonitorSchema RowKey 过滤出以R开头的数据
 */
public class MonitorSchemaRowKeyFilter {
    final static String RPORC = "R";

    /**
     * 过滤
     * @param srcString rowkey字符串
     * @return 是否以R开头 true：是 false:不是
     */
    public static Boolean filter(String srcString) {
        final int siz = srcString.lastIndexOf(Config.DELIMITER) + 1;
        final String str = srcString.substring(siz, siz + 1);
        return RPORC.equals(str) ? true : false;
    }

    /**
     * 返回过滤后的集合
     * 
     * @param msList 集合
     * @return 返回已R开头的集合
     */
    public static List<MonitorSchemaVO> filterList(List<MonitorSchemaVO> msList) {
        return msList.stream().filter(v -> MonitorSchemaRowKeyFilter.filter(v.getRowKey()))
                .collect(Collectors.toList());
    }
}
