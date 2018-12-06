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

import org.datatech.baikal.web.entity.bo.OperationLogBO;
import org.datatech.baikal.web.external.PageModel;
import org.datatech.baikal.web.vo.OperationLogVO;

/**
 * 操作日志接口类
 */
public interface OperationLogService {

    int save(OperationLogBO operationLogBO) throws Exception;

    int save(String operationType, String message) throws Exception;

    List<OperationLogVO> getList(OperationLogVO operationLogVO, PageModel hpm) throws Exception;
}
