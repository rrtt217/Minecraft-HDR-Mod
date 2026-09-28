/*
 * Copyright © 2015 Erik De Rijcke
 * Copyright © 2024 Casey Link
 *
 * Licensed under the Apache License, Version 2.0 (the"License");
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
 */

package org.freedesktop.wayland.util;

import org.freedesktop.wayland.raw.C;
import org.freedesktop.wayland.raw.wl_array;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class WlArray {
    public final MemorySegment arrayPtr;

    public WlArray(MemorySegment arrayPtr) {
        this.arrayPtr = arrayPtr;
    }

    public ByteBuffer asByteBuffer() {
        return this.arrayPtr.asSlice(0, wl_array.alloc(arrayPtr)).asByteBuffer();
    }

    public <T> List<T> toList(ValueLayout valueLayout, Class<T> type) {
        return WlArray.toList(this.arrayPtr, valueLayout, type);
    }

    public static <T> List<T> toList(MemorySegment arraySegment, ValueLayout valueLayout, Class<T> type) {
        long size = wl_array.size(arraySegment);
        MemorySegment data = wl_array.data(arraySegment);

        if (size == 0 || data.address() == 0) {
            return List.of();
        }

        int elementCount = (int) (size / valueLayout.byteSize());
        var ret = new ArrayList<T>(elementCount);
        for (int i = 0; i < elementCount; i++) {
            @SuppressWarnings("unchecked")
            var v = (T) valueLayout.varHandle().get(data, i * valueLayout.byteSize());
            ret.add(v);
        }
        return ret;
    }

    /**
     * If it is an array of enum values, then you can get the enum using this helper
     *
     * @param a    the array
     * @param type the enum class (must be one of the wayland-scanner generated ones
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> List<E> asEnum(WlArray a, Class<E> type) {
        List<Integer> integers = WlArray.toList(a.arrayPtr, C.C_INT, int.class);
        List<E> ret = new ArrayList<>(integers.size());
        for (Integer i : integers) {
            ret.add(EnumUtil.of(type, i));
        }
        return ret;
    }

}
