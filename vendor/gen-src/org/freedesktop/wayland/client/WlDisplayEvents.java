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
 * core global object
 * <p>
 *
 *       The core global object.  This is a special singleton object.  It
 *       is used for internal Wayland protocol features.
 *     
 */
public interface WlDisplayEvents {
  int VERSION = 1;

  /**
   * fatal error event
   * <p>
   *
   *         The error event is sent out when a fatal (non-recoverable)
   *         error has occurred.  The object_id argument is the object
   *         where the error occurred, most often in response to a request
   *         to that object.  The code identifies the error and is defined
   *         by the object interface.  As such, each interface defines its
   *         own set of error codes.  The message is a brief description
   *         of the error, for (debugging) convenience.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param objectId object where the error occurred
   * @param code error code
   * @param message error description
   */
  public void error(WlDisplayProxy emitter, @Nonnull Proxy objectId, int code, @Nonnull String message);

  /**
   * acknowledge object ID deletion
   * <p>
   *
   *         This event is used internally by the object ID management
   *         logic. When a client deletes an object that it had created,
   *         the server will send this event to acknowledge that it has
   *         seen the delete request. When the client receives this event,
   *         it will know that it can safely reuse the object ID.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param id deleted object ID
   */
  public void deleteId(WlDisplayProxy emitter, int id);
}
