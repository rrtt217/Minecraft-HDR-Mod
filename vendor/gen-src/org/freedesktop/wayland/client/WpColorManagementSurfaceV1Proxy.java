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
 *         A wp_color_management_surface_v1 allows the client to set the color
 *         space and HDR properties of a surface.
 * <p>
 *         If the wl_surface associated with the wp_color_management_surface_v1 is
 *         destroyed, the wp_color_management_surface_v1 object becomes inert.
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
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class,
    int.class
  },
  signature = "ou",
  functionName = "setImageDescription",
  name = "set_image_description"
)
,
    @Message(
  types = {
  },
  signature = "",
  functionName = "unsetImageDescription",
  name = "unset_image_description"
)

  },
  name = "wp_color_management_surface_v1",
  version = 3,
  events = {
  }
)
public class WpColorManagementSurfaceV1Proxy extends Proxy<WpColorManagementSurfaceV1Events> {

  public static final String INTERFACE_NAME = "wp_color_management_surface_v1";

  public WpColorManagementSurfaceV1Proxy(java.lang.foreign.MemorySegment pointer, WpColorManagementSurfaceV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpColorManagementSurfaceV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy the color management interface for a surface
   * <p>
   *
   *         Destroy the wp_color_management_surface_v1 object and do the same as
   *         unset_image_description.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * set the surface image description
   * <p>
   *
   *         If this protocol object is inert, the protocol error inert is raised.
   * <p>
   *         Set the image description of the underlying surface. The image
   *         description and rendering intent are double-buffered state, see
   *         wl_surface.commit.
   * <p>
   *         It is the client's responsibility to understand the image description
   *         it sets on a surface, and to provide content that matches that image
   *         description. Compositors might convert images to match their own or any
   *         other image descriptions.
   * <p>
   *         Image descriptions which are not ready (see wp_image_description_v1)
   *         are forbidden in this request, and in such case the protocol error
   *         image_description is raised.
   * <p>
   *         All image descriptions which are ready (see wp_image_description_v1)
   *         are allowed and must always be accepted by the compositor.
   * <p>
   *         When an image description is set on a surface, it establishes an
   *         explicit link between surface pixel values and surface colorimetry.
   *         This link may be undefined for some pixel values, see the image
   *         description creator interfaces for the conditions. Non-finite
   *         floating-point values (NaN, Inf) always have an undefined colorimetry.
   * <p>
   *         A rendering intent provides the client's preference on how surface
   *         colorimetry should be mapped to each output. The render_intent value
   *         must be one advertised by the compositor with
   *         wp_color_manager_v1.render_intent event, otherwise the protocol error
   *         render_intent is raised.
   * <p>
   *         By default, a surface does not have an associated image description
   *         nor a rendering intent. The handling of color on such surfaces is
   *         compositor implementation defined. Compositors should handle such
   *         surfaces as sRGB, but may handle them differently if they have specific
   *         requirements.
   * <p>
   *         Setting the image description has copy semantics; after this request,
   *         the image description can be immediately destroyed without affecting
   *         the pending state of the surface.
   *       
   * @param imageDescription 
   * @param renderIntent rendering intent
   */
  public void setImageDescription(@Nonnull WpImageDescriptionV1Proxy imageDescription, int renderIntent) {
    marshal(1, Arguments.create(2).set(0, imageDescription).set(1, renderIntent));
  }

  /**
   * remove the surface image description
   * <p>
   *
   *         If this protocol object is inert, the protocol error inert is raised.
   * <p>
   *         This request removes any image description from the surface. See
   *         set_image_description for how a compositor handles a surface without
   *         an image description. This is double-buffered state, see
   *         wl_surface.commit.
   *       
   */
  public void unsetImageDescription() {
    marshal(2);
  }
}
