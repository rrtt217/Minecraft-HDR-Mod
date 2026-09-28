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

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.function.Function;
import java.util.stream.Stream;

public class PointerArray {

    private static final ValueLayout POINTER_LAYOUT = ValueLayout.ADDRESS;
    private static final VarHandle POINTER_HANDLE = POINTER_LAYOUT.varHandle();

    /**
     * Allocates an array of pointers of the specified length.
     *
     * @param length    The number of pointers in the array.
     * @param allocator The Arena to use for allocation.
     * @return A MemorySegment representing the allocated array of pointers.
     */
    public static MemorySegment allocate(long length, SegmentAllocator allocator) {
        return allocator.allocate(POINTER_LAYOUT.byteSize() * length);
    }

    /**
     * Sets a pointer value at the specified index in the array.
     *
     * @param array The MemorySegment representing the array of pointers.
     * @param index The index at which to set the pointer.
     * @param value The MemorySegment to set (representing a pointer).
     */
    public static void setAtIndex(MemorySegment array, long index, MemorySegment value) {
        POINTER_HANDLE.set(array, index * POINTER_LAYOUT.byteSize(), value);
    }

    /**
     * Gets a pointer value at the specified index in the array.
     *
     * @param array The MemorySegment representing the array of pointers.
     * @param index The index from which to get the pointer.
     * @return A MemorySegment representing the pointer at the specified index.
     */
    public static MemorySegment getAtIndex(MemorySegment array, long index) {
        return (MemorySegment) POINTER_HANDLE.get(array, index * POINTER_LAYOUT.byteSize());
    }

    /**
     * Gets the length of the pointer array.
     *
     * @param array The MemorySegment representing the array of pointers.
     * @return The number of pointers in the array.
     */
    public static long length(MemorySegment array) {
        return array.byteSize() / POINTER_LAYOUT.byteSize();
    }

    /**
     * Copies a Java array of MemorySegments into a native array of pointers.
     *
     * @param sources The Java array of MemorySegments to copy.
     * @param session The Arena to use for allocation.
     * @return A MemorySegment representing the native array of pointers.
     */
    public static MemorySegment fromArray(MemorySegment[] sources, SegmentAllocator session) {
        MemorySegment array = allocate(sources.length, session);
        for (int i = 0; i < sources.length; i++) {
            setAtIndex(array, i, sources[i]);
        }
        return array;
    }

    /**
     * Copies a native array of pointers into a Java array of MemorySegments.
     *
     * @param array The MemorySegment representing the native array of pointers.
     * @return A Java array of MemorySegments.
     */
    public static MemorySegment[] toArray(MemorySegment array) {
        long length = length(array);
        MemorySegment[] result = new MemorySegment[(int) length];
        for (int i = 0; i < length; i++) {
            result[i] = getAtIndex(array, i);
        }
        return result;
    }

    /**
     * Creates a native array of pointers from a stream of objects and a conversion function.
     *
     * @param <T>       The type of objects in the stream.
     * @param stream    The stream of objects to convert.
     * @param converter A function that converts objects of type T to MemorySegments.
     * @param allocator The Arena to use for allocation.
     * @return A MemorySegment representing the native array of pointers.
     */
    public static <T> MemorySegment fromStream(Stream<T> stream,
                                               Function<T, MemorySegment> converter,
                                               SegmentAllocator allocator) {
        MemorySegment[] segments = stream
                .map(converter)
                .toArray(MemorySegment[]::new);

        return fromArray(segments, allocator);
    }
}
