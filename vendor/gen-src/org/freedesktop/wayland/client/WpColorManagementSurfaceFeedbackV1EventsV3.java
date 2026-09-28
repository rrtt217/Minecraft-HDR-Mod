package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public interface WpColorManagementSurfaceFeedbackV1EventsV3 extends WpColorManagementSurfaceFeedbackV1EventsV2 {
  int VERSION = 3;

  /**
   * the preferred image description changed (32-bit)
   * <p>
   *
   *         Starting from interface version 2, 'preferred_changed2' is sent instead
   *         of this event. See the 'preferred_changed2' event for the definition.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param identity the 32-bit image description id number
   */
  public void preferredChanged(WpColorManagementSurfaceFeedbackV1Proxy emitter, int identity);

  /**
   * the preferred image description changed
   * <p>
   *
   *         The preferred image description is the one which likely has the most
   *         performance and/or quality benefits for the compositor if used by the
   *         client for its wl_surface contents. This event is sent whenever the
   *         compositor changes the wl_surface's preferred image description.
   * <p>
   *         This event sends the identity of the new preferred state as the argument,
   *         so clients who are aware of the image description already can reuse it.
   *         Otherwise, if the client client wants to know what the preferred image
   *         description is, it shall use the get_preferred request.
   * <p>
   *         The preferred image description is not automatically used for anything.
   *         It is only a hint, and clients may set any valid image description with
   *         set_image_description, but there might be performance and color accuracy
   *         improvements by providing the wl_surface contents in the preferred
   *         image description. Therefore clients that can, should render according
   *         to the preferred image description
   *       
   * @param emitter The protocol object that emitted the event.
   * @param identityHi high 32 bits of the 64-bit image description id number
   * @param identityLo low 32 bits of the 64-bit image description id number
   */
  public void preferredChanged2(WpColorManagementSurfaceFeedbackV1Proxy emitter, int identityHi, int identityLo);
}
