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
 * global error values
 * <p>
 *
 *         These errors are global and can be emitted in response to any
 *         server request.
 *       
 */
public enum WlDisplayError {

  /**
   * server couldn't find object
   */
  INVALID_OBJECT(0),
  /**
   * method doesn't exist on the specified interface or malformed request
   */
  INVALID_METHOD(1),
  /**
   * server is out of memory
   */
  NO_MEMORY(2),
  /**
   * implementation error in compositor
   */
  IMPLEMENTATION(3);

  public final int value;

  private WlDisplayError(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WlDisplayError of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WlDisplayError> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WlDisplayError.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WlDisplayError.class);
  }
}
