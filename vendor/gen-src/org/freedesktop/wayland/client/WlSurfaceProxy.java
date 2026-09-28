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
    org.freedesktop.wayland.client.WlBufferProxy.class,
    int.class,
    int.class
  },
  signature = "?oii",
  functionName = "attach",
  name = "attach"
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
  functionName = "damage",
  name = "damage"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WlCallbackProxy.class
  },
  signature = "n",
  functionName = "frame",
  name = "frame"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WlRegionProxy.class
  },
  signature = "?o",
  functionName = "setOpaqueRegion",
  name = "set_opaque_region"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WlRegionProxy.class
  },
  signature = "?o",
  functionName = "setInputRegion",
  name = "set_input_region"
)
,
    @Message(
  types = {
  },
  signature = "",
  functionName = "commit",
  name = "commit"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "2i",
  functionName = "setBufferTransform",
  name = "set_buffer_transform"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "3i",
  functionName = "setBufferScale",
  name = "set_buffer_scale"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "4iiii",
  functionName = "damageBuffer",
  name = "damage_buffer"
)
,
    @Message(
  types = {
    int.class,
    int.class
  },
  signature = "5ii",
  functionName = "offset",
  name = "offset"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WlCallbackProxy.class
  },
  signature = "7n",
  functionName = "getRelease",
  name = "get_release"
)

  },
  name = "wl_surface",
  version = 7,
  events = {
    @Message(
  types = {
    org.freedesktop.wayland.client.WlOutputProxy.class
  },
  signature = "o",
  functionName = "enter",
  name = "enter"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WlOutputProxy.class
  },
  signature = "o",
  functionName = "leave",
  name = "leave"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "6i",
  functionName = "preferredBufferScale",
  name = "preferred_buffer_scale"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "6u",
  functionName = "preferredBufferTransform",
  name = "preferred_buffer_transform"
)

  }
)
public class WlSurfaceProxy extends Proxy<WlSurfaceEvents> {

  public static final String INTERFACE_NAME = "wl_surface";

  public WlSurfaceProxy(java.lang.foreign.MemorySegment pointer, WlSurfaceEvents implementation, int version) {
    super(pointer, implementation, version);
  }

