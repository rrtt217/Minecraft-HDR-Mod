package org.freedesktop.wayland.shared;

import java.util.HashMap;
import java.util.Map;
import org.freedesktop.wayland.util.EnumUtil;
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
 * rendering intents
 * <p>
 *
 *         See the ICC.1:2022 specification from the International Color Consortium
 *         for more details about rendering intents.
 * <p>
 *         The principles of ICC defined rendering intents apply with all types of
 *         image descriptions, not only those with ICC file profiles.
 * <p>
 *         Compositors must support the perceptual rendering intent. Other
 *         rendering intents are optional.
 *       
 */
public enum WpColorManagerV1RenderIntent {

  /**
   * perceptual
   */
  PERCEPTUAL(0),
  /**
   * media-relative colorimetric
   */
  RELATIVE(1),
  /**
   * saturation
   */
  SATURATION(2),
  /**
   * ICC-absolute colorimetric
   */
  ABSOLUTE(3),
  /**
   * media-relative colorimetric + black point compensation
   */
  RELATIVE_BPC(4),
  /**
   *
   */
  ABSOLUTE_NO_ADAPTATION(5);

  public final int value;

  private WpColorManagerV1RenderIntent(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WpColorManagerV1RenderIntent of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WpColorManagerV1RenderIntent> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WpColorManagerV1RenderIntent.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WpColorManagerV1RenderIntent.class);
  }
}
