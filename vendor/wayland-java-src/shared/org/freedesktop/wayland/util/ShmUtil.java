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

import org.freedesktop.wayland.raw.C;

import java.lang.foreign.MemorySegment;

public class ShmUtil {
    public static int mkstemp(String template) {
        return C.mkstemp(Memory.ARENA_AUTO.allocateFrom(template));
    }

    public static int fcntl(int fd,
                            int cmd,
                            int arg) {
        var invoker = C.fcntl.makeInvoker(C.C_INT);
        return invoker.apply(fd, cmd, arg);
    }

    public static MemorySegment mmap(long addr,
                                     int len,
                                     int prot,
                                     int flags,
                                     int fildes,
                                     int off) {

        return C.mmap(MemorySegment.ofAddress(addr), len, prot, flags, fildes, off);
    }

    public static int munmap(MemorySegment ptr, long len) {
        return C.munmap(ptr, len);
    }

    public static int close(int fildes) {
        return C.close(fildes);
    }

    public static int ftruncate(int fildes, int length) {
        return C.ftruncate(fildes, length);
    }

}
