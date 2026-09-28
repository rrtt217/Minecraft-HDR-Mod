package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public interface WlBufferEvents {
  int VERSION = 1;

  /**
   * compositor releases buffer
   * <p>
   *
   *         Sent when this wl_buffer is no longer used by the compositor.
   * <p>
   *         For more information on when release events may or may not be sent,
   *         and what consequences it has, please see the description of
   *         wl_surface.attach.
   * <p>
   *         If a client receives a release event before the frame callback
   *         requested in the same wl_surface.commit that attaches this
   *         wl_buffer to a surface, then the client is immediately free to
   *         reuse the buffer and its backing storage, and does not need a
   *         second buffer for the next surface content update. Typically
   *         this is possible, when the compositor maintains a copy of the
   *         wl_surface contents, e.g. as a GL texture. This is an important
   *         optimization for GL(ES) compositors with wl_shm clients.
   *       
   * @param emitter The protocol object that emitted the event.
   */
  public void release(WlBufferProxy emitter);
}
