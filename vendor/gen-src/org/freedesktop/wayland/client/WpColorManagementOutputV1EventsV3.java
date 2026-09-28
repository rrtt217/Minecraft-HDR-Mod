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
 * output color properties
 * <p>
 *
 *       A wp_color_management_output_v1 describes the color properties of an
 *       output.
 * <p>
 *       The wp_color_management_output_v1 is associated with the wl_output global
 *       underlying the wl_output object. Therefore the client destroying the
 *       wl_output object has no impact, but the compositor removing the output
 *       global makes the wp_color_management_output_v1 object inert.
 *     
 */
public interface WpColorManagementOutputV1EventsV3 extends WpColorManagementOutputV1EventsV2 {
  int VERSION = 3;

  /**
   * image description changed
   * <p>
   *
   *         This event is sent whenever the image description of the output changed,
   *         followed by one wl_output.done event common to output events across all
   *         extensions.
   * <p>
   *         If the client wants to use the updated image description, it needs to do
   *         get_image_description again, because image description objects are
   *         immutable.
   *       
   * @param emitter The protocol object that emitted the event.
   */
  public void imageDescriptionChanged(WpColorManagementOutputV1Proxy emitter);
}
