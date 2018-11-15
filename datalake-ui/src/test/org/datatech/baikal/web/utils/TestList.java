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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestList {

    @Test
    public void test(String[] args) {
        List list1 = new ArrayList();
        list1.add("1111");
        list1.add("2222");
        list1.add("3333");

        List list2 = new ArrayList();
        list2.add("3333");
        list2.add("4444");
        list2.add("5555");

        System.out.println(list1.contains("2222"));

        // 并集
        // list1.addAll(list2);
        // list1.forEach(x-> System.out.println(x));
        // 结果：[1111, 2222, 3333, 3333, 4444, 5555]

        // 交集
        // list1.retainAll(list2);
        // 结果：[3333]

        // 差集
        list1.removeAll(list2);
        list1.forEach(x -> System.out.println(x));
        // 结果：[1111, 2222]

        // 无重复并集
        // list2.removeAll(list1);
        // list1.addAll(list2);
        // 结果：[1111, 2222, 3333, 4444, 5555]
    }
}
