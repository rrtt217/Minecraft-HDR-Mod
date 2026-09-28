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
package org.freedesktop.wayland.client;

import org.freedesktop.wayland.raw.C;
import org.freedesktop.wayland.raw.LibWayland;
import org.freedesktop.wayland.util.ObjectCache;

import java.lang.foreign.MemorySegment;

/**
 * A queue for {@link Proxy} object events.
 * <p>
 * Event queues allows the events on a display to be handled in a thread-safe
 * manner.
 *
 * @see Display
 */
public class EventQueue {
    public final MemorySegment pointer;

    protected EventQueue(final MemorySegment pointer) {
        this.pointer = pointer;
        ObjectCache.store(this.pointer, this);
    }

    public static EventQueue get(final MemorySegment pointer) {
        EventQueue eventQueue = ObjectCache.from(pointer);
        if (eventQueue == null) {
            eventQueue = new EventQueue(pointer);
        }
        return eventQueue;
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

        final EventQueue that = (EventQueue) o;

        return this.pointer.equals(that.pointer);
    }

    /**
     * Destroy an event queue
     * <p>
     * Destroy the given event queue. Any pending event on that queue is
     * discarded.
     * </p>
     * The {@link Display} object used to create the queue should not be
     * destroyed until all event queues created with it are destroyed with
     * this function.
     */
    public void destroy() {
        LibWayland.wl_event_queue_destroy(this.pointer);
        ObjectCache.remove(this.pointer);
    }
}
