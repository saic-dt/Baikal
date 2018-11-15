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

/**
 *
 */
public class ListPaging {

    public static List<List<Integer>> paging(List<Integer> list, int pageSize) {
        int totalCount = list.size();
        int pageCount;
        int m = totalCount % pageSize;

        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }

        List<List<Integer>> totalList = new ArrayList<List<Integer>>();
        for (int i = 1; i <= pageCount; i++) {
            if (m == 0) {
                List<Integer> subList = list.subList((i - 1) * pageSize, pageSize * (i));
                totalList.add(subList);
            } else {
                if (i == pageCount) {
                    List<Integer> subList = list.subList((i - 1) * pageSize, totalCount);
                    totalList.add(subList);
                } else {
                    List<Integer> subList = list.subList((i - 1) * pageSize, pageSize * i);
                    totalList.add(subList);
                }
            }
        }

        return totalList;
    }

    public static List<Integer> paging(List<Integer> list, int pageSize, int start) {
        int totalCount = list.size();
        int pageCount;
        int m = totalCount % pageSize;

        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }

        List<Integer> totalList = new ArrayList();
        if (start < 0) {
            start = 0;
        }

        start = start + 1;
        if (start > pageCount) {
            // start=pageCount;
            return new ArrayList<>();
        }
        if (start <= pageCount) {
            if (m == 0) {
                totalList = list.subList((start - 1) * pageSize, pageSize * (start));
            } else {
                if (start == pageCount) {
                    totalList = list.subList((start - 1) * pageSize, totalCount);
                    // totalList = list.subList((totalCount-pageSize), totalCount);
                } else {
                    totalList = list.subList((start - 1) * pageSize, pageSize * (start));
                }
            }
        }
        return totalList;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i < 13; i++) {
            list.add(i);
        }

        List<List<Integer>> totalList = paging(list, 20);
        System.out.println(totalList);

        List<Integer> subList = paging(list, 20, 1);
        System.out.println(subList);
    }
}