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

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;

public class JsonIterUtil {

    final static String NULLOBJECT = "{}";

    public static <T> T json2Object(String json, Class<T> cl) {
        String realyJson = json;
        if (json == null || "".equals(json.replaceAll("\\s*", ""))) {
            T v = null;
            try {
                v = cl.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return v;
        }
        return JsonIterator.deserialize(realyJson, cl);

    }

    public static <T> T json2Map(String json, TypeLiteral<T> typeLiteral) {
        String realyJson = json;
        if (json == null || "".equals(json.replaceAll("\\s*", ""))) {
            realyJson = NULLOBJECT;
        }
        return JsonIterator.deserialize(realyJson, typeLiteral);

    }

    public static String objectToString(Object object) {
        return JsonStream.serialize(object);
    }
}
