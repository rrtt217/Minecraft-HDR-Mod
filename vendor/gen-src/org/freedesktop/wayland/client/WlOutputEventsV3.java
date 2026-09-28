package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public interface WlOutputEventsV3 extends WlOutputEventsV2 {
  int VERSION = 3;

  /**
   * properties of the output
   * <p>
   *
   *         The geometry event describes geometric properties of the output.
   *         The event is sent when binding to the output object and whenever
   *         any of the properties change.
   * <p>
   *         The physical size can be set to zero if it doesn't make sense for this
   *         output (e.g. for projectors or virtual outputs).
   * <p>
   *         The geometry event will be followed by a done event (starting from
   *         version 2).
   * <p>
   *         Clients should use wl_surface.preferred_buffer_transform instead of the
   *         transform advertised by this event to find the preferred buffer
   *         transform to use for a surface.
   * <p>
   *         Note: wl_output only advertises partial information about the output
   *         position and identification. Some compositors, for instance those not
   *         implementing a desktop-style output layout or those exposing virtual
   *         outputs, might fake this information. Instead of using x and y, clients
   *         should use xdg_output.logical_position. Instead of using make and model,
   *         clients should use name and description.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param x x position within the global compositor space
   * @param y y position within the global compositor space
   * @param physicalWidth width in millimeters of the output
   * @param physicalHeight height in millimeters of the output
   * @param subpixel subpixel orientation of the output
   * @param make textual description of the manufacturer
   * @param model textual description of the model
   * @param transform additional transformation applied to buffer contents during presentation
   */
  public void geometry(WlOutputProxy emitter, int x, int y, int physicalWidth, int physicalHeight, int subpixel, @Nonnull String make, @Nonnull String model, int transform);

  /**
   * advertise available modes for the output
   * <p>
   *
   *         The mode event describes an available mode for the output.
   * <p>
   *         The event is sent when binding to the output object and there
   *         will always be one mode, the current mode.  The event is sent
   *         again if an output changes mode, for the mode that is now
   *         current.  In other words, the current mode is always the last
   *         mode that was received with the current flag set.
   * <p>
   *         Non-current modes are deprecated. A compositor can decide to only
   *         advertise the current mode and never send other modes. Clients
   *         should not rely on non-current modes.
   * <p>
   *         The size of a mode is given in physical hardware units of
   *         the output device. This is not necessarily the same as
   *         the output size in the global compositor space. For instance,
   *         the output may be scaled, as described in wl_output.scale,
   *         or transformed, as described in wl_output.transform. Clients
   *         willing to retrieve the output size in the global compositor
   *         space should use xdg_output.logical_size instead.
   * <p>
   *         The vertical refresh rate can be set to zero if it doesn't make
   *         sense for this output (e.g. for virtual outputs).
   * <p>
   *         The mode event will be followed by a done event (starting from
   *         version 2).
   * <p>
   *         Clients should not use the refresh rate to schedule frames. Instead,
   *         they should use the wl_surface.frame event or the presentation-time
   *         protocol.
   * <p>
   *         Note: this information is not always meaningful for all outputs. Some
   *         compositors, such as those exposing virtual outputs, might fake the
   *         refresh rate or the size.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param flags bitfield of mode flags
   * @param width width of the mode in hardware units
   * @param height height of the mode in hardware units
   * @param refresh vertical refresh rate in mHz
   */
  public void mode(WlOutputProxy emitter, int flags, int width, int height, int refresh);

  /**
   * sent all information about output
   * <p>
   *
   *         This event is sent after all other properties have been
   *         sent after binding to the output object and after any
   *         other property changes done after that. This allows
   *         changes to the output properties to be seen as
   *         atomic, even if they happen via multiple events.
   *       
   * @param emitter The protocol object that emitted the event.
   */
  public void done(WlOutputProxy emitter);

  /**
   * output scaling properties
   * <p>
   *
   *         This event contains scaling geometry information
   *         that is not in the geometry event. It may be sent after
   *         binding the output object or if the output scale changes
   *         later. The compositor will emit a non-zero, positive
   *         value for scale. If it is not sent, the client should
   *         assume a scale of 1.
   * <p>
   *         A scale larger than 1 means that the compositor will
   *         automatically scale surface buffers by this amount
   *         when rendering. This is used for very high resolution
   *         displays where applications rendering at the native
   *         resolution would be too small to be legible.
   * <p>
   *         Clients should use wl_surface.preferred_buffer_scale
   *         instead of this event to find the preferred buffer
   *         scale to use for a surface.
   * <p>
   *         The scale event will be followed by a done event.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param factor scaling factor of output
   */
  public void scale(WlOutputProxy emitter, int factor);
}
