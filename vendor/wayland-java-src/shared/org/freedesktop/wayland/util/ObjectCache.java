/*
 * Copyright © 2015 Erik De Rijcke
 * Copyright © 2024 Casey Link
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.freedesktop.wayland.util;

import org.slf4j.LoggerFactory;

import java.lang.foreign.MemorySegment;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache for POJOs with a native context.
 * It maps POJOs to their native context (a native pointer) so it can consistently return the same POJO given a pointer
 * value.
 */
@SuppressWarnings("unchecked")
public class ObjectCache {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ObjectCache.class);
    private static final Map<MemorySegment, Object> MAPPED_OBJECTS = new HashMap<>();

    /**
     * Retrieve a POJO that is mapped to a native pointer. This method should be used to easily retrieve a POJO with a
     * native context. This method will only return a POJO if it was previously cached with a call to {@link #store(MemorySegment, Object)}
     *
     * @param pointer The pointer of the associated object.
     * @param <T>     The type of the POJO to cast.
     * @return The cached object.
     */
    public static <T> T from(final MemorySegment pointer) {
        if (MemorySegment.NULL.equals(pointer))
            return null;
        return (T) MAPPED_OBJECTS.get(pointer);
    }

    /**
     * Maps a native pointer to a POJO. This method should be used to easily store a POJO with a native context.
     *
     * @param pointer The pointer of the associated object.
     * @param object  The object to cache.
     */
    public static void store(final MemorySegment pointer,
                             final Object object) {
        if (MemorySegment.NULL.equals(pointer)) {
            LOG.warn("Adding NULL MemorySegment to ObjectCache s={}", pointer);
            return;
        }
        final Object oldValue = MAPPED_OBJECTS.put(pointer, object);
        if (oldValue != null) {
            //put it back!
            MAPPED_OBJECTS.put(pointer, oldValue);
            throw new IllegalStateException(String.format("Can not re-map existing pointer. MemorySegment=%s, old value=%s, new value=%s",
                    pointer,
                    oldValue,
                    object));
        }
    }

    /**
     * Remove a mapped POJO. This method should be used when the native context of the POJO is no longer valid.
     *
     * @param pointer The pointer of the associated object.
     */
    public static <T> T remove(final MemorySegment pointer) {
        return (T) MAPPED_OBJECTS.remove(pointer);
    }
}

