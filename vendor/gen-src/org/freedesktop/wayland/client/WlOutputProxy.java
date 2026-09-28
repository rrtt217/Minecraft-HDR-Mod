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
 * compositor output region
 * <p>
 *
 *       An output describes part of the compositor geometry.  The
 *       compositor works in the 'compositor coordinate system' and an
 *       output corresponds to a rectangular area in that space that is
 *       actually visible.  This typically corresponds to a monitor that
 *       displays part of the compositor space.  This object is published
 *       as global during start up, or when a monitor is hotplugged.
 *     
 */
@Interface(
  methods = {
    @Message(
  types = {
  },
  signature = "3",
  functionName = "release",
  name = "release"
)

  },
  name = "wl_output",
  version = 4,
  events = {
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    java.lang.String.class,
    java.lang.String.class,
    int.class
  },
  signature = "iiiiissi",
  functionName = "geometry",
  name = "geometry"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "uiii",
  functionName = "mode",
  name = "mode"
)
,
    @Message(
  types = {
  },
  signature = "2",
  functionName = "done",
  name = "done"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "2i",
  functionName = "scale",
  name = "scale"
)
,
    @Message(
  types = {
    java.lang.String.class
  },
  signature = "4s",
  functionName = "name",
  name = "name"
)
,
    @Message(
  types = {
    java.lang.String.class
  },
  signature = "4s",
  functionName = "description",
  name = "description"
)

  }
)
public class WlOutputProxy extends Proxy<WlOutputEvents> {

  public static final String INTERFACE_NAME = "wl_output";

  public WlOutputProxy(java.lang.foreign.MemorySegment pointer, WlOutputEvents implementation, int version) {
    super(pointer, implementation, version);
  }

  public WlOutputProxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * release the output object
   * <p>
   *
   *         Using this request a client can tell the server that it is not going to
   *         use the output object anymore.
   *       
   */
  public void release() {
    if (getVersion() < 3) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 3 is required for this operation.");
    }
    marshal(0);
  }
}
