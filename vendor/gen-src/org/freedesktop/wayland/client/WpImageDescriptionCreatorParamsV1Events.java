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
 * holder of image description parameters
 * <p>
 *
 *       This type of object is used for collecting all the parameters required
 *       to create a wp_image_description_v1 object. A complete set of required
 *       parameters consists of these properties:
 *       - transfer characteristic function (tf)
 *       - chromaticities of primaries and white point (primary color volume)
 * <p>
 *       The following properties are optional and have a well-defined default
 *       if not explicitly set:
 *       - primary color volume luminance range
 *       - reference white luminance level
 *       - mastering display primaries and white point (target color volume)
 *       - mastering luminance range
 * <p>
 *       The following properties are optional and will be ignored
 *       if not explicitly set:
 *       - maximum content light level
 *       - maximum frame-average light level
 * <p>
 *       Each required property must be set exactly once if the client is to create
 *       an image description. The set requests verify that a property was not
 *       already set. The create request verifies that all required properties are
 *       set. There may be several alternative requests for setting each property,
 *       and in that case the client must choose one of them.
 * <p>
 *       Once all properties have been set, the create request must be used to
 *       create the image description object, destroying the creator in the
 *       process.
 * <p>
 *       A viewer, who is viewing the display defined by the resulting image
 *       description (the viewing environment included), is assumed to be fully
 *       adapted to the primary color volume's white point.
 * <p>
 *       Any of the following conditions will cause the colorimetry of a pixel
 *       to become undefined:
 *       - Values outside of the defined range of the transfer characteristic.
 *       - Tristimulus that exceeds the target color volume.
 *       - If extended_target_volume is not supported: tristimulus that exceeds
 *       the primary color volume.
 * <p>
 *       The closest correspondence to an image description created through this
 *       interface is the Display class of profiles in ICC.
 *     
 */
public interface WpImageDescriptionCreatorParamsV1Events {
  int VERSION = 1;
}
