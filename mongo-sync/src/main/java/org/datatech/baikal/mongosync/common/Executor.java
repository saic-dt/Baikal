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

package org.datatech.baikal.mongosync.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread pool executor for job management.
 */
public class Executor {
    public static ThreadPoolExecutor FIXED_THREAD_POOL;
    public static ConcurrentHashMap<String, Thread> FIXED_THREAD_POOL_THREADS = new ConcurrentHashMap<>();

    static {
        ThreadPoolExecutor.DiscardPolicy policy = new ThreadPoolExecutor.DiscardPolicy();
        FIXED_THREAD_POOL = new ThreadPoolExecutor(Constants.MAX_POOL_SIZE, Constants.MAX_POOL_SIZE, 60,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3), policy);

    }

}
