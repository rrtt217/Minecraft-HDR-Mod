package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.freedesktop.wayland.util.Arguments;
import org.freedesktop.wayland.util.Interface;
import org.freedesktop.wayland.util.Message;
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
 * region interface
 * <p>
 *
 *       A region object describes an area.
 * <p>
 *       Region objects are used to describe the opaque and input
 *       regions of a surface.
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
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "iiii",
  functionName = "add",
  name = "add"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "iiii",
  functionName = "subtract",
  name = "subtract"
)

  },
  name = "wl_region",
  version = 7,
  events = {
  }
)
public class WlRegionProxy extends Proxy<WlRegionEvents> {

  public static final String INTERFACE_NAME = "wl_region";

  public WlRegionProxy(java.lang.foreign.MemorySegment pointer, WlRegionEvents implementation, int version) {
    super(pointer, implementation, version);
  }

  public WlRegionProxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy region
   * <p>
   *
   *         Destroy the region.  This will invalidate the object ID.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * add rectangle to region
   * <p>
   *
   *         Add the specified rectangle to the region.
   *       
   * @param x region-local x coordinate
   * @param y region-local y coordinate
   * @param width rectangle width
   * @param height rectangle height
   */
  public void add(int x, int y, int width, int height) {
    marshal(1, Arguments.create(4).set(0, x).set(1, y).set(2, width).set(3, height));
  }

  /**
   * subtract rectangle from region
   * <p>
   *
   *         Subtract the specified rectangle from the region.
   *       
   * @param x region-local x coordinate
   * @param y region-local y coordinate
   * @param width rectangle width
   * @param height rectangle height
   */
  public void subtract(int x, int y, int width, int height) {
    marshal(2, Arguments.create(4).set(0, x).set(1, y).set(2, width).set(3, height));
  }
}
