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

package org.datatech.baikal.web.utils.encrypt;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class CheckUtils {

    public static final String COMMON_FIELD = "flowID,initiator,";

    /**
     * 验证对象是否为NULL,空字符串，空数组，空的Collection或Map(只有空格的字符串也认为是空串)
     * 
     * @param obj
     *            被验证的对象
     * @param message
     *            异常信息
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof String && obj.toString().trim().length() == 0) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            throw new IllegalArgumentException(message + " must be specified");
        }
    }

}
