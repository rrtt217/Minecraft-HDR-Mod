package org.freedesktop.wayland.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.freedesktop.wayland.util.Arguments;
import org.freedesktop.wayland.util.Interface;
import org.freedesktop.wayland.util.Message;
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
 * holder of image description ICC information
 * <p>
 *
 *       This type of object is used for collecting all the information required
 *       to create a wp_image_description_v1 object from an ICC file. A complete
 *       set of required parameters consists of these properties:
 *       - ICC file
 * <p>
 *       Each required property must be set exactly once if the client is to create
 *       an image description. The set requests verify that a property was not
 *       already set. The create request verifies that all required properties are
 *       set. There may be several alternative requests for setting each property,
 *       and in that case the client must choose one of them.
 * <p>
 *       Once all properties have been set, the create request must be used to
 *       create the image description object, destroying the creator in the
 *       process.
 * <p>
 *       The link between a pixel value (a device value in ICC) and its respective
 *       colorimetry is defined by the details of the particular ICC profile.
 *       Those details also determine when colorimetry becomes undefined.
 *     
 */
@Interface(
  methods = {
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class
  },
  signature = "n",
  functionName = "create",
  name = "create"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class
  },
  signature = "huu",
  functionName = "setIccFile",
  name = "set_icc_file"
)

  },
  name = "wp_image_description_creator_icc_v1",
  version = 3,
  events = {
  }
)
public class WpImageDescriptionCreatorIccV1Proxy extends Proxy<WpImageDescriptionCreatorIccV1Events> {

  public static final String INTERFACE_NAME = "wp_image_description_creator_icc_v1";

  public WpImageDescriptionCreatorIccV1Proxy(java.lang.foreign.MemorySegment pointer, WpImageDescriptionCreatorIccV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpImageDescriptionCreatorIccV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * Create the image description object from ICC data
   * <p>
   *
   *         Create an image description object based on the ICC information
   *         previously set on this object. A compositor must parse the ICC data in
   *         some undefined but finite amount of time.
   * <p>
   *         The completeness of the parameter set is verified. If the set is not
   *         complete, the protocol error incomplete_set is raised. For the
   *         definition of a complete set, see the description of this interface.
   * <p>
   *         If the particular combination of the information is not supported
   *         by the compositor, the resulting image description object shall
   *         immediately deliver the wp_image_description_v1.failed event with the
   *         'unsupported' cause. If a valid image description was created from the
   *         information, the wp_image_description_v1.ready event will eventually
   *         be sent instead.
   * <p>
   *         This request destroys the wp_image_description_creator_icc_v1 object.
   * <p>
   *         The resulting image description object does not allow get_information
   *         request.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionV1Proxy create(WpImageDescriptionV1Events implementation) {
    return marshalConstructor(0, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * set the ICC profile file
   * <p>
   *
   *         Sets the ICC profile file to be used as the basis of the image
   *         description.
   * <p>
   *         The data shall be found through the given fd at the given offset, having
   *         the given length. The fd must be seekable and readable. Violating these
   *         requirements raises the bad_fd protocol error.
   * <p>
   *         If reading the data fails due to an error independent of the client, the
   *         compositor shall send the wp_image_description_v1.failed event on the
   *         created wp_image_description_v1 with the 'operating_system' cause.
   * <p>
   *         The maximum size of the ICC profile is 32 MB. If length is greater than
   *         that or zero, the protocol error bad_size is raised. If offset + length
   *         exceeds the file size, the protocol error out_of_file is raised.
   * <p>
   *         A compositor may read the file at any time starting from this request
   *         and only until whichever happens first:
   *         - If create request was issued, the wp_image_description_v1 object
   *           delivers either failed or ready event; or
   *         - if create request was not issued, this
   *           wp_image_description_creator_icc_v1 object is destroyed.
   * <p>
   *         A compositor shall not modify the contents of the file, and the fd may
   *         be sealed for writes and size changes. The client must ensure to its
   *         best ability that the data does not change while the compositor is
   *         reading it.
   * <p>
   *         The data must represent a valid ICC profile. The ICC profile version
   *         must be 2 or 4, it must be a 3 channel profile and the class must be
   *         Display or ColorSpace. Violating these requirements will not result in a
   *         protocol error, but will eventually send the
   *         wp_image_description_v1.failed event on the created
   *         wp_image_description_v1 with the 'unsupported' cause.
   * <p>
   *         See the International Color Consortium specification ICC.1:2022 for more
   *         details about ICC profiles.
   * <p>
   *         If ICC file has already been set on this object, the protocol error
   *         already_set is raised.
   *       
   * @param iccProfile ICC profile
   * @param offset byte offset in fd to start of ICC data
   * @param length length of ICC data in bytes
   */
  public void setIccFile(int iccProfile, int offset, int length) {
    marshal(1, Arguments.create(3).set(0, iccProfile).set(1, offset).set(2, length));
  }
}
