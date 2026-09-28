package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.freedesktop.wayland.util.Arguments;
import org.freedesktop.wayland.util.Interface;
import org.freedesktop.wayland.util.Message;
// 
//
//    Copyright 2019 Sebastian Wick
//    Copyright 2019 Erwin Burema
//    Copyright 2020 AMD
//    Copyright 2020-2024 Collabora, Ltd.
//    Copyright 2024 Xaver Hugl
//    Copyright 2022-2025 Red Hat, Inc.
//
//    Permission is hereby granted, free of charge, to any person obtaining a
//    copy of this software and associated documentation files (the "Software"),
//    to deal in the Software without restriction, including without limitation
//    the rights to use, copy, modify, merge, publish, distribute, sublicense,
//    and/or sell copies of the Software, and to permit persons to whom the
//    Software is furnished to do so, subject to the following conditions:
//
//    The above copyright notice and this permission notice (including the next
//    paragraph) shall be included in all copies or substantial portions of the
//    Software.
//
//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
//    THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
//    DEALINGS IN THE SOFTWARE.
//  
/**
 * Colorimetric image description
 * <p>
 *
 *       An image description carries information about the pixel color encoding
 *       and its intended display and viewing environment. The image description is
 *       attached to a wl_surface via
 *       wp_color_management_surface_v1.set_image_description. A compositor can use
 *       this information to decode pixel values into colorimetrically meaningful
 *       quantities, which allows the compositor to transform the surface contents
 *       to become suitable for various displays and viewing environments.
 * <p>
 *       Note, that the wp_image_description_v1 object is not ready to be used
 *       immediately after creation. The object eventually delivers either the
 *       'ready' or the 'failed' event, specified in all requests creating it. The
 *       object is deemed "ready" after receiving the 'ready' event.
 * <p>
 *       An object which is not ready is illegal to use, it can only be destroyed.
 *       Any other request in this interface shall result in the 'not_ready'
 *       protocol error. Attempts to use an object which is not ready through other
 *       interfaces shall raise protocol errors defined there.
 * <p>
 *       Once created and regardless of how it was created, a
 *       wp_image_description_v1 object always refers to one fixed image
 *       description. It cannot change after creation.
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
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionInfoV1Proxy.class
  },
  signature = "n",
  functionName = "getInformation",
  name = "get_information"
)

  },
  name = "wp_image_description_v1",
  version = 3,
  events = {
    @Message(
  types = {
    int.class,
    java.lang.String.class
  },
  signature = "us",
  functionName = "failed",
  name = "failed"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "ready",
  name = "ready"
)
,
    @Message(
  types = {
    int.class,
    int.class
  },
  signature = "2uu",
  functionName = "ready2",
  name = "ready2"
)

  }
)
public class WpImageDescriptionV1Proxy extends Proxy<WpImageDescriptionV1Events> {

  public static final String INTERFACE_NAME = "wp_image_description_v1";

  public WpImageDescriptionV1Proxy(java.lang.foreign.MemorySegment pointer, WpImageDescriptionV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpImageDescriptionV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy the image description
   * <p>
   *
   *         Destroy this object. It is safe to destroy an object which is not ready.
   * <p>
   *         Destroying a wp_image_description_v1 object has no side-effects, not
   *         even if a wp_color_management_surface_v1.set_image_description has not
   *         yet been followed by a wl_surface.commit.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * get information about the image description
   * <p>
   *
   *         Creates a wp_image_description_info_v1 object which delivers the
   *         information that makes up the image description.
   * <p>
   *         Not all image description protocol objects allow get_information
   *         request. Whether it is allowed or not is defined by the request that
   *         created the object. If get_information is not allowed, the protocol
   *         error no_information is raised.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionInfoV1Proxy getInformation(WpImageDescriptionInfoV1Events implementation) {
    return marshalConstructor(1, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionInfoV1Proxy.class, Arguments.create(1).set(0, 0));
  }
}
