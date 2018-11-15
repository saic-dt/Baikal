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

import java.util.Collection;
import java.util.Map;

/**
 * 断言工具类。
 */
public class AssertUtil {

    /**
     * NULL断言
     * @param arg0 泛型参数对象
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void isNull(T arg0) throws IllegalArgumentException {
        if (arg0 != null) {
            throw new IllegalArgumentException("arg0 must be null.");
        }
    }

    /**
     * 非NULL断言。
     * 
     * @param arg0 泛型参数对象
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void isNotNull(T arg0) throws IllegalArgumentException {
        if (arg0 == null) {
            throw new IllegalArgumentException("arg0 must not be null.");
        }
    }

    /**
     * 非NULL断言。
     * 
     * @param args 泛型参数数组
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void isNotNulls(T[] args) throws IllegalArgumentException {
        isNotNull(args);
        for (Object arg : args) {
            isNotNull(arg);
        }
    }

    /**
     * 空断言。
     * 
     * @param arg0 泛型参数字符串
     * @throws IllegalArgumentException 参数不合法
     */
    public static void isEmpty(String arg0) throws IllegalArgumentException {
        if ((arg0 != null) && (arg0.length() != 0)) {
            throw new IllegalArgumentException("arg0 must be empty.");
        }
    }

    /**
     * 非空断言。
     * 
     * @param arg0 泛型参数字符串
     * @throws IllegalArgumentException 参数不合法
     */
    public static void notEmpty(String arg0) throws IllegalArgumentException {
        if ((arg0 == null) || (arg0.length() == 0)) {
            throw new IllegalArgumentException("arg0 must not be empty.");
        }
    }

    /**
     * 真断言。
     * 
     * @param arg0 泛型参数布尔
     * @throws IllegalArgumentException 参数不合法
     */
    public static void isTrue(boolean arg0) throws IllegalArgumentException {
        if (!arg0) {
            throw new IllegalArgumentException("arg0 must be TRUE.");
        }
    }

    /**
     * 假断言。
     * 
     * @param arg0 泛型参数布尔
     * @throws IllegalArgumentException 参数不合法
     */
    public static void isFalse(boolean arg0) throws IllegalArgumentException {
        if (arg0) {
            throw new IllegalArgumentException("arg must be FALSE.");
        }
    }

    /**
     * 值相等断言（区分大小写）。
     * 
     * @param arg0 泛型参数对象
     * @param arg1 泛型参数对象
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void isEquals(T arg0, T arg1) throws IllegalArgumentException {
        if (arg0 == null) {
            if (arg1 != null) {
                throw new IllegalArgumentException("arg0 must be equal to arg1.");
            }

            return;
        }
        if (!arg0.equals(arg1)) {
            throw new IllegalArgumentException("arg0 must be equal to arg1.");
        }
    }

    /**
     * 值相等断言（不区分大小写）。
     * 
     * @param arg0 泛型参数对象
     * @param arg1 泛型参数对象
     * @throws IllegalArgumentException 参数不合法
     */
    public static void isEqualsIgnoreCase(String arg0, String arg1) throws IllegalArgumentException {
        if (arg0 == null) {
            if (arg1 != null) {
                throw new IllegalArgumentException("arg0 must be equal to arg1.");
            }

            return;
        }
        if (!arg0.equalsIgnoreCase(arg1)) {
            throw new IllegalArgumentException("arg0 must be equal to arg1.");
        }
    }

