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
 * color management extension to a surface
 * <p>
 *
 *         A wp_color_management_surface_feedback_v1 allows the client to get the
 *         preferred image description of a surface.
 * <p>
 *         If the wl_surface associated with this object is destroyed, the
 *         wp_color_management_surface_feedback_v1 object becomes inert.
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
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class
  },
  signature = "n",
  functionName = "getPreferred",
  name = "get_preferred"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class
  },
  signature = "n",
  functionName = "getPreferredParametric",
  name = "get_preferred_parametric"
)

  },
  name = "wp_color_management_surface_feedback_v1",
  version = 3,
  events = {
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "preferredChanged",
  name = "preferred_changed"
)
,
    @Message(
  types = {
    int.class,
    int.class
  },
  signature = "2uu",
  functionName = "preferredChanged2",
  name = "preferred_changed2"
)

  }
)
public class WpColorManagementSurfaceFeedbackV1Proxy extends Proxy<WpColorManagementSurfaceFeedbackV1Events> {

  public static final String INTERFACE_NAME = "wp_color_management_surface_feedback_v1";

  public WpColorManagementSurfaceFeedbackV1Proxy(java.lang.foreign.MemorySegment pointer, WpColorManagementSurfaceFeedbackV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpColorManagementSurfaceFeedbackV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy the color management interface for a surface
   * <p>
   *
   *         Destroy the wp_color_management_surface_feedback_v1 object.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * get the preferred image description
   * <p>
   *
   *         If this protocol object is inert, the protocol error inert is raised.
   * <p>
   *         The preferred image description represents the compositor's preferred
   *         color encoding for this wl_surface at the current time. There might be
   *         performance and power advantages, as well as improved color
   *         reproduction, if the image description of a content update matches the
   *         preferred image description.
   * <p>
   *         This creates a new wp_image_description_v1 object for the currently
   *         preferred image description for the wl_surface. The client should
   *         stop using and destroy the image descriptions created by earlier
   *         invocations of this request for the associated wl_surface.
   *         This request is usually sent as a reaction to the preferred_changed
   *         event or when creating a wp_color_management_surface_feedback_v1 object
   *         if the client is capable of adapting to image descriptions.
   * <p>
   *         The created wp_image_description_v1 object preserves the preferred image
   *         description of the wl_surface from the time the object was created.
   * <p>
   *         The resulting image description object allows get_information request.
   * <p>
   *         If the image description is parametric, the client should set it on its
   *         wl_surface only if the image description is an exact match with the
   *         client content. Particularly if everything else matches, but the target
   *         color volume is greater than what the client needs, the client should
   *         create its own parameric image description with its exact parameters.
   * <p>
   *         If the interface version is inadequate for the preferred image
   *         description, meaning that the client does not support all the
   *         events needed to deliver the crucial information, the resulting image
   *         description object shall immediately deliver the
   *         wp_image_description_v1.failed event with the low_version cause,
   *         otherwise the object shall immediately deliver the ready event.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionV1Proxy getPreferred(WpImageDescriptionV1Events implementation) {
    return marshalConstructor(1, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * get the preferred image description
   * <p>
   *
   *         The same description as for get_preferred applies, except the returned
   *         image description is guaranteed to be parametric. This is meant for
   *         clients that can only deal with parametric image descriptions.
   * <p>
   *         If the compositor doesn't support parametric image descriptions, the
   *         unsupported_feature error is emitted.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionV1Proxy getPreferredParametric(WpImageDescriptionV1Events implementation) {
    return marshalConstructor(2, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(1).set(0, 0));
  }
}
