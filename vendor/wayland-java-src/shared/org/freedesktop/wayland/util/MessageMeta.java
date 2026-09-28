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

import org.freedesktop.wayland.raw.wl_message;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.stream.Stream;

/**
 * Wrapper class for a {@link Message} to create a native wayland message for use with the native library. To create
 * a new native context for a given {@link Message}, use {@link #init(MemorySegment, Message)} .
 *
 * @see InterfaceMeta
 */
public class MessageMeta {

    public final MemorySegment wlMessagePointer;
    private final Message message;

    protected MessageMeta(final MemorySegment wlMessagePointer,
                          final Message message) {
        this.wlMessagePointer = wlMessagePointer;
        this.message = message;
        ObjectCache.store(this.wlMessagePointer, this);
    }

    public MemorySegment getNativeWlMessage() {
        return this.wlMessagePointer;
    }

    public static MemorySegment initArray(Message[] methods, Arena arena) {
        MemorySegment methods_array = wl_message.allocateArray(methods.length, arena);
        for (int i = 0; i < methods.length; i++) {
            MemorySegment wlMessagePointer = wl_message.asSlice(methods_array, i);
            MessageMeta.init(wlMessagePointer, methods[i]);
        }
        return methods_array;
    }

    public static MessageMeta init(final MemorySegment wlMessagePointer, final Message message) {
        wl_message.name(wlMessagePointer, Memory.ARENA_AUTO.allocateFrom(message.name()));
        wl_message.signature(wlMessagePointer, Memory.ARENA_AUTO.allocateFrom(message.signature()));
        MemorySegment typesArray = PointerArray.fromStream(Stream.of(message.types()),
                (c) -> InterfaceMeta.get(c).getNativeWlInterface(),
                Memory.ARENA_AUTO
        );
        wl_message.types(wlMessagePointer, typesArray);
        return new MessageMeta(wlMessagePointer, message);
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public int hashCode() {
        return getNativeWlMessage().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final MessageMeta messageMeta = (MessageMeta) o;

        return getNativeWlMessage().equals(messageMeta.getNativeWlMessage());
    }
}