    /**
     * 值不相等断言（区分大小写）。
     * 
     * @param arg0 泛型参数对象
     * @param arg1 泛型参数对象
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void isNotEquals(T arg0, T arg1) throws IllegalArgumentException {
        if (arg0 == null) {
            if (arg1 == null) {
                throw new IllegalArgumentException("arg0 must not be equal to arg1.");
            }

            return;
        }
        if (arg0.equals(arg1)) {
            throw new IllegalArgumentException("arg0 must not be equal to arg1.");
        }
    }

    /**
     * 值不相等断言（不区分大小写）。
     * 
     * @param arg0 泛型参数对象
     * @param arg1 泛型参数对象
     * @throws IllegalArgumentException 参数不合法
     */
    public static void isNotEqualsIgnoreCase(String arg0, String arg1) throws IllegalArgumentException {
        if (arg0 == null) {
            if (arg1 == null) {
                throw new IllegalArgumentException("arg0 must not be equal to arg1.");
            }

            return;
        }
        if (arg0.equalsIgnoreCase(arg1)) {
            throw new IllegalArgumentException("arg0 must not be equal to arg1.");
        }
    }

    /**
     * 为指定类的对象的断言。
     * 
     * @param arg0 泛型参数类
     * @param arg1 泛型参数对象
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void isInstanceof(Class<?> arg0, T arg1) throws IllegalArgumentException {
        if (arg0 == null) {
            throw new IllegalArgumentException("arg0 is required.");
        }
        if (arg1 == null) {
            throw new IllegalArgumentException("arg1 is required.");
        }
        if (!arg0.isInstance(arg1)) {
            throw new IllegalArgumentException("arg0 must be an arg1 class-type or an arg1 subclass-type.");
        }
    }

    /**
     * 不包含断言。
     * 
     * @param arg0 泛型参数字符串
     * @param arg1 泛型参数字符串
     * @throws IllegalArgumentException 参数不合法
     */
    public static void doesNotContain(String arg0, String arg1) throws IllegalArgumentException {
        if ((StringUtil.isNotEmpty(arg0)) && (StringUtil.isNotEmpty(arg1)) && (arg0.indexOf(arg1) != -1)) {
            throw new IllegalArgumentException("arg0 must not contain arg1.");
        }
    }

    /**
     * 集合不为NULL的断言。
     * 
     * @param elements 泛型参数数组
     * @param <T> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <T> void notAllNull(T[] elements) throws IllegalArgumentException {
        if (elements == null) {
            throw new IllegalArgumentException("neither of arguments must not be null.");
        }

        for (Object element : elements) {
            if (element == null) {
                throw new IllegalArgumentException("neither of arguments must not be null.");
            }
        }
    }

    /**
     * 集合不为空的断言。
     * 
     * @param arg0 泛型参数集合
     * @param <E> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <E> void notEmpty(Collection<E> arg0) throws IllegalArgumentException {
        if (CollectionsUtil.isEmpty(arg0)) {
            throw new IllegalArgumentException("this collection must not be empty.");
        }
    }

    /**
     * MAP不为空的断言。
     * 
     * @param map 泛型参数map集合
     * @param <K> 泛型
     * @param <V> 泛型
     * @throws IllegalArgumentException 参数不合法
     */
    public static <K, V> void notEmpty(Map<K, V> map) throws IllegalArgumentException {
        if (CollectionsUtil.isEmpty(map)) {
            throw new IllegalArgumentException("this map must not be empty.");
        }
    }

    /**
     * 子类是否属于超类的断言。
     * 
     * @param superType 泛型参数类
     * @param subType 泛型参数类
     * @throws IllegalArgumentException 参数不合法
     */
    public static void isAssignable(Class<?> superType, Class<?> subType) throws IllegalArgumentException {
        if (superType == null) {
            throw new IllegalArgumentException("superType is required.");
        }
        if ((subType == null) || (!superType.isAssignableFrom(subType))) {
            throw new IllegalArgumentException(subType + " is not assignable to " + superType);
        }
    }

    /**
     * 必须为真的断言。
     * 
     * @param expression 泛型参数布尔
     * @throws IllegalStateException 参数不合法
     */
    public static void state(boolean expression) throws IllegalStateException {
        if (!expression) {
            throw new IllegalStateException("expression must be TRUE.");
        }
    }
}