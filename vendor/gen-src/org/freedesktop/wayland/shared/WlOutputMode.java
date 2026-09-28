package org.freedesktop.wayland.shared;

import java.util.HashMap;
import java.util.Map;
import org.freedesktop.wayland.util.EnumUtil;
// 
//
//    Copyright © 2008-2011 Kristian Høgsberg
//    Copyright © 2010-2011 Intel Corporation
//    Copyright © 2012-2013 Collabora, Ltd.
//
//    Permission is hereby granted, free of charge, to any person
//    obtaining a copy of this software and associated documentation files
//    (the "Software"), to deal in the Software without restriction,
//    including without limitation the rights to use, copy, modify, merge,
//    publish, distribute, sublicense, and/or sell copies of the Software,
//    and to permit persons to whom the Software is furnished to do so,
//    subject to the following conditions:
//
//    The above copyright notice and this permission notice (including the
//    next paragraph) shall be included in all copies or substantial
//    portions of the Software.
//
//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//    NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
//    BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
//    ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//    CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//    SOFTWARE.
//  
/**
 * mode information
 * <p>
 *
 *         These flags describe properties of an output mode.
 *         They are used in the flags bitfield of the mode event.
 *       
 */
public enum WlOutputMode {

  /**
   * indicates this is the current mode
   */
  CURRENT(0x1),
  /**
   * indicates this is the preferred mode
   */
  PREFERRED(0x2);

  public final int value;

  private WlOutputMode(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WlOutputMode of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WlOutputMode> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WlOutputMode.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WlOutputMode.class);
  }
}
