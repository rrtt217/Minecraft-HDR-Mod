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
@Interface(
  methods = {
    @Message(
  types = {
    int.class,
    org.freedesktop.wayland.client.Proxy.class
  },
  signature = "usun",
  functionName = "bind",
  name = "bind"
)

  },
  name = "wl_registry",
  version = 1,
  events = {
    @Message(
  types = {
    int.class,
    java.lang.String.class,
    int.class
  },
  signature = "usu",
  functionName = "global",
  name = "global"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "globalRemove",
  name = "global_remove"
)

  }
)
public class WlRegistryProxy extends Proxy<WlRegistryEvents> {

  public static final String INTERFACE_NAME = "wl_registry";

  public WlRegistryProxy(java.lang.foreign.MemorySegment pointer, WlRegistryEvents implementation, int version) {
    super(pointer, implementation, version);
  }

  public WlRegistryProxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * bind an object to the display
   * <p>
   *
   *         Binds a new, client-created object to the server using the
   *         specified name as the identifier.
   *       
   * @param name unique numeric name of the object
   * @param proxyType The type of proxy to create. Must be a subclass of Proxy.
   * @param version The protocol version to use. Must not be higher than what the supplied implementation can support.
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public <J,T extends Proxy<J>> T bind(int name, Class<T> proxyType, int version, J implementation) {
    return marshalConstructor(0, implementation, version, proxyType, Arguments.create(4).set(0, name).set(1, proxyType.getAnnotation(Interface.class).name()).set(2, version).set(3, 0));
  }
}
