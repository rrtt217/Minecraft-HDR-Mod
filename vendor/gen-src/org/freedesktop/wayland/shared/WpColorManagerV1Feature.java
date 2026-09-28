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
 * compositor supported features
 * <p>
 */
public enum WpColorManagerV1Feature {

  /**
   * create_icc_creator request
   */
  ICC_V2_V4(0),
  /**
   * create_parametric_creator request
   */
  PARAMETRIC(1),
  /**
   * parametric set_primaries request
   */
  SET_PRIMARIES(2),
  /**
   * parametric set_tf_power request
   */
  SET_TF_POWER(3),
  /**
   * parametric set_luminances request
   */
  SET_LUMINANCES(4),
  /**
   *
   */
  SET_MASTERING_DISPLAY_PRIMARIES(5),
  /**
   *
   */
  EXTENDED_TARGET_VOLUME(6),
  /**
   * create_windows_scrgb request
   */
  WINDOWS_SCRGB(7),
  /**
   * create_windows_bt2100 request
   */
  WINDOWS_BT2100(8);

  public final int value;

  private WpColorManagerV1Feature(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WpColorManagerV1Feature of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WpColorManagerV1Feature> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WpColorManagerV1Feature.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WpColorManagerV1Feature.class);
  }
}
