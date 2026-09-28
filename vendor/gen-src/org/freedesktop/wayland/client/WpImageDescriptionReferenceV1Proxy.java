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
 * Reference to an image description
 * <p>
 *
 *       This object is a reference to an image description. This interface is
 *       frozen at version 1 to allow other protocols to create
 *       wp_image_description_v1 objects.
 * <p>
 *       The wp_color_manager_v1.get_image_description request can be used to
 *       retrieve the underlying image description.
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

  },
  name = "wp_image_description_reference_v1",
  version = 1,
  events = {
  }
)
public class WpImageDescriptionReferenceV1Proxy extends Proxy<WpImageDescriptionReferenceV1Events> {

  public static final String INTERFACE_NAME = "wp_image_description_reference_v1";

  public WpImageDescriptionReferenceV1Proxy(java.lang.foreign.MemorySegment pointer, WpImageDescriptionReferenceV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpImageDescriptionReferenceV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy the reference
   * <p>
   *
   *         Destroy this object. This has no effect on the referenced image
   *         description.
   *       
   */
  public void destroy() {
    marshal(0);
  }
}
