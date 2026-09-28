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
 * wl_surface error values
 * <p>
 *
 *         These errors can be emitted in response to wl_surface requests.
 *       
 */
public enum WlSurfaceError {

  /**
   * buffer scale value is invalid
   */
  INVALID_SCALE(0),
  /**
   * buffer transform value is invalid
   */
  INVALID_TRANSFORM(1),
  /**
   * buffer size is invalid
   */
  INVALID_SIZE(2),
  /**
   * buffer offset is invalid
   */
  INVALID_OFFSET(3),
  /**
   * surface was destroyed before its role object
   */
  DEFUNCT_ROLE_OBJECT(4),
  /**
   * no buffer was attached
   */
  NO_BUFFER(5);

  public final int value;

  private WlSurfaceError(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WlSurfaceError of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WlSurfaceError> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WlSurfaceError.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WlSurfaceError.class);
  }
}
