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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhcacheUtils {
    private static EhcacheEnt ent = new EhcacheEnt();

    public static <T> T getMonitorSchemaCache(String key) {
        return ent.getMonitorSchemaCache(key);
    }

    public static void putMonitorSchemaCache(String key, Object obj) {
        ent.putMonitorSchemaCache(key, obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDbCache(String key) {
        return ent.getDbCache(key);
    }

    public static void putDbCache(String key, Object obj) {
        ent.putDbCache(key, obj);
    }

    public static <T> T getTableCountCache(String key) {
        return ent.getTableCountCache(key);
    }

    public static void putTableCountCache(String key, Object obj) {
        ent.putTableCountCache(key, obj);
    }

    //最后输错密码时间
    //失败次数缓存
    public static <T> T getLastLoginFailureTime(String key) {
        return ent.getLastErrorTime(key);
    }

    public static void putLastLoginFailureTime(String key, long value) {
        ent.putLastErrorTime(key, value);
    }

    //失败次数缓存
    public static <T> T getFailureNumberCache(String key) {
        return ent.getLoginErrorNumberCache(key);
    }

    public static void putFailureNumberCache(String key, int value) {
        ent.putLoginErrorNumberCache(key, value);
    }

    //锁定标志缓存
    public static <T> T getLockFlag(String key) {
        return ent.getLockFlagCache(key);
    }

    public static void putLockFlag(String key, boolean value) {
        ent.putLockFlagCache(key, value);
    }

    //锁定时间缓存
    public static <T> T getLockTime(String key) {
        return ent.getLockTimeCache(key);
    }

    public static void putLockTime(String key, Object obj) {
        ent.putLockTimeCache(key, obj);
    }

    public static void cleanTableCount() {
        ent.cleanTableCount();
    }

    public static void cleanTableCountSync() {
        ent.cleanTableCountSync();
    }

    public static <T> T getTableCountSyncCache(String key) {
        return ent.getTableCountSyncCache(key);
    }

    public static void putTableCountSyncCache(String key, Object obj) {
        ent.putTableCountSyncCache(key, obj);
    }

    public static <T> T getMonitorTbaleCache(String key) {
        return ent.getMonitorTbaleCache(key);
    }

    public static void putMonitorTbaleCache(String key, Object obj) {
        ent.putMonitorTbaleCache(key, obj);
    }

    public static void removeCountCatch(String key) {
        ent.removeCountCatch(key);
    }

    public static int getLockTimeCacheTimeToLive(String key) {
        return ent.getLockTimeCacheTimeToLive(key);
    }
}

class EhcacheEnt {
    private CacheManager cacheManager = new CacheManager();
    private Cache dbCache = cacheManager.getCache("db-cache");
    private Cache monitorTbaleCache = cacheManager.getCache("monitor-table-cache");
    private Cache monitorSchemaCache = cacheManager.getCache("monitor-schema-cache");
    private Cache tableCount = cacheManager.getCache("table-count-cache");
    private Cache tableCountSync = cacheManager.getCache("table-count-cache-sync");
    private Cache loginFailNumberCache = cacheManager.getCache("login_fail_number");
    private Cache lockFlagCache = cacheManager.getCache("lock_flag");
    private Cache lockTimeCache = cacheManager.getCache("lock_time");
    private Cache lastErrorTime = cacheManager.getCache("last_error_time");

    @SuppressWarnings("unchecked")
    public <T> T getLastErrorTime(String key) {
        final Element element = lastErrorTime.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public int getLockTimeCacheTimeToLive(String key) {
        return lockTimeCache.get(key).getTimeToLive();
    }

    public void putLastErrorTime(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        lastErrorTime.put(putGreeting);
    }

    public <T> T getLoginErrorNumberCache(String key) {
        final Element element = loginFailNumberCache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putLoginErrorNumberCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        loginFailNumberCache.put(putGreeting);
    }

    public <T> T getLockFlagCache(String key) {
        final Element element = lockFlagCache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putLockFlagCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        lockFlagCache.put(putGreeting);
    }

    public <T> T getLockTimeCache(String key) {
        final Element element = lockTimeCache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putLockTimeCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        lockTimeCache.put(putGreeting);
    }

    public <T> T getMonitorSchemaCache(String key) {
        final Element element = monitorSchemaCache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putMonitorSchemaCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        monitorSchemaCache.put(putGreeting);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDbCache(String key) {
        final Element element = dbCache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putDbCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        dbCache.put(putGreeting);
    }

    public synchronized <T> T getTableCountCacheEr(String key) {
        final Element element = tableCount.get(key);
        if (element == null) {
            putTableCountCache(key, -1);
        }
        return element == null ? null : (T) element.getObjectValue();
    }

    public <T> T getTableCountCache(String key) {
        final Element element = tableCount.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putTableCountCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        tableCount.put(putGreeting);
    }

    public void cleanTableCount() {
        tableCount.removeAll();
    }

    public void cleanTableCountSync() {
        tableCountSync.removeAll();
    }

    public <T> T getTableCountSyncCache(String key) {
        final Element element = tableCountSync.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putTableCountSyncCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        tableCountSync.put(putGreeting);
    }

    public <T> T getMonitorTbaleCache(String key) {
        final Element element = monitorTbaleCache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    public void putMonitorTbaleCache(String key, Object obj) {
        final Element putGreeting = new Element(key, obj);
        monitorTbaleCache.put(putGreeting);
    }

    public void removeCountCatch(String key) {
        tableCount.remove(key);
    }

}
