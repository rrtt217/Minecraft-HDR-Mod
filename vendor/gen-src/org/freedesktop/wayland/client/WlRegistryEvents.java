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
 * global registry object
 * <p>
 *
 *       The singleton global registry object.  The server has a number of
 *       global objects that are available to all clients.  These objects
 *       typically represent an actual object in the server (for example,
 *       an input device) or they are singleton objects that provide
 *       extension functionality.
 * <p>
 *       When a client creates a registry object, the registry object
 *       will emit a global event for each global currently in the
 *       registry.  Globals come and go as a result of device or
 *       monitor hotplugs, reconfiguration or other events, and the
 *       registry will send out global and global_remove events to
 *       keep the client up to date with the changes.  To mark the end
 *       of the initial burst of events, the client can use the
 *       wl_display.sync request immediately after calling
 *       wl_display.get_registry.
 * <p>
 *       A client can bind to a global object by using the bind
 *       request.  This creates a client-side handle that lets the object
 *       emit events to the client and lets the client invoke requests on
 *       the object.
 *     
 */
public interface WlRegistryEvents {
  int VERSION = 1;

  /**
   * announce global object
   * <p>
   *
   *         Notify the client of global objects.
   * <p>
   *         The event notifies the client that a global object with
   *         the given name is now available, and it implements the
   *         given version of the given interface.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param name numeric name of the global object
   * @param interface_ interface implemented by the object
   * @param version interface version
   */
  public void global(WlRegistryProxy emitter, int name, @Nonnull String interface_, int version);

  /**
   * announce removal of global object
   * <p>
   *
   *         Notify the client of removed global objects.
   * <p>
   *         This event notifies the client that the global identified
   *         by name is no longer available.  If the client bound to
   *         the global using the bind request, the client should now
   *         destroy that object.
   * <p>
   *         The object remains valid and requests to the object will be
   *         ignored until the client destroys it, to avoid races between
   *         the global going away and a client sending a request to it.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param name numeric name of the global object
   */
  public void globalRemove(WlRegistryProxy emitter, int name);
}
