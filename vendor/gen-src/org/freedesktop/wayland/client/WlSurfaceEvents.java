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
 * an onscreen surface
 * <p>
 *
 *       A surface is a rectangular area that may be displayed on zero
 *       or more outputs, and shown any number of times at the compositor's
 *       discretion. They can present wl_buffers, receive user input, and
 *       define a local coordinate system.
 * <p>
 *       The size of a surface (and relative positions on it) is described
 *       in surface-local coordinates, which may differ from the buffer
 *       coordinates of the pixel content, in case a buffer_transform
 *       or a buffer_scale is used.
 * <p>
 *       A surface without a "role" is fairly useless: a compositor does
 *       not know where, when or how to present it. The role is the
 *       purpose of a wl_surface. Examples of roles are a cursor for a
 *       pointer (as set by wl_pointer.set_cursor), a drag icon
 *       (wl_data_device.start_drag), a sub-surface
 *       (wl_subcompositor.get_subsurface), and a window as defined by a
 *       shell protocol (e.g. wl_shell.get_shell_surface).
 * <p>
 *       A surface can have only one role at a time. Initially a
 *       wl_surface does not have a role. Once a wl_surface is given a
 *       role, it is set permanently for the whole lifetime of the
 *       wl_surface object. Giving the current role again is allowed,
 *       unless explicitly forbidden by the relevant interface
 *       specification.
 * <p>
 *       Surface roles are given by requests in other interfaces such as
 *       wl_pointer.set_cursor. The request should explicitly mention
 *       that this request gives a role to a wl_surface. Often, this
 *       request also creates a new protocol object that represents the
 *       role and adds additional functionality to wl_surface. When a
 *       client wants to destroy a wl_surface, they must destroy this role
 *       object before the wl_surface, otherwise a defunct_role_object error is
 *       sent.
 * <p>
 *       Destroying the role object does not remove the role from the
 *       wl_surface, but it may stop the wl_surface from "playing the role".
 *       For instance, if a wl_subsurface object is destroyed, the wl_surface
 *       it was created for will be unmapped and forget its position and
 *       z-order. It is allowed to create a wl_subsurface for the same
 *       wl_surface again, but it is not allowed to use the wl_surface as
 *       a cursor (cursor is a different role than sub-surface, and role
 *       switching is not allowed).
 *     
 */
public interface WlSurfaceEvents {
  int VERSION = 1;

  /**
   * surface enters an output
   * <p>
   *
   *         This is emitted whenever a surface's creation, movement, or resizing
   *         results in some part of it being within the scanout region of an
   *         output.
   * <p>
   *         Note that a surface may be overlapping with zero or more outputs.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param output output entered by the surface
   */
  public void enter(WlSurfaceProxy emitter, @Nonnull WlOutputProxy output);

  /**
   * surface leaves an output
   * <p>
   *
   *         This is emitted whenever a surface's creation, movement, or resizing
   *         results in it no longer having any part of it within the scanout region
   *         of an output.
   * <p>
   *         Clients should not use the number of outputs the surface is on for frame
   *         throttling purposes. The surface might be hidden even if no leave event
   *         has been sent, and the compositor might expect new surface content
   *         updates even if no enter event has been sent. The frame event should be
   *         used instead.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param output output left by the surface
   */
  public void leave(WlSurfaceProxy emitter, @Nonnull WlOutputProxy output);
}
