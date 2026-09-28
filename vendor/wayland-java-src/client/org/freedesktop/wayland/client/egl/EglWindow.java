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
package org.freedesktop.wayland.client.egl;

import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.util.ObjectCache;

import java.lang.foreign.MemorySegment;
import java.util.Objects;


public class EglWindow {

    public final MemorySegment pointer;

    protected EglWindow(final MemorySegment pointer) {
        this.pointer = pointer;
        ObjectCache.store(this.pointer, this);
    }

    public static EglWindow create(final Proxy<?> wlSurfaceProxy,
                                   final int width,
                                   final int height) {
        // TODO implement when we jextract the egl header
//        return EglWindow.get(
//                LibWayland.wl_egl_window_create(wlSurfaceProxy.pointer,
//                        width,
//                        height)
//        );
        throw new UnsupportedOperationException("TODO implement");
    }

    public static EglWindow get(final MemorySegment pointer) {
        EglWindow eglWindow = ObjectCache.from(pointer);
        if (eglWindow == null) {
            eglWindow = new EglWindow(pointer);
        }
        return eglWindow;
    }

    public void resize(final int width,
                       final int height,
                       final int dx,
                       final int dy) {
        /*
        LibWayland.wl_egl_window_resize(this.pointer,
                width,
                height,
                dx,
                dy);

         */
        // TODO uncomment when egl
    }

    public Size getAttachedSize() {
        /*
        try (Arena arena = Arena.ofAuto()) {
            final var x = arena.allocateFrom(C.C_INT);
            final var y = arena.allocateFrom(C.C_INT);
            LibWayland.wl_egl_window_get_attached_size(this.pointer,
                    x,
                    y
            );
            return new Size(x.get(C.C_INT, 0), y.get(C.C_INT, 0));
        }
         */
        // TODO uncomment and test once egl is added
        throw new UnsupportedOperationException("TODO implement");
    }

    public void destroy() {
        // TODO uncomment egl LibWayland.wl_egl_window_destroy(this.pointer);
        ObjectCache.remove(this.pointer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EglWindow eglWindow)) return false;
        return Objects.equals(pointer, eglWindow.pointer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pointer);
    }

    public record Size(int width, int height) {
    }
}