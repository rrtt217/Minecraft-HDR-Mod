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
 * core global object
 * <p>
 *
 *       The core global object.  This is a special singleton object.  It
 *       is used for internal Wayland protocol features.
 *     
 * <p>
 * @see Display
 */
@Interface(
  methods = {
    @Message(
  types = {
    org.freedesktop.wayland.client.WlCallbackProxy.class
  },
  signature = "n",
  functionName = "sync",
  name = "sync"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WlRegistryProxy.class
  },
  signature = "n",
  functionName = "getRegistry",
  name = "get_registry"
)

  },
  name = "wl_display",
  version = 1,
  events = {
    @Message(
  types = {
    org.freedesktop.wayland.client.Proxy.class,
    int.class,
    java.lang.String.class
  },
  signature = "ous",
  functionName = "error",
  name = "error"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "deleteId",
  name = "delete_id"
)

  }
)
public class WlDisplayProxy extends Display {

  public static final String INTERFACE_NAME = "wl_display";

  public WlDisplayProxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * asynchronous roundtrip
   * <p>
   *
   *         The sync request asks the server to emit the 'done' event
   *         on the returned wl_callback object.  Since requests are
   *         handled in-order and events are delivered in-order, this can
   *         be used as a barrier to ensure all previous requests and the
   *         resulting events have been handled.
   * <p>
   *         The object returned by this request will be destroyed by the
   *         compositor after the callback is fired and as such the client must not
   *         attempt to use it after that point.
   * <p>
   *         The callback_data passed in the callback is undefined and should be ignored.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WlCallbackProxy sync(WlCallbackEvents implementation) {
    return marshalConstructor(0, implementation, getVersion(), org.freedesktop.wayland.client.WlCallbackProxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * get global registry object
   * <p>
   *
   *         This request creates a registry object that allows the client
   *         to list and bind the global objects available from the
   *         compositor.
   * <p>
   *         It should be noted that the server side resources consumed in
   *         response to a get_registry request can only be released when the
   *         client disconnects, not when the client side proxy is destroyed.
   *         Therefore, clients should invoke get_registry as infrequently as
   *         possible to avoid wasting memory.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WlRegistryProxy getRegistry(WlRegistryEvents implementation) {
    return marshalConstructor(1, implementation, getVersion(), org.freedesktop.wayland.client.WlRegistryProxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * Connect to a Wayland display
   * <p>
   * Connect to the Wayland display named 'name'. If 'name' is 'null',
   * its value will be replaced with the WAYLAND_DISPLAY environment
   * variable if it is set, otherwise display "wayland-0" will be used.
   * @param name Name of the Wayland display to connect to
   * @return A {@code WlDisplayProxy} object or null on failure.
   */
  public static WlDisplayProxy connect(String name) {
    return new WlDisplayProxy(Display._connect(name));
  }

  /**
   * Connect to Wayland display on an already open fd
   * <p>
   * The {@code WlDisplayProxy} takes ownership of the fd and will close it when the
   * display is destroyed.  The fd will also be closed in case of
   * failure.
   * @param fd The fd to use for the connection
   * @return A {@code WlDisplayProxy} object or null on failure.
   */
  public static WlDisplayProxy connectToFd(int fd) {
    return new WlDisplayProxy(Display._connect(fd));
  }

  public Void getImplementation() {
    throw new UnsupportedOperationException("Implementation provided by native library");
  }
}
