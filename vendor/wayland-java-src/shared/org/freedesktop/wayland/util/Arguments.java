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

import org.freedesktop.wayland.raw.wl_argument;
import org.freedesktop.wayland.raw.wl_array;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;


public class Arguments {
    public final MemorySegment pointer;
    // TODO cleanup argument refs?
    private final List<Object> argumentRefs = new LinkedList<>();

    Arguments(final MemorySegment pointer) {
        this.pointer = pointer;
    }

    public static Arguments create(final int size) {
        return new Arguments(
                // TODO audit arena
                wl_argument.allocateArray(size, Memory.ARENA_AUTO)
        );
    }

    private MemorySegment getAt(final int index) {
        return wl_argument.asSlice(pointer, index);
    }

    public int getI(final int index) {
        return wl_argument.i(getAt(index));
    }

    public int getU(final int index) {
        return wl_argument.u(getAt(index));
    }

    public Fixed getFixed(final int index) {
        return new Fixed(wl_argument.f(getAt(index)));
    }

    public String getS(final int index) {
        return wl_argument.s(getAt(index)).getString(0);
    }

    public MemorySegment getO(final int index) {
        return wl_argument.o(getAt(index));
    }

    public int getN(final int index) {
        return wl_argument.n(getAt(index));
    }

    public MemorySegment getA(final int index) {
        return wl_argument.a(getAt(index));
    }

    public int getH(final int index) {
        return wl_argument.h(getAt(index));
    }

    /**
     * int32_t i;  signed integer
     * uint32_t u; unsigned integer
     * uint32_t n; new_id
     * int32_t h; file descriptor
     *
     * @param index
     * @param iunh
     * @return
     */
    public Arguments set(final int index,
                         final int iunh) {
        wl_argument.i(getAt(index), iunh);
        return this;
    }

    /**
     * struct wl_object *o; object
     *
     * @param index
     * @param o
     * @return
     */
    public Arguments set(final int index,
                         final WaylandObject o) {
        this.argumentRefs.add(o);
        wl_argument.o(getAt(index),
                o.getPointer()
        );
        return this;
    }

    /**
     * wl_fixed_t f; fixed point
     *
     * @param index
     * @param f
     * @return
     */
    public Arguments set(final int index,
                         final Fixed f) {
        this.argumentRefs.add(f);
        wl_argument.f(getAt(index),
                f.getRaw()
        );
        return this;
    }

    /**
     * const char *s; string
     *
     * @param index
     * @param s
     * @return
     */
    public Arguments set(final int index,
                         final String s) {
        // TODO use proper arena
        MemorySegment nativeStringValue = Memory.ARENA_AUTO.allocateFrom((String) s);
        wl_argument.s(getAt(index), nativeStringValue);
        this.argumentRefs.add(nativeStringValue);
        return this;
    }

    /**
     * struct wl_array *a; array
     *
     * @param index
     * @param array
     * @return
     */

    public Arguments set(final int index, final WlArray array) {
        return set(index, array.asByteBuffer());
    }

    public Arguments set(final int index, final ByteBuffer array) {
        MemorySegment wl_array_current = wl_array.allocate(Memory.ARENA_AUTO);
        var dataPtr = MemorySegment.ofBuffer(array);
        wl_array.alloc(wl_array_current, dataPtr.byteSize());
        wl_array.size(wl_array_current, dataPtr.byteSize());
        wl_array.data(wl_array_current, dataPtr);
        wl_argument.a(getAt(index), wl_array_current);
        this.argumentRefs.add(wl_array_current);
        return this;
    }


    @Override
    public int hashCode() {
        return this.pointer.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Arguments arguments = (Arguments) o;

        return this.pointer.equals(arguments.pointer);
    }
}
