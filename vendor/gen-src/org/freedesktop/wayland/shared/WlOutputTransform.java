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
 * transformation applied to buffer contents
 * <p>
 *
 *         This describes transformations that clients and compositors apply to
 *         buffer contents.
 * <p>
 *         The flipped values correspond to an initial flip around a
 *         vertical axis followed by rotation.
 * <p>
 *         The purpose is mainly to allow clients to render accordingly and
 *         tell the compositor, so that for fullscreen surfaces, the
 *         compositor will still be able to scan out directly from client
 *         surfaces.
 *       
 */
public enum WlOutputTransform {

  /**
   * no transform
   */
  NORMAL(0),
  /**
   * 90 degrees counter-clockwise
   */
  _90(1),
  /**
   * 180 degrees counter-clockwise
   */
  _180(2),
  /**
   * 270 degrees counter-clockwise
   */
  _270(3),
  /**
   * 180 degree flip around a vertical axis
   */
  FLIPPED(4),
  /**
   * flip and rotate 90 degrees counter-clockwise
   */
  FLIPPED_90(5),
  /**
   * flip and rotate 180 degrees counter-clockwise
   */
  FLIPPED_180(6),
  /**
   * flip and rotate 270 degrees counter-clockwise
   */
  FLIPPED_270(7);

  public final int value;

  private WlOutputTransform(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static WlOutputTransform of(int i) {
    return MAP.get(i);
  }
  private static final Map<Integer, WlOutputTransform> MAP = EnumUtil.buildEnumMap(org.freedesktop.wayland.shared.WlOutputTransform.class);
  static {
    EnumUtil.register(org.freedesktop.wayland.shared.WlOutputTransform.class);
  }
}