  public WlSurfaceProxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * delete surface
   * <p>
   *
   *         Deletes the surface and invalidates its object ID.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * set the surface contents
   * <p>
   *
   *         Set a buffer as the content of this surface.
   * <p>
   *         The new size of the surface is calculated based on the buffer
   *         size transformed by the inverse buffer_transform and the
   *         inverse buffer_scale. This means that at commit time the supplied
   *         buffer size must be an integer multiple of the buffer_scale. If
   *         that's not the case, an invalid_size error is sent.
   * <p>
   *         The x and y arguments specify the location of the new pending
   *         buffer's upper left corner, relative to the current buffer's upper
   *         left corner, in surface-local coordinates. In other words, the
   *         x and y, combined with the new surface size define in which
   *         directions the surface's size changes. Setting anything other than 0
   *         as x and y arguments is discouraged, and should instead be replaced
   *         with using the separate wl_surface.offset request.
   * <p>
   *         When the bound wl_surface version is 5 or higher, passing any
   *         non-zero x or y is a protocol violation, and will result in an
   *         'invalid_offset' error being raised. The x and y arguments are ignored
   *         and do not change the pending state. To achieve equivalent semantics,
   *         use wl_surface.offset.
   * <p>
   *         Surface contents are double-buffered state, see wl_surface.commit.
   * <p>
   *         The initial surface contents are void; there is no content.
   *         wl_surface.attach assigns the given wl_buffer as the pending
   *         wl_buffer. wl_surface.commit makes the pending wl_buffer the new
   *         surface contents, and the size of the surface becomes the size
   *         calculated from the wl_buffer, as described above. After commit,
   *         there is no pending buffer until the next attach.
   * <p>
   *         Committing a pending wl_buffer allows the compositor to read the
   *         pixels in the wl_buffer. The compositor may access the pixels at
   *         any time after the wl_surface.commit request. When the compositor
   *         will not access the pixels anymore, it will send the
   *         wl_buffer.release event. Only after receiving wl_buffer.release,
   *         the client may reuse the wl_buffer. A wl_buffer that has been
   *         attached and then replaced by another attach instead of committed
   *         will not receive a release event, and is not used by the
   *         compositor.
   * <p>
   *         If a pending wl_buffer has been committed to more than one wl_surface,
   *         the delivery of wl_buffer.release events becomes undefined. A well
   *         behaved client should not rely on wl_buffer.release events in this
   *         case. Instead, clients hitting this case should use
   *         wl_surface.get_release or use a protocol extension providing per-commit
   *         release notifications (if none of these options are available, a
   *         fallback can be implemented by creating multiple wl_buffer objects from
   *         the same backing storage).
   * <p>
   *         Destroying the wl_buffer after wl_buffer.release does not change
   *         the surface contents. Destroying the wl_buffer before wl_buffer.release
   *         is allowed as long as the underlying buffer storage isn't re-used (this
   *         can happen e.g. on client process termination). However, if the client
   *         destroys the wl_buffer before receiving the wl_buffer.release event and
   *         mutates the underlying buffer storage, the surface contents become
   *         undefined immediately.
   * <p>
   *         If wl_surface.attach is sent with a NULL wl_buffer, the
   *         following wl_surface.commit will remove the surface content.
   * <p>
   *         If a pending wl_buffer has been destroyed, the result is not specified.
   *         Many compositors are known to remove the surface content on the following
   *         wl_surface.commit, but this behaviour is not universal. Clients seeking to
   *         maximise compatibility should not destroy pending buffers and should
   *         ensure that they explicitly remove content from surfaces, even after
   *         destroying buffers.
   *       
   * @param buffer buffer of surface contents
   * @param x surface-local x coordinate
   * @param y surface-local y coordinate
   */
  public void attach(@Nullable WlBufferProxy buffer, int x, int y) {
    marshal(1, Arguments.create(3).set(0, buffer).set(1, x).set(2, y));
  }

  /**
   * mark part of the surface damaged
   * <p>
   *
   *         This request is used to describe the regions where the pending
   *         buffer is different from the current surface contents, and where
   *         the surface therefore needs to be repainted. The compositor
   *         ignores the parts of the damage that fall outside of the surface.
   * <p>
   *         Damage is double-buffered state, see wl_surface.commit.
   * <p>
   *         The damage rectangle is specified in surface-local coordinates,
   *         where x and y specify the upper left corner of the damage rectangle.
   * <p>
   *         The initial value for pending damage is empty: no damage.
   *         wl_surface.damage adds pending damage: the new pending damage
   *         is the union of old pending damage and the given rectangle.
   * <p>
   *         wl_surface.commit assigns pending damage as the current damage,
   *         and clears pending damage. The server will clear the current
   *         damage as it repaints the surface.
   * <p>
   *         Note! New clients should not use this request. Instead damage can be
   *         posted with wl_surface.damage_buffer which uses buffer coordinates
   *         instead of surface coordinates.
   *       
   * @param x surface-local x coordinate
   * @param y surface-local y coordinate
   * @param width width of damage rectangle
   * @param height height of damage rectangle
   */
  public void damage(int x, int y, int width, int height) {
    marshal(2, Arguments.create(4).set(0, x).set(1, y).set(2, width).set(3, height));
  }

  /**
   * request a frame throttling hint
   * <p>
   *
   *         Request a notification when it is a good time to start drawing a new
   *         frame, by creating a frame callback. This is useful for throttling
   *         redrawing operations, and driving animations.
   * <p>
   *         When a client is animating on a wl_surface, it can use the 'frame'
   *         request to get notified when it is a good time to draw and commit the
   *         next frame of animation. If the client commits an update earlier than
   *         that, it is likely that some updates will not make it to the display,
   *         and the client is wasting resources by drawing too often.
   * <p>
   *         The frame request will take effect on the next wl_surface.commit.
   *         The notification will only be posted for one frame unless
   *         requested again. For a wl_surface, the notifications are posted in
   *         the order the frame requests were committed.
   * <p>
   *         The server must send the notifications so that a client
   *         will not send excessive updates, while still allowing
   *         the highest possible update rate for clients that wait for the reply
   *         before drawing again. The server should give some time for the client
   *         to draw and commit after sending the frame callback events to let it
   *         hit the next output refresh.
   * <p>
   *         A server should avoid signaling the frame callbacks if the
   *         surface is not visible in any way, e.g. the surface is off-screen,
   *         or completely obscured by other opaque surfaces.
   * <p>
   *         The object returned by this request will be destroyed by the
   *         compositor after the callback is fired and as such the client must not
   *         attempt to use it after that point.
   * <p>
   *         The callback_data passed in the callback is the current time, in
   *         milliseconds, with an undefined base.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WlCallbackProxy frame(WlCallbackEvents implementation) {
    return marshalConstructor(3, implementation, getVersion(), org.freedesktop.wayland.client.WlCallbackProxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * set opaque region
   * <p>
   *
   *         This request sets the region of the surface that contains
   *         opaque content.
   * <p>
   *         The opaque region is an optimization hint for the compositor
   *         that lets it optimize the redrawing of content behind opaque
   *         regions.  Setting an opaque region is not required for correct
   *         behaviour, but marking transparent content as opaque will result
   *         in repaint artifacts.
   * <p>
   *         The opaque region is specified in surface-local coordinates.
   * <p>
   *         The compositor ignores the parts of the opaque region that fall
   *         outside of the surface.
   * <p>
   *         Opaque region is double-buffered state, see wl_surface.commit.
   * <p>
   *         wl_surface.set_opaque_region changes the pending opaque region.
   *         wl_surface.commit copies the pending region to the current region.
   *         Otherwise, the pending and current regions are never changed.
   * <p>
   *         The initial value for an opaque region is empty. Setting the pending
   *         opaque region has copy semantics, and the wl_region object can be
   *         destroyed immediately. A NULL wl_region causes the pending opaque
   *         region to be set to empty.
   *       
   * @param region opaque region of the surface
   */
  public void setOpaqueRegion(@Nullable WlRegionProxy region) {
    marshal(4, Arguments.create(1).set(0, region));
  }

  /**
   * set input region
   * <p>
   *
   *         This request sets the region of the surface that can receive
   *         pointer and touch events.
   * <p>
   *         Input events happening outside of this region will try the next
   *         surface in the server surface stack. The compositor ignores the
   *         parts of the input region that fall outside of the surface.
   * <p>
   *         The input region is specified in surface-local coordinates.
   * <p>
   *         Input region is double-buffered state, see wl_surface.commit.
   * <p>
   *         wl_surface.set_input_region changes the pending input region.
   *         wl_surface.commit copies the pending region to the current region.
   *         Otherwise the pending and current regions are never changed,
   *         except cursor and icon surfaces are special cases, see
   *         wl_pointer.set_cursor and wl_data_device.start_drag.
   * <p>
   *         The initial value for an input region is infinite. That means the
   *         whole surface will accept input. Setting the pending input region
   *         has copy semantics, and the wl_region object can be destroyed
   *         immediately. A NULL wl_region causes the input region to be set
   *         to infinite.
   *       
   * @param region input region of the surface
   */
  public void setInputRegion(@Nullable WlRegionProxy region) {
    marshal(5, Arguments.create(1).set(0, region));
  }

  /**
   * commit pending surface state
   * <p>
   *
   *         Surface state (input, opaque, and damage regions, attached buffers,
   *         etc.) is double-buffered. Protocol requests modify the pending state,
   *         as opposed to the active state in use by the compositor.
   * <p>
   *         All requests that need a commit to become effective are documented
   *         to affect double-buffered state.
   * <p>
   *         Other interfaces may add further double-buffered surface state.
   * <p>
   *         A commit request atomically creates a Content Update (CU) from the
   *         pending state, even if the pending state has not been touched. The
   *         content update is placed at the end of a per-surface queue until it
   *         becomes active. After commit, the new pending state is as documented for
   *         each related request.
   * <p>
   *         A CU is either a Desync Content Update (DCU) or a Sync Content Update
   *         (SCU). If the surface is effectively synchronized at the commit request,
   *         it is a SCU, otherwise a DCU.
   * <p>
   *         When a surface transitions from effectively synchronized to effectively
   *         desynchronized, all SCUs in its queue which are not reachable by any
   *         DCU become DCUs and dependency edges from outside the queue to these CUs
   *         are removed.
   * <p>
   *         See wl_subsurface for the definition of 'effectively synchronized' and
   *         'effectively desynchronized'.
   * <p>
   *         When a CU is placed in the queue, the CU has a dependency on the CU in
   *         front of it and to the SCU at end of the queue of every direct child
   *         surface if that SCU exists and does not have another dependent. This can
   *         form a directed acyclic graph of CUs with dependencies as edges.
   * <p>
   *         In addition to surface state, the CU can have constraints that must be
   *         satisfied before it can be applied. Other interfaces may add CU
   *         constraints.
   * <p>
   *         All DCUs which do not have a SCU in front of themselves in their queue,
   *         are candidates. If the graph that's reachable by a candidate does not
   *         have any unsatisfied constraints, the entire graph must be applied
   *         atomically.
   * <p>
   *         When a CU is applied, the wl_buffer is applied before all other state.
   *         This means that all coordinates in double-buffered state are relative to
   *         the newly attached wl_buffers, except for wl_surface.attach itself. If
   *         there is no newly attached wl_buffer, the coordinates are relative to
   *         the previous content update.
   *       
   */
  public void commit() {
    marshal(6);
  }

  /**
   * sets the buffer transformation
   * <p>
   *
   *         This request sets the transformation that the client has already applied
   *         to the content of the buffer. The accepted values for the transform
   *         parameter are the values for wl_output.transform.
   * <p>
   *         The compositor applies the inverse of this transformation whenever it
   *         uses the buffer contents.
   * <p>
   *         Buffer transform is double-buffered state, see wl_surface.commit.
   * <p>
   *         A newly created surface has its buffer transformation set to normal.
   * <p>
   *         wl_surface.set_buffer_transform changes the pending buffer
   *         transformation. wl_surface.commit copies the pending buffer
   *         transformation to the current one. Otherwise, the pending and current
   *         values are never changed.
   * <p>
   *         The purpose of this request is to allow clients to render content
   *         according to the output transform, thus permitting the compositor to
   *         use certain optimizations even if the display is rotated. Using
   *         hardware overlays and scanning out a client buffer for fullscreen
   *         surfaces are examples of such optimizations. Those optimizations are
   *         highly dependent on the compositor implementation, so the use of this
   *         request should be considered on a case-by-case basis.
   * <p>
   *         Note that if the transform value includes 90 or 270 degree rotation,
   *         the width of the buffer will become the surface height and the height
   *         of the buffer will become the surface width.
   * <p>
   *         If transform is not one of the values from the
   *         wl_output.transform enum the invalid_transform protocol error
   *         is raised.
   *       
   * @param transform transform for interpreting buffer contents
   */
  public void setBufferTransform(int transform) {
    if (getVersion() < 2) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 2 is required for this operation.");
    }
    marshal(7, Arguments.create(1).set(0, transform));
  }

  /**
   * sets the buffer scaling factor
   * <p>
   *
   *         This request sets an optional scaling factor on how the compositor
   *         interprets the contents of the buffer attached to the window.
   * <p>
   *         Buffer scale is double-buffered state, see wl_surface.commit.
   * <p>
   *         A newly created surface has its buffer scale set to 1.
   * <p>
   *         wl_surface.set_buffer_scale changes the pending buffer scale.
   *         wl_surface.commit copies the pending buffer scale to the current one.
   *         Otherwise, the pending and current values are never changed.
   * <p>
   *         The purpose of this request is to allow clients to supply higher
   *         resolution buffer data for use on high resolution outputs. It is
   *         intended that you pick the same buffer scale as the scale of the
   *         output that the surface is displayed on. This means the compositor
   *         can avoid scaling when rendering the surface on that output.
   * <p>
   *         Note that if the scale is larger than 1, then you have to attach
   *         a buffer that is larger (by a factor of scale in each dimension)
   *         than the desired surface size.
   * <p>
   *         If scale is not greater than 0 the invalid_scale protocol error is
   *         raised.
   *       
   * @param scale scale for interpreting buffer contents
   */
  public void setBufferScale(int scale) {
    if (getVersion() < 3) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 3 is required for this operation.");
    }
    marshal(8, Arguments.create(1).set(0, scale));
  }

  /**
   * mark part of the surface damaged using buffer coordinates
   * <p>
   *
   *         This request is used to describe the regions where the pending
   *         buffer is different from the current surface contents, and where
   *         the surface therefore needs to be repainted. The compositor
   *         ignores the parts of the damage that fall outside of the surface.
   * <p>
   *         Damage is double-buffered state, see wl_surface.commit.
   * <p>
   *         The damage rectangle is specified in buffer coordinates,
   *         where x and y specify the upper left corner of the damage rectangle.
   * <p>
   *         The initial value for pending damage is empty: no damage.
   *         wl_surface.damage_buffer adds pending damage: the new pending
   *         damage is the union of old pending damage and the given rectangle.
   * <p>
   *         wl_surface.commit assigns pending damage as the current damage,
   *         and clears pending damage. The server will clear the current
   *         damage as it repaints the surface.
   * <p>
   *         This request differs from wl_surface.damage in only one way - it
   *         takes damage in buffer coordinates instead of surface-local
   *         coordinates. While this generally is more intuitive than surface
   *         coordinates, it is especially desirable when using wp_viewport
   *         or when a drawing library (like EGL) is unaware of buffer scale
   *         and buffer transform.
   * <p>
   *         Note: Because buffer transformation changes and damage requests may
   *         be interleaved in the protocol stream, it is impossible to determine
   *         the actual mapping between surface and buffer damage until
   *         wl_surface.commit time. Therefore, compositors wishing to take both
   *         kinds of damage into account will have to accumulate damage from the
   *         two requests separately and only transform from one to the other
   *         after receiving the wl_surface.commit.
   *       
   * @param x buffer-local x coordinate
   * @param y buffer-local y coordinate
   * @param width width of damage rectangle
   * @param height height of damage rectangle
   */
  public void damageBuffer(int x, int y, int width, int height) {
    if (getVersion() < 4) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 4 is required for this operation.");
    }
    marshal(9, Arguments.create(4).set(0, x).set(1, y).set(2, width).set(3, height));
  }

  /**
   * set the surface contents offset
   * <p>
   *
   *         The x and y arguments specify the location of the new pending
   *         buffer's upper left corner, relative to the current buffer's upper
   *         left corner, in surface-local coordinates. In other words, the
   *         x and y, combined with the new surface size define in which
   *         directions the surface's size changes.
   * <p>
   *         The exact semantics of wl_surface.offset are role-specific. Refer to
   *         the documentation of specific roles for more information.
   * <p>
   *         Surface location offset is double-buffered state, see
   *         wl_surface.commit.
   * <p>
   *         This request is semantically equivalent to and the replaces the x and y
   *         arguments in the wl_surface.attach request in wl_surface versions prior
   *         to 5. See wl_surface.attach for details.
   *       
   * @param x surface-local x coordinate
   * @param y surface-local y coordinate
   */
  public void offset(int x, int y) {
    if (getVersion() < 5) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 5 is required for this operation.");
    }
    marshal(10, Arguments.create(2).set(0, x).set(1, y));
  }

  /**
   * get a release callback
   * <p>
   *
   *         Create a callback for the release of the buffer attached by the client
   *         with wl_surface.attach.
   * <p>
   *         The compositor will release the buffer when it has finished its usage of
   *         the underlying storage for the relevant commit. Once the client receives
   *         this event, and assuming the associated buffer is not pending release
   *         from other wl_surface.commit requests, the client can safely re-use the
   *         buffer.
   * <p>
   *         Release callbacks are double-buffered state, and will be associated
   *         with the pending buffer at wl_surface.commit time.
   * <p>
   *         The callback_data passed in the wl_callback.done event is unused and
   *         is always zero.
   * <p>
   *         Sending this request without attaching a non-null buffer in the same
   *         content update is a protocol error. The compositor will send the
   *         no_buffer error in this case.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WlCallbackProxy getRelease(WlCallbackEvents implementation) {
    if (getVersion() < 7) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 7 is required for this operation.");
    }
    return marshalConstructor(11, implementation, getVersion(), org.freedesktop.wayland.client.WlCallbackProxy.class, Arguments.create(1).set(0, 0));
  }
}
