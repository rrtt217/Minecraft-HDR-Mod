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
 * Colorimetric image description information
 * <p>
 *
 *       Sends all matching events describing an image description object exactly
 *       once and finally sends the 'done' event.
 * <p>
 *       This means
 *       - if the image description is parametric, it must send
 *         - primaries
 *         - named_primaries, if applicable
 *         - at least one of tf_power and tf_named, as applicable
 *         - luminances
 *         - target_primaries
 *         - target_luminance
 *       - if the image description is parametric, it may send, if applicable,
 *         - target_max_cll
 *         - target_max_fall
 *       - if the image description contains an ICC profile, it must send the
 *         icc_file event
 * <p>
 *       Once a wp_image_description_info_v1 object has delivered a 'done' event it
 *       is automatically destroyed.
 * <p>
 *       Every wp_image_description_info_v1 created from the same
 *       wp_image_description_v1 shall always return the exact same data.
 *     
 */
@Interface(
  methods = {
  },
  name = "wp_image_description_info_v1",
  version = 3,
  events = {
    @Message(
  types = {
  },
  signature = "",
  functionName = "done",
  name = "done"
)
,
    @Message(
  types = {
    int.class,
    int.class
  },
  signature = "hu",
  functionName = "iccFile",
  name = "icc_file"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "iiiiiiii",
  functionName = "primaries",
  name = "primaries"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "primariesNamed",
  name = "primaries_named"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "tfPower",
  name = "tf_power"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "tfNamed",
  name = "tf_named"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class
  },
  signature = "uuu",
  functionName = "luminances",
  name = "luminances"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "iiiiiiii",
  functionName = "targetPrimaries",
  name = "target_primaries"
)
,
    @Message(
  types = {
    int.class,
    int.class
  },
  signature = "uu",
  functionName = "targetLuminance",
  name = "target_luminance"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "targetMaxCll",
  name = "target_max_cll"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "targetMaxFall",
  name = "target_max_fall"
)

  }
)
public class WpImageDescriptionInfoV1Proxy extends Proxy<WpImageDescriptionInfoV1Events> {

  public static final String INTERFACE_NAME = "wp_image_description_info_v1";

  public WpImageDescriptionInfoV1Proxy(java.lang.foreign.MemorySegment pointer, WpImageDescriptionInfoV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpImageDescriptionInfoV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }
}
