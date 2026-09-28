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
 * holder of image description parameters
 * <p>
 *
 *       This type of object is used for collecting all the parameters required
 *       to create a wp_image_description_v1 object. A complete set of required
 *       parameters consists of these properties:
 *       - transfer characteristic function (tf)
 *       - chromaticities of primaries and white point (primary color volume)
 * <p>
 *       The following properties are optional and have a well-defined default
 *       if not explicitly set:
 *       - primary color volume luminance range
 *       - reference white luminance level
 *       - mastering display primaries and white point (target color volume)
 *       - mastering luminance range
 * <p>
 *       The following properties are optional and will be ignored
 *       if not explicitly set:
 *       - maximum content light level
 *       - maximum frame-average light level
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
 *       A viewer, who is viewing the display defined by the resulting image
 *       description (the viewing environment included), is assumed to be fully
 *       adapted to the primary color volume's white point.
 * <p>
 *       Any of the following conditions will cause the colorimetry of a pixel
 *       to become undefined:
 *       - Values outside of the defined range of the transfer characteristic.
 *       - Tristimulus that exceeds the target color volume.
 *       - If extended_target_volume is not supported: tristimulus that exceeds
 *       the primary color volume.
 * <p>
 *       The closest correspondence to an image description created through this
 *       interface is the Display class of profiles in ICC.
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
    int.class
  },
  signature = "u",
  functionName = "setTfNamed",
  name = "set_tf_named"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "setTfPower",
  name = "set_tf_power"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "setPrimariesNamed",
  name = "set_primaries_named"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "iiiiiiii",
  functionName = "setPrimaries",
  name = "set_primaries"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class
  },
  signature = "uuu",
  functionName = "setLuminances",
  name = "set_luminances"
)
,
    @Message(
  types = {
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class,
    int.class
  },
  signature = "iiiiiiii",
  functionName = "setMasteringDisplayPrimaries",
  name = "set_mastering_display_primaries"
)
,
    @Message(
  types = {
    int.class,
    int.class
  },
  signature = "uu",
  functionName = "setMasteringLuminance",
  name = "set_mastering_luminance"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "setMaxCll",
  name = "set_max_cll"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "setMaxFall",
  name = "set_max_fall"
)

  },
  name = "wp_image_description_creator_params_v1",
  version = 3,
  events = {
  }
)
public class WpImageDescriptionCreatorParamsV1Proxy extends Proxy<WpImageDescriptionCreatorParamsV1Events> {

  public static final String INTERFACE_NAME = "wp_image_description_creator_params_v1";

