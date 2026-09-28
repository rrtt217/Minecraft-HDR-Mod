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
 * callback object
 * <p>
 *
 *       Clients can handle the 'done' event to get notified when
 *       the related request is done.
 * <p>
 *       Note, because wl_callback objects are created from multiple independent
 *       factory interfaces, the wl_callback interface is frozen at version 1.
 *     
 */
public interface WlCallbackEvents {
  int VERSION = 1;

  /**
   * done event
   * <p>
   *
   *         Notify the client when the related request is done.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param callbackData request-specific data for the callback
   */
  public void done(WlCallbackProxy emitter, int callbackData);
}
