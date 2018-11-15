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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

/**
 * 深度对象克隆，支持泛型
 */
public class ClonerUtil {
    /**
     * 深度克隆 必须保证克隆的对象继承序列化接口
     * 
     * @param t
     *            克隆对象
     *
     * @param <T> 泛型
     * @return 克隆后的对象
     * @throws IOException IOException
     * @throws OptionalDataException OptionalDataException
     * @throws ClassNotFoundException 类没有找到
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCloner(T t) throws IOException, OptionalDataException, ClassNotFoundException {// 将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(t);// 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (T) (oi.readObject());
    }
}
