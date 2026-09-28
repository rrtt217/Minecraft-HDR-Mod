package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * Colorimetric image description
 * <p>
 *
 *       An image description carries information about the pixel color encoding
 *       and its intended display and viewing environment. The image description is
 *       attached to a wl_surface via
 *       wp_color_management_surface_v1.set_image_description. A compositor can use
 *       this information to decode pixel values into colorimetrically meaningful
 *       quantities, which allows the compositor to transform the surface contents
 *       to become suitable for various displays and viewing environments.
 * <p>
 *       Note, that the wp_image_description_v1 object is not ready to be used
 *       immediately after creation. The object eventually delivers either the
 *       'ready' or the 'failed' event, specified in all requests creating it. The
 *       object is deemed "ready" after receiving the 'ready' event.
 * <p>
 *       An object which is not ready is illegal to use, it can only be destroyed.
 *       Any other request in this interface shall result in the 'not_ready'
 *       protocol error. Attempts to use an object which is not ready through other
 *       interfaces shall raise protocol errors defined there.
 * <p>
 *       Once created and regardless of how it was created, a
 *       wp_image_description_v1 object always refers to one fixed image
 *       description. It cannot change after creation.
 *     
 */
public interface WpImageDescriptionV1EventsV3 extends WpImageDescriptionV1EventsV2 {
  int VERSION = 3;

  /**
   * graceful error on creating the image description
   * <p>
   *
   *         If creating a wp_image_description_v1 object fails for a reason that is
   *         not defined as a protocol error, this event is sent.
   * <p>
   *         The requests that create image description objects define whether and
   *         when this can occur. Only such creation requests can trigger this event.
   *         This event cannot be triggered after the image description was
   *         successfully formed.
   * <p>
   *         Once this event has been sent, the wp_image_description_v1 object will
   *         never become ready and it can only be destroyed.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param cause generic reason
   * @param msg ad hoc human-readable explanation
   */
  public void failed(WpImageDescriptionV1Proxy emitter, int cause, @Nonnull String msg);

  /**
   * the object is ready to be used (32-bit)
   * <p>
   *
   *         Starting from interface version 2, the 'ready2' event is sent instead
   *         of this event.
   * <p>
   *         For the definition of this event, see the 'ready2' event. The
   *         difference to this event is as follows.
   * <p>
   *         The id number is valid only as long as the protocol object is alive. If
   *         all protocol objects referring to the same image description record are
   *         destroyed, the id number may be recycled for a different image
   *         description record.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param identity the 32-bit image description id number
   */
  public void ready(WpImageDescriptionV1Proxy emitter, int identity);

  /**
   * the object is ready to be used
   * <p>
   *
   *         Once this event has been sent, the wp_image_description_v1 object is
   *         deemed "ready". Ready objects can be used to send requests and can be
   *         used through other interfaces.
   * <p>
   *         Every ready wp_image_description_v1 protocol object refers to an
   *         underlying image description record in the compositor. Multiple protocol
   *         objects may end up referring to the same record. Clients may identify
   *         these "copies" by comparing their id numbers: if the numbers from two
   *         protocol objects are identical, the protocol objects refer to the same
   *         image description record. Two different image description records
   *         cannot have the same id number simultaneously. The id number does not
   *         change during the lifetime of the image description record.
   * <p>
   *         Image description id number is not a protocol object id. Zero is
   *         reserved as an invalid id number. It shall not be possible for a client
   *         to refer to an image description by its id number in protocol. The id
   *         numbers might not be portable between Wayland connections. A compositor
   *         shall not send an invalid id number.
   * <p>
   *         Compositors must not recycle image description id numbers.
   * <p>
   *         This identity allows clients to de-duplicate image description records
   *         and avoid get_information request if they already have the image
   *         description information.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param identityHi high 32 bits of the 64-bit image description id number
   * @param identityLo low 32 bits of the 64-bit image description id number
   */
  public void ready2(WpImageDescriptionV1Proxy emitter, int identityHi, int identityLo);
}
