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
 * named transfer functions
 * <p>
 *
 *         Named transfer functions used to represent well-known transfer
 *         characteristics of displays.
 * <p>
 *         A value of 0 is invalid and will never be present in the list of enums.
 * <p>
 *         See appendix.md for the formulae.
 *       
 */
public enum WpColorManagerV1TransferFunction {

  /**
   *
   */
  BT1886(1),
  /**
   *
   */
  GAMMA22(2),
  /**
   *
   */
  GAMMA28(3),
  /**
   *
   */
  ST240(4),
  /**
   *
   */
  EXT_LINEAR(5),
  /**
   *
   */
  LOG_100(6),
  /**
   *
   */
  LOG_316(7),
  /**
   *
   */
  XVYCC(8),
  /**
   *
   */
  SRGB(9),
  /**
   *
   */
  EXT_SRGB(10),
  /**
   *
   */
  ST2084_PQ(11),
  /**
   *
   */
  ST428(12),
  /**
   *
   */
  HLG(13),
  /**
   *
   */
  COMPOUND_POWER_2_4(14);

  public final int value;

  private WpColorManagerV1TransferFunction(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WpColorManagerV1TransferFunction of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WpColorManagerV1TransferFunction> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WpColorManagerV1TransferFunction.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WpColorManagerV1TransferFunction.class);
  }
}
