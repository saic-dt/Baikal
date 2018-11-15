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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class CollectionsUtil {

    public static <T> boolean isEmpty(T[] array) {
        if (array == null) {
            return true;
        }
        return array.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        if (collection == null) {
            return true;
        }
        return collection.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.size() == 0;
    }

    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity) {
        if (capacity < 1) {
            return newArrayBlockingQueue(1);
        }
        return new ArrayBlockingQueue(capacity);
    }

    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity < 1) {
            return newArrayBlockingQueue(1, fair);
        }
        return new ArrayBlockingQueue(capacity, fair);
    }

    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity, boolean fair,
            Collection<? extends E> c) {
        if (capacity < 1) {
            return newArrayBlockingQueue(1, fair, c);
        }
        if (c == null) {
            return newArrayBlockingQueue(capacity, fair);
        }
        if (capacity < c.size()) {
            return newArrayBlockingQueue(capacity, fair);
        }
        return new ArrayBlockingQueue(capacity, fair, c);
    }

    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity, boolean fair, E[] elements) {
        if (capacity < 1) {
            return newArrayBlockingQueue(1, fair, elements);
        }
        if (isEmpty(elements)) {
            return newArrayBlockingQueue(capacity, fair);
        }
        if (capacity < elements.length) {
            return newArrayBlockingQueue(capacity, fair);
        }
        return new ArrayBlockingQueue(capacity, fair, newArrayList(elements));
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList();
    }

    public static <E> ArrayList<E> newArrayList(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newArrayList();
        }
        return new ArrayList(c);
    }

    public static <E> ArrayList<E> newArrayList(E[] elements) {
        if (isEmpty(elements)) {
            return newArrayList();
        }
        ArrayList arrayList = newArrayList(elements.length);
        for (Object element : elements) {
            arrayList.add(element);
        }
        return arrayList;
    }

    public static <E> ArrayList<E> newArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            return newArrayList(0);
        }
        return new ArrayList(initialCapacity);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap();
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            return newConcurrentHashMap(0);
        }
        return new ConcurrentHashMap(initialCapacity);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor,
            int concurrencyLevel) {
        if (initialCapacity < 0) {
            return newConcurrentHashMap(0, loadFactor, concurrencyLevel);
        }
        if ((loadFactor <= 0.0F) || (concurrencyLevel <= 0)) {
            return newConcurrentHashMap(initialCapacity);
        }
        return new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newConcurrentHashMap();
        }
        return new ConcurrentHashMap(m);
    }

    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue();
    }

    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newConcurrentLinkedQueue();
        }
        return new ConcurrentLinkedQueue(c);
    }

    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(E[] elements) {
        if (isEmpty(elements)) {
            return newConcurrentLinkedQueue();
        }
        return new ConcurrentLinkedQueue(newArrayList(elements));
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList();
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newCopyOnWriteArrayList();
        }
        return new CopyOnWriteArrayList(c);
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(E[] toCopyIn) {
        if (isEmpty(toCopyIn)) {
            return newCopyOnWriteArrayList();
        }
        return new CopyOnWriteArrayList(toCopyIn);
    }

    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet();
    }

    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newCopyOnWriteArraySet();
        }
        return new CopyOnWriteArraySet(c);
    }

    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(E[] elements) {
        if (isEmpty(elements)) {
            return newCopyOnWriteArraySet();
        }
        return new CopyOnWriteArraySet(newArrayList(elements));
    }

    public static <E extends Delayed> DelayQueue<E> newDelayQueue() {
        return new DelayQueue();
    }

    public static <E extends Delayed> DelayQueue<E> newDelayQueue(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newDelayQueue();
        }
        return new DelayQueue(c);
    }

    public static <E extends Delayed> DelayQueue<E> newDelayQueue(E[] elements) {
        if (isEmpty(elements)) {
            return newDelayQueue();
        }
        return new DelayQueue(newArrayList(elements));
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap();
    }

    public static <K, V> HashMap<K, V> newHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            return newHashMap(0);
        }
        return new HashMap(initialCapacity);
    }

    public static <K, V> HashMap<K, V> newHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            return newHashMap(0, loadFactor);
        }
        if (loadFactor <= 0.0F) {
            return newHashMap(initialCapacity);
        }
        return new HashMap(initialCapacity, loadFactor);
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newHashMap();
        }
        return new HashMap(m);
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet();
    }

    public static <E> HashSet<E> newHashSet(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newHashSet();
        }
        return new HashSet(c);
    }

    public static <E> HashSet<E> newHashSet(E[] elements) {
        if (isEmpty(elements)) {
            return newHashSet();
        }
        return new HashSet(newArrayList(elements));
    }

    public static <E> HashSet<E> newHashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            return newHashSet(0);
        }
        return new HashSet(initialCapacity);
    }

    public static <E> HashSet<E> newHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            return newHashSet(0, loadFactor);
        }
        if (loadFactor <= 0.0F) {
            return newHashSet(initialCapacity);
        }
        return new HashSet(initialCapacity, loadFactor);
    }

    public static <K, V> Hashtable<K, V> newHashtable() {
        return new Hashtable();
    }

    public static <K, V> Hashtable<K, V> newHashtable(int initialCapacity) {
        if (initialCapacity < 0) {
            return newHashtable(0);
        }
        return new Hashtable(initialCapacity);
    }

    public static <K, V> Hashtable<K, V> newHashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            return newHashtable(0, loadFactor);
        }
        if (loadFactor <= 0.0F) {
            return newHashtable(initialCapacity);
        }
        return new Hashtable(initialCapacity, loadFactor);
    }

    public static <K, V> Hashtable<K, V> newHashtable(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newHashtable();
        }
        return new Hashtable(m);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap();
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap(int expectedMaxSize) {
        if (expectedMaxSize < 0) {
            return newIdentityHashMap(0);
        }
        return new IdentityHashMap(expectedMaxSize);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newIdentityHashMap();
        }
        return new IdentityHashMap(m);
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue();
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newLinkedBlockingQueue();
        }
        return new LinkedBlockingQueue(c);
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(E[] elements) {
        if (isEmpty(elements)) {
            return newLinkedBlockingQueue();
        }
        return new LinkedBlockingQueue(newArrayList(elements));
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity) {
        if (capacity < 1) {
            return newLinkedBlockingQueue(1);
        }
        return new LinkedBlockingQueue(capacity);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            return newLinkedHashMap(0);
        }
        return new LinkedHashMap(initialCapacity);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            return newLinkedHashMap(0, loadFactor);
        }
        if (loadFactor <= 0.0F) {
            return newLinkedHashMap(initialCapacity);
        }
        return new LinkedHashMap(initialCapacity, loadFactor);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newLinkedHashMap();
        }
        return new LinkedHashMap(m);
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newLinkedHashSet();
        }
        return new LinkedHashSet(c);
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(E[] elements) {
        if (isEmpty(elements)) {
            return newLinkedHashSet();
        }
        return new LinkedHashSet(newArrayList(elements));
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            return newLinkedHashSet(0);
        }
        return new LinkedHashSet(initialCapacity);
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            return newLinkedHashSet(0, loadFactor);
        }
        if (loadFactor <= 0.0F) {
            return newLinkedHashSet(initialCapacity);
        }
        return new LinkedHashSet(initialCapacity, loadFactor);
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList();
    }

    public static <E> LinkedList<E> newLinkedList(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newLinkedList();
        }
        return new LinkedList(c);
    }

    public static <E> LinkedList<E> newLinkedList(E[] elements) {
        if (isEmpty(elements)) {
            return newLinkedList();
        }
        return new LinkedList(newArrayList(elements));
    }

    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue();
    }

    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newPriorityBlockingQueue();
        }
        return new PriorityBlockingQueue(c);
    }

    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(E[] elements) {
        if (isEmpty(elements)) {
            return newPriorityBlockingQueue();
        }
        return new PriorityBlockingQueue(newArrayList(elements));
    }

    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            return newPriorityBlockingQueue(1);
        }
        return new PriorityBlockingQueue(initialCapacity);
    }

    public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(int initialCapacity,
            Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            return newPriorityBlockingQueue(1, comparator);
        }
        return new PriorityBlockingQueue(initialCapacity, comparator);
    }

    public static <E> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue();
    }

    public static <E> PriorityQueue<E> newPriorityQueue(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newPriorityQueue();
        }
        return new PriorityQueue(c);
    }

    public static <E> PriorityQueue<E> newPriorityQueue(E[] elements) {
        if (isEmpty(elements)) {
            return newPriorityQueue();
        }
        return new PriorityQueue(newArrayList(elements));
    }

    public static <E> PriorityQueue<E> newPriorityQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            return newPriorityQueue(1);
        }
        return new PriorityQueue(initialCapacity);
    }

    public static <E> PriorityQueue<E> newPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            return newPriorityQueue(1, comparator);
        }
        return new PriorityQueue(initialCapacity, comparator);
    }

    public static <E> PriorityQueue<E> newPriorityQueue(PriorityQueue<? extends E> c) {
        if (isEmpty(c)) {
            return newPriorityQueue();
        }
        return new PriorityQueue(c);
    }

    public static <E> PriorityQueue<E> newPriorityQueue(SortedSet<? extends E> c) {
        if (isEmpty(c)) {
            return newPriorityQueue();
        }
        return new PriorityQueue(c);
    }

    public static <E> Stack<E> newStack() {
        return new Stack();
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> c) {
        return new TreeMap(c);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newTreeMap();
        }
        return new TreeMap(m);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> m) {
        if (isEmpty(m)) {
            return newTreeMap();
        }
        return new TreeMap(m);
    }

    public static <E> TreeSet<E> newTreeSet() {
        return new TreeSet();
    }

    public static <E> TreeSet<E> newTreeSet(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newTreeSet();
        }
        return new TreeSet(c);
    }

    public static <E> TreeSet<E> newTreeSet(E[] elements) {
        if (isEmpty(elements)) {
            return newTreeSet();
        }
        return new TreeSet(newArrayList(elements));
    }

    public static <E> TreeSet<E> newTreeSet(Comparator<? super E> c) {
        return new TreeSet(c);
    }

    public static <E> TreeSet<E> newTreeSet(SortedSet<E> s) {
        if (isEmpty(s)) {
            return newTreeSet();
        }
        return new TreeSet(s);
    }

    public static <E> Vector<E> newVector() {
        return new Vector();
    }

    public static <E> Vector<E> newVector(Collection<? extends E> c) {
        if (isEmpty(c)) {
            return newVector();
        }
        return new Vector(c);
    }

    public static <E> Vector<E> newVector(E[] elements) {
        if (isEmpty(elements)) {
            return newVector();
        }
        return new Vector(newArrayList(elements));
    }

    public static <E> Vector<E> newVector(int initialCapacity) {
        if (initialCapacity < 0) {
            return newVector(0);
        }
        return new Vector(initialCapacity);
    }

    public static <E> Vector<E> newVector(int initialCapacity, int capacityIncrement) {
        if (initialCapacity < 0) {
            return newVector(0, capacityIncrement);
        }
        return new Vector(initialCapacity, capacityIncrement);
    }

    public static <K, V> WeakHashMap<K, V> newWeakHashMap() {
        return new WeakHashMap();
    }

    public static <K, V> WeakHashMap<K, V> newWeakHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            return newWeakHashMap(0);
        }
        return new WeakHashMap(initialCapacity);
    }

    public static <K, V> WeakHashMap<K, V> newWeakHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            return newWeakHashMap(0, loadFactor);
        }
        if (loadFactor <= 0.0F) {
            return newWeakHashMap(initialCapacity);
        }
        return new WeakHashMap(initialCapacity, loadFactor);
    }

    public static <K, V> WeakHashMap<K, V> newWeakHashMap(Map<? extends K, ? extends V> m) {
        if (isEmpty(m)) {
            return newWeakHashMap();
        }
        return new WeakHashMap(m);
    }

    public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
        AssertUtil.isNotNull(map);
        AssertUtil.isNotNull(key);
        AssertUtil.isNotNull(value);
        Object exists = map.putIfAbsent(key, value);
        if (exists != null) {
            return (V) exists;
        }
        return value;
    }

    // public static <T> boolean equalsListElements(List<T> src, List<T> dist) {
    // return equalsListElements(src, dist, new CollectionComparator() {
    // public boolean equalsTo(T src, T target) {
    // if (src == null) {
    // return target == null;
    // }
    // return src.equals(target);
    // }
    // });
    // }

    public static <T> boolean equalsListElements(List<T> src, List<T> dist, CollectionComparator<T> comparator) {
        AssertUtil.isNotNull(src);
        AssertUtil.isNotNull(dist);
        AssertUtil.isNotNull(comparator);
        if ((isEmpty(src)) || (isEmpty(dist))) {
            return (isEmpty(src)) && (isEmpty(dist));
        }
        if (src.size() != dist.size()) {
            return false;
        }
        for (int i = 0; i < src.size(); i++) {
            if (!comparator.equalsTo(src.get(i), dist.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static abstract interface CollectionComparator<T> {
        public abstract boolean equalsTo(T paramT1, T paramT2);
    }
}