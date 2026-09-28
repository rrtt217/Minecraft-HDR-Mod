package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.freedesktop.wayland.util.Arguments;
import org.freedesktop.wayland.util.Interface;
import org.freedesktop.wayland.util.Message;
// 
//
//    Copyright © 2008-2011 Kristian Høgsberg
//    Copyright © 2010-2011 Intel Corporation
//    Copyright © 2012-2013 Collabora, Ltd.
//
//    Permission is hereby granted, free of charge, to any person
//    obtaining a copy of this software and associated documentation files
//    (the "Software"), to deal in the Software without restriction,
//    including without limitation the rights to use, copy, modify, merge,
//    publish, distribute, sublicense, and/or sell copies of the Software,
//    and to permit persons to whom the Software is furnished to do so,
//    subject to the following conditions:
//
//    The above copyright notice and this permission notice (including the
//    next paragraph) shall be included in all copies or substantial
//    portions of the Software.
//
//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//    NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
//    BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
//    ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//    CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//    SOFTWARE.
//  
/**
 * content for a wl_surface
 * <p>
 *
 *       A buffer provides the content for a wl_surface. Buffers are
 *       created through factory interfaces such as wl_shm, wp_linux_buffer_params
 *       (from the linux-dmabuf protocol extension) or similar. It has a width and
 *       a height and can be attached to a wl_surface, but the mechanism by which a
 *       client provides and updates the contents is defined by the buffer factory
 *       interface.
 * <p>
 *       Color channels are assumed to be electrical rather than optical (in other
 *       words, encoded with a transfer function) unless otherwise specified. If
 *       the buffer uses a format that has an alpha channel, the alpha channel is
 *       assumed to be premultiplied into the electrical color channel values
 *       (after transfer function encoding) unless otherwise specified.
 * <p>
 *       Note, because wl_buffer objects are created from multiple independent
 *       factory interfaces, the wl_buffer interface is frozen at version 1.
 *     
 */
@Interface(
  methods = {
    @Message(
  types = {
  },
  signature = "",
  functionName = "destroy",
  name = "destroy"
)

  },
  name = "wl_buffer",
  version = 1,
  events = {
    @Message(
  types = {
  },
  signature = "",
  functionName = "release",
  name = "release"
)

  }
)
public class WlBufferProxy extends Proxy<WlBufferEvents> {

  public static final String INTERFACE_NAME = "wl_buffer";

  public WlBufferProxy(java.lang.foreign.MemorySegment pointer, WlBufferEvents implementation, int version) {
    super(pointer, implementation, version);
  }

  public WlBufferProxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy a buffer
   * <p>
   *
   *         Destroy a buffer. If and how you need to release the backing
   *         storage is defined by the buffer factory interface.
   * <p>
   *         For possible side-effects to a surface, see wl_surface.attach.
   *       
   */
  public void destroy() {
    marshal(0);
  }
}
