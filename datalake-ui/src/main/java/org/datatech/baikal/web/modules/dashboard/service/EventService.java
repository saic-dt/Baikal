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

package org.datatech.baikal.web.modules.dashboard.service;

import java.util.List;
import java.util.Map;

import org.datatech.baikal.web.entity.bo.EventBO;
import org.datatech.baikal.web.vo.EventVO;
import org.datatech.baikal.web.vo.SourceDataVO;

/**
 * <p>
 * 事件接口类
 * <p>
 */
public interface EventService {

    int save(EventBO eventDO) throws Exception;

    List<EventVO> getListByRowKey(Long startRowkey, Long endRowkey) throws Exception;

    List<EventVO> getListByRowKeyFileter(String startRowkey, String endRowkey, SourceDataVO sd, String tenantName)
            throws Exception;

    List<EventVO> queryAllByFilter(String startRowkey, String endRowkey, Map<String, String> map, String tenantName)
            throws Exception;

    long queryAllByFilterCount(String startRowkey, String endRowkey, Map<String, String> map, String tenantName)
            throws Exception;

    int saveBatch(List<EventBO> list) throws Exception;
}
