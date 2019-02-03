/**
 * Copyright [2019] [choodon-excel of copyright https://github.com/choodon account owner]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */
package com.choodon.tool.excel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MapCounter
 *
 * @author michael
 * @since 2019-01-08
 */
public class MapCounter {
    private static final Map<String, AtomicLong> COUNTER = new ConcurrentHashMap<>();

    private MapCounter() {
    }

    public static final Long get(String key) {
        if (COUNTER.containsKey(key)) {
            AtomicLong atomicLong = COUNTER.get(key);
            Long value = atomicLong.incrementAndGet();
            if (value < 0) {
                synchronized (atomicLong) {
                    value = atomicLong.get();
                    if (value < 0) {
                        atomicLong.set(0);
                        return 0L;
                    } else {
                        return atomicLong.incrementAndGet();
                    }
                }

            }
            return value;
        } else {
            synchronized (MapCounter.class) {
                if (COUNTER.containsKey(key)) {
                    return COUNTER.get(key).incrementAndGet();
                } else {
                    COUNTER.put(key, new AtomicLong());
                    return 0L;
                }
            }
        }
    }

    public static final void remove(String key) {
        COUNTER.remove(key);
    }
}
