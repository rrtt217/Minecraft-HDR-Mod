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
 * Colorimetric image description information
 * <p>
 *
 *       Sends all matching events describing an image description object exactly
 *       once and finally sends the 'done' event.
 * <p>
 *       This means
 *       - if the image description is parametric, it must send
 *         - primaries
 *         - named_primaries, if applicable
 *         - at least one of tf_power and tf_named, as applicable
 *         - luminances
 *         - target_primaries
 *         - target_luminance
 *       - if the image description is parametric, it may send, if applicable,
 *         - target_max_cll
 *         - target_max_fall
 *       - if the image description contains an ICC profile, it must send the
 *         icc_file event
 * <p>
 *       Once a wp_image_description_info_v1 object has delivered a 'done' event it
 *       is automatically destroyed.
 * <p>
 *       Every wp_image_description_info_v1 created from the same
 *       wp_image_description_v1 shall always return the exact same data.
 *     
 */
public interface WpImageDescriptionInfoV1Events {
  int VERSION = 1;

  /**
   * end of information
   * <p>
   *
   *         Signals the end of information events and destroys the object.
   *       
   * @param emitter The protocol object that emitted the event.
   */
  public void done(WpImageDescriptionInfoV1Proxy emitter);

  /**
   * ICC profile matching the image description
   * <p>
   *
   *         The icc argument provides a file descriptor to the client which may be
   *         memory-mapped to provide the ICC profile matching the image description.
   *         The fd is read-only, and if mapped then it must be mapped with
   *         MAP_PRIVATE by the client.
   * <p>
   *         The ICC profile version and other details are determined by the
   *         compositor. There is no provision for a client to ask for a specific
   *         kind of a profile.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param icc ICC profile file descriptor
   * @param iccSize ICC profile size, in bytes
   */
  public void iccFile(WpImageDescriptionInfoV1Proxy emitter, int icc, int iccSize);

  /**
   * primaries as chromaticity coordinates
   * <p>
   *
   *         Delivers the primary color volume primaries and white point using CIE
   *         1931 xy chromaticity coordinates.
   * <p>
   *         Each coordinate value is multiplied by 1 million to get the argument
   *         value to carry precision of 6 decimals.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param rX Red x * 1M
   * @param rY Red y * 1M
   * @param gX Green x * 1M
   * @param gY Green y * 1M
   * @param bX Blue x * 1M
   * @param bY Blue y * 1M
   * @param wX White x * 1M
   * @param wY White y * 1M
   */
  public void primaries(WpImageDescriptionInfoV1Proxy emitter, int rX, int rY, int gX, int gY, int bX, int bY, int wX, int wY);

  /**
   * named primaries
   * <p>
   *
   *         Delivers the primary color volume primaries and white point using an
   *         explicitly enumerated named set.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param primaries named primaries
   */
  public void primariesNamed(WpImageDescriptionInfoV1Proxy emitter, int primaries);

  /**
   * transfer characteristic as a power curve
   * <p>
   *
   *         The color component transfer characteristic of this image description is
   *         a pure power curve. This event provides the exponent of the power
   *         function. This curve represents the conversion from electrical to
   *         optical pixel or color values.
   * <p>
   *         The curve exponent has been multiplied by 10000 to get the argument eexp
   *         value to carry the precision of 4 decimals.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param eexp the exponent * 10000
   */
  public void tfPower(WpImageDescriptionInfoV1Proxy emitter, int eexp);

  /**
   * named transfer characteristic
   * <p>
   *
   *         Delivers the transfer characteristic using an explicitly enumerated
   *         named function.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param tf named transfer function
   */
  public void tfNamed(WpImageDescriptionInfoV1Proxy emitter, int tf);

  /**
   * primary color volume luminance range and reference white
   * <p>
   *
   *         Delivers the primary color volume luminance range and the reference
   *         white luminance level. These values include the minimum display emission
   *         and ambient flare luminances, assumed to be optically additive and have
   *         the chromaticity of the primary color volume white point.
   * <p>
   *         The minimum luminance is multiplied by 10000 to get the argument
   *         'min_lum' value and carries precision of 4 decimals. The maximum
   *         luminance and reference white luminance values are unscaled.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param minLum minimum luminance (cd/m²) * 10000
   * @param maxLum maximum luminance (cd/m²)
   * @param referenceLum reference white luminance (cd/m²)
   */
  public void luminances(WpImageDescriptionInfoV1Proxy emitter, int minLum, int maxLum, int referenceLum);

  /**
   * target primaries as chromaticity coordinates
   * <p>
   *
   *         Provides the color primaries and white point of the target color volume
   *         using CIE 1931 xy chromaticity coordinates. This is compatible with the
   *         SMPTE ST 2086 definition of HDR static metadata for mastering displays.
   * <p>
   *         While primary color volume is about how color is encoded, the target
   *         color volume is the actually displayable color volume.
   * <p>
   *         Each coordinate value is multiplied by 1 million to get the argument
   *         value to carry precision of 6 decimals.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param rX Red x * 1M
   * @param rY Red y * 1M
   * @param gX Green x * 1M
   * @param gY Green y * 1M
   * @param bX Blue x * 1M
   * @param bY Blue y * 1M
   * @param wX White x * 1M
   * @param wY White y * 1M
   */
  public void targetPrimaries(WpImageDescriptionInfoV1Proxy emitter, int rX, int rY, int gX, int gY, int bX, int bY, int wX, int wY);

  /**
   * target luminance range
   * <p>
   *
   *         Provides the luminance range that the image description is targeting as
   *         the minimum and maximum absolute luminance L. These values include the
   *         minimum display emission and ambient flare luminances, assumed to be
   *         optically additive and have the chromaticity of the primary color
   *         volume white point. This should be compatible with the SMPTE ST 2086
   *         definition of HDR static metadata.
   * <p>
   *         This luminance range is only theoretical and may not correspond to the
   *         luminance of light emitted on an actual display.
   * <p>
   *         Min L value is multiplied by 10000 to get the argument min_lum value and
   *         carry precision of 4 decimals. Max L value is unscaled for max_lum.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param minLum min L (cd/m²) * 10000
   * @param maxLum max L (cd/m²)
   */
  public void targetLuminance(WpImageDescriptionInfoV1Proxy emitter, int minLum, int maxLum);

  /**
   * target maximum content light level
   * <p>
   *
   *         Provides the targeted max_cll of the image description. max_cll is
   *         defined by CTA-861-H.
   * <p>
   *         This luminance is only theoretical and may not correspond to the
   *         luminance of light emitted on an actual display.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param maxCll Maximum content light-level (cd/m²)
   */
  public void targetMaxCll(WpImageDescriptionInfoV1Proxy emitter, int maxCll);

  /**
   * target maximum frame-average light level
   * <p>
   *
   *         Provides the targeted max_fall of the image description. max_fall is
   *         defined by CTA-861-H.
   * <p>
   *         This luminance is only theoretical and may not correspond to the
   *         luminance of light emitted on an actual display.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param maxFall Maximum frame-average light level (cd/m²)
   */
  public void targetMaxFall(WpImageDescriptionInfoV1Proxy emitter, int maxFall);
}