  public WpImageDescriptionCreatorParamsV1Proxy(java.lang.foreign.MemorySegment pointer, WpImageDescriptionCreatorParamsV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpImageDescriptionCreatorParamsV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * Create the image description object using params
   * <p>
   *
   *         Create an image description object based on the parameters previously
   *         set on this object.
   * <p>
   *         The completeness of the parameter set is verified. If the set is not
   *         complete, the protocol error incomplete_set is raised. For the
   *         definition of a complete set, see the description of this interface.
   * <p>
   *         When both max_cll and max_fall are set, max_fall must be less or equal
   *         to max_cll otherwise the invalid_luminance protocol error is raised.
   * <p>
   *         In version 1, these following conditions also result in the
   *         invalid_luminance protocol error. Version 2 and later do not have this
   *         requirement.
   *         - When max_cll is set, it must be greater than min L and less or equal
   *           to max L of the mastering luminance range.
   *         - When max_fall is set, it must be greater than min L and less or equal
   *           to max L of the mastering luminance range.
   * <p>
   *         If the particular combination of the parameter set is not supported
   *         by the compositor, the resulting image description object shall
   *         immediately deliver the wp_image_description_v1.failed event with the
   *         'unsupported' cause. If a valid image description was created from the
   *         parameter set, the wp_image_description_v1.ready event will eventually
   *         be sent instead.
   * <p>
   *         This request destroys the wp_image_description_creator_params_v1
   *         object.
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
   * named transfer characteristic
   * <p>
   *
   *         Sets the transfer characteristic using explicitly enumerated named
   *         functions.
   * <p>
   *         When the resulting image description is attached to an image, the
   *         content should be decoded according to the industry standard
   *         practices for the transfer characteristic.
   * <p>
   *         Only names advertised with wp_color_manager_v1 event supported_tf_named
   *         are allowed. Other values shall raise the protocol error invalid_tf.
   * <p>
   *         If transfer characteristic has already been set on this object, the
   *         protocol error already_set is raised.
   *       
   * @param tf named transfer function
   */
  public void setTfNamed(int tf) {
    marshal(1, Arguments.create(1).set(0, tf));
  }

  /**
   * transfer characteristic as a power curve
   * <p>
   *
   *         Sets the color component transfer characteristic to a power curve with
   *         the given exponent. Negative values are handled by mirroring the
   *         positive half of the curve through the origin. The valid domain and
   *         range of the curve are all finite real numbers. This curve represents
   *         the conversion from electrical to optical color channel values.
   * <p>
   *         The curve exponent shall be multiplied by 10000 to get the argument eexp
   *         value to carry the precision of 4 decimals.
   * <p>
   *         The curve exponent must be at least 1.0 and at most 10.0. Otherwise the
   *         protocol error invalid_tf is raised.
   * <p>
   *         If transfer characteristic has already been set on this object, the
   *         protocol error already_set is raised.
   * <p>
   *         This request can be used when the compositor advertises
   *         wp_color_manager_v1.feature.set_tf_power. Otherwise this request raises
   *         the protocol error unsupported_feature.
   *       
   * @param eexp the exponent * 10000
   */
  public void setTfPower(int eexp) {
    marshal(2, Arguments.create(1).set(0, eexp));
  }

  /**
   * named primaries
   * <p>
   *
   *         Sets the color primaries and white point using explicitly named sets.
   *         This describes the primary color volume which is the basis for color
   *         value encoding.
   * <p>
   *         Only names advertised with wp_color_manager_v1 event
   *         supported_primaries_named are allowed. Other values shall raise the
   *         protocol error invalid_primaries_named.
   * <p>
   *         If primaries have already been set on this object, the protocol error
   *         already_set is raised.
   *       
   * @param primaries named primaries
   */
  public void setPrimariesNamed(int primaries) {
    marshal(3, Arguments.create(1).set(0, primaries));
  }

  /**
   * primaries as chromaticity coordinates
   * <p>
   *
   *         Sets the color primaries and white point using CIE 1931 xy chromaticity
   *         coordinates. This describes the primary color volume which is the basis
   *         for color value encoding.
   * <p>
   *         Each coordinate value is multiplied by 1 million to get the argument
   *         value to carry precision of 6 decimals.
   * <p>
   *         If primaries have already been set on this object, the protocol error
   *         already_set is raised.
   * <p>
   *         This request can be used if the compositor advertises
   *         wp_color_manager_v1.feature.set_primaries. Otherwise this request raises
   *         the protocol error unsupported_feature.
   *       
   * @param rX Red x * 1M
   * @param rY Red y * 1M
   * @param gX Green x * 1M
   * @param gY Green y * 1M
   * @param bX Blue x * 1M
   * @param bY Blue y * 1M
   * @param wX White x * 1M
   * @param wY White y * 1M
   */
  public void setPrimaries(int rX, int rY, int gX, int gY, int bX, int bY, int wX, int wY) {
    marshal(4, Arguments.create(8).set(0, rX).set(1, rY).set(2, gX).set(3, gY).set(4, bX).set(5, bY).set(6, wX).set(7, wY));
  }

  /**
   * primary color volume luminance range and reference white
   * <p>
   *
   *         Sets the primary color volume luminance range and the reference white
   *         luminance level. These values include the minimum display emission, but
   *         not external flare. The minimum display emission is assumed to have
   *         the chromaticity of the primary color volume white point.
   * <p>
   *         The default luminances from
   *         https://www.color.org/chardata/rgb/srgb.xalter are
   *         - primary color volume minimum: 0.2 cd/m²
   *         - primary color volume maximum: 80 cd/m²
   *         - reference white: 80 cd/m²
   * <p>
   *         Setting a named transfer characteristic can imply other default
   *         luminances.
   * <p>
   *         The default luminances get overwritten when this request is used.
   *         With transfer_function.st2084_pq the given 'max_lum' value is ignored,
   *         and 'max_lum' is taken as 'min_lum' + 10000 cd/m².
   * <p>
   *         'min_lum' and 'max_lum' specify the minimum and maximum luminances of
   *         the primary color volume as reproduced by the targeted display.
   * <p>
   *         'reference_lum' specifies the luminance of the reference white as
   *         reproduced by the targeted display, and reflects the targeted viewing
   *         environment.
   * <p>
   *         Compositors should make sure that all content is anchored, meaning that
   *         an input signal level of 'reference_lum' on one image description and
   *         another input signal level of 'reference_lum' on another image
   *         description should produce the same output level, even though the
   *         'reference_lum' on both image representations can be different.
   * <p>
   *         'reference_lum' may be higher than 'max_lum'. In that case reaching
   *         the reference white output level in image content requires the
   *         'extended_target_volume' feature support.
   * <p>
   *         If 'max_lum' or 'reference_lum' are less than or equal to 'min_lum',
   *         the protocol error invalid_luminance is raised.
   * <p>
   *         The minimum luminance is multiplied by 10000 to get the argument
   *         'min_lum' value and carries precision of 4 decimals. The maximum
   *         luminance and reference white luminance values are unscaled.
   * <p>
   *         If the primary color volume luminance range and the reference white
   *         luminance level have already been set on this object, the protocol error
   *         already_set is raised.
   * <p>
   *         This request can be used if the compositor advertises
   *         wp_color_manager_v1.feature.set_luminances. Otherwise this request
   *         raises the protocol error unsupported_feature.
   *       
   * @param minLum minimum luminance (cd/m²) * 10000
   * @param maxLum maximum luminance (cd/m²)
   * @param referenceLum reference white luminance (cd/m²)
   */
  public void setLuminances(int minLum, int maxLum, int referenceLum) {
    marshal(5, Arguments.create(3).set(0, minLum).set(1, maxLum).set(2, referenceLum));
  }

  /**
   * mastering display primaries as chromaticity coordinates
   * <p>
   *
   *         Provides the color primaries and white point of the mastering display
   *         using CIE 1931 xy chromaticity coordinates. This is compatible with the
   *         SMPTE ST 2086 definition of HDR static metadata.
   * <p>
   *         The mastering display primaries and mastering display luminances define
   *         the target color volume.
   * <p>
   *         If mastering display primaries are not explicitly set, the target color
   *         volume is assumed to have the same primaries as the primary color volume.
   * <p>
   *         The target color volume is defined by all tristimulus values between 0.0
   *         and 1.0 (inclusive) of the color space defined by the given mastering
   *         display primaries and white point. The colorimetry is identical between
   *         the container color space and the mastering display color space,
   *         including that no chromatic adaptation is applied even if the white
   *         points differ.
   * <p>
   *         The target color volume can exceed the primary color volume to allow for
   *         a greater color volume with an existing color space definition (for
   *         example scRGB). It can be smaller than the primary color volume to
   *         minimize gamut and tone mapping distances for big color spaces (HDR
   *         metadata).
   * <p>
   *         To make use of the entire target color volume a suitable pixel format
   *         has to be chosen (e.g. floating point to exceed the primary color
   *         volume, or abusing limited quantization range as with xvYCC).
   * <p>
   *         Each coordinate value is multiplied by 1 million to get the argument
   *         value to carry precision of 6 decimals.
   * <p>
   *         If mastering display primaries have already been set on this object, the
   *         protocol error already_set is raised.
   * <p>
   *         This request can be used if the compositor advertises
   *         wp_color_manager_v1.feature.set_mastering_display_primaries. Otherwise
   *         this request raises the protocol error unsupported_feature. The
   *         advertisement implies support only for target color volumes fully
   *         contained within the primary color volume.
   * <p>
   *         If a compositor additionally supports target color volume exceeding the
   *         primary color volume, it must advertise
   *         wp_color_manager_v1.feature.extended_target_volume. If a client uses
   *         target color volume exceeding the primary color volume and the
   *         compositor does not support it, the result is implementation defined.
   *         Compositors are recommended to detect this case and fail the image
   *         description gracefully, but it may as well result in color artifacts.
   *       
   * @param rX Red x * 1M
   * @param rY Red y * 1M
   * @param gX Green x * 1M
   * @param gY Green y * 1M
   * @param bX Blue x * 1M
   * @param bY Blue y * 1M
   * @param wX White x * 1M
   * @param wY White y * 1M
   */
  public void setMasteringDisplayPrimaries(int rX, int rY, int gX, int gY, int bX, int bY, int wX, int wY) {
    marshal(6, Arguments.create(8).set(0, rX).set(1, rY).set(2, gX).set(3, gY).set(4, bX).set(5, bY).set(6, wX).set(7, wY));
  }

  /**
   * display mastering luminance range
   * <p>
   *
   *         Sets the luminance range that was used during the content mastering
   *         process as the minimum and maximum absolute luminance L. These values
   *         include the minimum display emission and ambient flare luminances,
   *         assumed to be optically additive and have the chromaticity of the
   *         primary color volume white point. This should be
   *         compatible with the SMPTE ST 2086 definition of HDR static metadata.
   * <p>
   *         The mastering display primaries and mastering display luminances define
   *         the target color volume.
   * <p>
   *         If mastering luminances are not explicitly set, the target color volume
   *         is assumed to have the same min and max luminances as the primary color
   *         volume.
   * <p>
   *         If max L is less than or equal to min L, the protocol error
   *         invalid_luminance is raised.
   * <p>
   *         Min L value is multiplied by 10000 to get the argument min_lum value
   *         and carry precision of 4 decimals. Max L value is unscaled for max_lum.
   * <p>
   *         This request can be used if the compositor advertises
   *         wp_color_manager_v1.feature.set_mastering_display_primaries. Otherwise
   *         this request raises the protocol error unsupported_feature. The
   *         advertisement implies support only for target color volumes fully
   *         contained within the primary color volume.
   * <p>
   *         If a compositor additionally supports target color volume exceeding the
   *         primary color volume, it must advertise
   *         wp_color_manager_v1.feature.extended_target_volume. If a client uses
   *         target color volume exceeding the primary color volume and the
   *         compositor does not support it, the result is implementation defined.
   *         Compositors are recommended to detect this case and fail the image
   *         description gracefully, but it may as well result in color artifacts.
   *       
   * @param minLum min L (cd/m²) * 10000
   * @param maxLum max L (cd/m²)
   */
  public void setMasteringLuminance(int minLum, int maxLum) {
    marshal(7, Arguments.create(2).set(0, minLum).set(1, maxLum));
  }

  /**
   * maximum content light level
   * <p>
   *
   *         Sets the maximum content light level (max_cll) as defined by CTA-861-H.
   * <p>
   *         max_cll is undefined by default.
   *       
   * @param maxCll Maximum content light level (cd/m²)
   */
  public void setMaxCll(int maxCll) {
    marshal(8, Arguments.create(1).set(0, maxCll));
  }

  /**
   * maximum frame-average light level
   * <p>
   *
   *         Sets the maximum frame-average light level (max_fall) as defined by
   *         CTA-861-H.
   * <p>
   *         max_fall is undefined by default.
   *       
   * @param maxFall Maximum frame-average light level (cd/m²)
   */
  public void setMaxFall(int maxFall) {
    marshal(9, Arguments.create(1).set(0, maxFall));
  }
}
