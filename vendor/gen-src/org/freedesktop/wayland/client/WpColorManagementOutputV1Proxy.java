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
  functionName = "getImageDescription",
  name = "get_image_description"
)

  },
  name = "wp_color_management_output_v1",
  version = 3,
  events = {
    @Message(
  types = {
  },
  signature = "",
  functionName = "imageDescriptionChanged",
  name = "image_description_changed"
)

  }
)
public class WpColorManagementOutputV1Proxy extends Proxy<WpColorManagementOutputV1Events> {

  public static final String INTERFACE_NAME = "wp_color_management_output_v1";

  public WpColorManagementOutputV1Proxy(java.lang.foreign.MemorySegment pointer, WpColorManagementOutputV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpColorManagementOutputV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy the color management output
   * <p>
   *
   *         Destroy the color wp_color_management_output_v1 object. This does not
   *         affect any remaining protocol objects.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * get the image description of the output
   * <p>
   *
   *         This creates a new wp_image_description_v1 object for the current image
   *         description of the output. There always is exactly one image description
   *         active for an output so the client should destroy the image description
   *         created by earlier invocations of this request. This request is usually
   *         sent as a reaction to the image_description_changed event or when
   *         creating a wp_color_management_output_v1 object.
   * <p>
   *         The image description of an output represents the color encoding the
   *         output expects. There might be performance and power advantages, as well
   *         as improved color reproduction, if a content update matches the image
   *         description of the output it is being shown on. If a content update is
   *         shown on any other output than the one it matches the image description
   *         of, then the color reproduction on those outputs might be considerably
   *         worse.
   * <p>
   *         The created wp_image_description_v1 object preserves the image
   *         description of the output from the time the object was created.
   * <p>
   *         The resulting image description object allows get_information request.
   * <p>
   *         If this protocol object is inert, the resulting image description object
   *         shall immediately deliver the wp_image_description_v1.failed event with
   *         the no_output cause.
   * <p>
   *         If the interface version is inadequate for the output's image
   *         description, meaning that the client does not support all the events
   *         needed to deliver the crucial information, the resulting image
   *         description object shall immediately deliver the
   *         wp_image_description_v1.failed event with the low_version cause.
   * <p>
   *         Otherwise the object shall immediately deliver the ready event.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionV1Proxy getImageDescription(WpImageDescriptionV1Events implementation) {
    return marshalConstructor(1, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(1).set(0, 0));
  }
}
