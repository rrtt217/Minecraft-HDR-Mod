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
 * color manager singleton
 * <p>
 *
 *       A singleton global interface used for getting color management extensions
 *       for wl_surface and wl_output objects, and for creating client defined
 *       image description objects. The extension interfaces allow
 *       getting the image description of outputs and setting the image
 *       description of surfaces.
 * <p>
 *       Compositors should never remove this global.
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
    org.freedesktop.wayland.client.WpColorManagementOutputV1Proxy.class,
    org.freedesktop.wayland.client.WlOutputProxy.class
  },
  signature = "no",
  functionName = "getOutput",
  name = "get_output"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpColorManagementSurfaceV1Proxy.class,
    org.freedesktop.wayland.client.WlSurfaceProxy.class
  },
  signature = "no",
  functionName = "getSurface",
  name = "get_surface"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpColorManagementSurfaceFeedbackV1Proxy.class,
    org.freedesktop.wayland.client.WlSurfaceProxy.class
  },
  signature = "no",
  functionName = "getSurfaceFeedback",
  name = "get_surface_feedback"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionCreatorIccV1Proxy.class
  },
  signature = "n",
  functionName = "createIccCreator",
  name = "create_icc_creator"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionCreatorParamsV1Proxy.class
  },
  signature = "n",
  functionName = "createParametricCreator",
  name = "create_parametric_creator"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class
  },
  signature = "n",
  functionName = "createWindowsScrgb",
  name = "create_windows_scrgb"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class,
    org.freedesktop.wayland.client.WpImageDescriptionReferenceV1Proxy.class
  },
  signature = "2no",
  functionName = "getImageDescription",
  name = "get_image_description"
)
,
    @Message(
  types = {
    org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class
  },
  signature = "3n",
  functionName = "createWindowsBt2100",
  name = "create_windows_bt2100"
)

  },
  name = "wp_color_manager_v1",
  version = 3,
  events = {
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "supportedIntent",
  name = "supported_intent"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "supportedFeature",
  name = "supported_feature"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "supportedTfNamed",
  name = "supported_tf_named"
)
,
    @Message(
  types = {
    int.class
  },
  signature = "u",
  functionName = "supportedPrimariesNamed",
  name = "supported_primaries_named"
)
,
    @Message(
  types = {
  },
  signature = "",
  functionName = "done",
  name = "done"
)

  }
)
public class WpColorManagerV1Proxy extends Proxy<WpColorManagerV1Events> {

  public static final String INTERFACE_NAME = "wp_color_manager_v1";

  public WpColorManagerV1Proxy(java.lang.foreign.MemorySegment pointer, WpColorManagerV1Events implementation, int version) {
    super(pointer, implementation, version);
  }

  public WpColorManagerV1Proxy(java.lang.foreign.MemorySegment pointer) {
    super(pointer);
  }

  /**
   * destroy the color manager
   * <p>
   *
   *         Destroy the wp_color_manager_v1 object. This does not affect any other
   *         objects in any way.
   *       
   */
  public void destroy() {
    marshal(0);
  }

  /**
   * create a color management interface for a wl_output
   * <p>
   *
   *         This creates a new wp_color_management_output_v1 object for the
   *         given wl_output.
   * <p>
   *         See the wp_color_management_output_v1 interface for more details.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   * @param output 
   */
  public WpColorManagementOutputV1Proxy getOutput(WpColorManagementOutputV1Events implementation, @Nonnull WlOutputProxy output) {
    return marshalConstructor(1, implementation, getVersion(), org.freedesktop.wayland.client.WpColorManagementOutputV1Proxy.class, Arguments.create(2).set(0, 0).set(1, output));
  }

  /**
   * create a color management interface for a wl_surface
   * <p>
   *
   *         If a wp_color_management_surface_v1 object already exists for the given
   *         wl_surface, the protocol error surface_exists is raised.
   * <p>
   *         This creates a new color wp_color_management_surface_v1 object for the
   *         given wl_surface.
   * <p>
   *         See the wp_color_management_surface_v1 interface for more details.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   * @param surface 
   */
  public WpColorManagementSurfaceV1Proxy getSurface(WpColorManagementSurfaceV1Events implementation, @Nonnull WlSurfaceProxy surface) {
    return marshalConstructor(2, implementation, getVersion(), org.freedesktop.wayland.client.WpColorManagementSurfaceV1Proxy.class, Arguments.create(2).set(0, 0).set(1, surface));
  }

  /**
   * create a color management feedback interface
   * <p>
   *
   *         This creates a new color wp_color_management_surface_feedback_v1 object
   *         for the given wl_surface.
   * <p>
   *         See the wp_color_management_surface_feedback_v1 interface for more
   *         details.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   * @param surface 
   */
  public WpColorManagementSurfaceFeedbackV1Proxy getSurfaceFeedback(WpColorManagementSurfaceFeedbackV1Events implementation, @Nonnull WlSurfaceProxy surface) {
    return marshalConstructor(3, implementation, getVersion(), org.freedesktop.wayland.client.WpColorManagementSurfaceFeedbackV1Proxy.class, Arguments.create(2).set(0, 0).set(1, surface));
  }

  /**
   * make a new ICC-based image description creator object
   * <p>
   *
   *         Makes a new ICC-based image description creator object with all
   *         properties initially unset. The client can then use the object's
   *         interface to define all the required properties for an image description
   *         and finally create a wp_image_description_v1 object.
   * <p>
   *         This request can be used when the compositor advertises
   *         wp_color_manager_v1.feature.icc_v2_v4.
   *         Otherwise this request raises the protocol error unsupported_feature.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionCreatorIccV1Proxy createIccCreator(WpImageDescriptionCreatorIccV1Events implementation) {
    return marshalConstructor(4, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionCreatorIccV1Proxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * make a new parametric image description creator object
   * <p>
   *
   *         Makes a new parametric image description creator object with all
   *         properties initially unset. The client can then use the object's
   *         interface to define all the required properties for an image description
   *         and finally create a wp_image_description_v1 object.
   * <p>
   *         This request can be used when the compositor advertises
   *         wp_color_manager_v1.feature.parametric.
   *         Otherwise this request raises the protocol error unsupported_feature.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionCreatorParamsV1Proxy createParametricCreator(WpImageDescriptionCreatorParamsV1Events implementation) {
    return marshalConstructor(5, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionCreatorParamsV1Proxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * create Windows-scRGB image description object
   * <p>
   *
   *         This creates a pre-defined image description for the so-called
   *         Windows-scRGB stimulus encoding. This comes from the Windows 10 handling
   *         of its own definition of an scRGB color space for an HDR screen
   *         driven in BT.2100/PQ signalling mode.
   * <p>
   *         Windows-scRGB uses sRGB (BT.709) color primaries and white point.
   *         The transfer characteristic is extended linear.
   * <p>
   *         The nominal color channel value range is extended, meaning it includes
   *         negative and greater than 1.0 values. Negative values are used to
   *         escape the sRGB color gamut boundaries. To make use of the extended
   *         range, the client needs to use a pixel format that can represent those
   *         values, e.g. floating-point 16 bits per channel.
   * <p>
   *         Nominal color value R=G=B=0.0 corresponds to BT.2100/PQ system
   *         0 cd/m², and R=G=B=1.0 corresponds to BT.2100/PQ system 80 cd/m².
   *         The maximum is R=G=B=125.0 corresponding to 10k cd/m².
   * <p>
   *         Windows-scRGB is displayed by Windows 10 by converting it to
   *         BT.2100/PQ, maintaining the CIE 1931 chromaticity and mapping the
   *         luminance as above. No adjustment is made to the signal to account
   *         for the viewing conditions.
   * <p>
   *         The reference white level of Windows-scRGB is unknown. If a
   *         reference white level must be assumed for compositor processing, it
   *         should be R=G=B=2.5375 corresponding to 203 cd/m² of Report ITU-R
   *         BT.2408-7.
   * <p>
   *         The target color volume of Windows-scRGB is unknown. The color gamut
   *         may be anything between sRGB and BT.2100.
   * <p>
   *         Note: EGL_EXT_gl_colorspace_scrgb_linear definition differs from
   *         Windows-scRGB by using R=G=B=1.0 as the reference white level, while
   *         Windows-scRGB reference white level is unknown or varies. However,
   *         it seems probable that Windows implements both
   *         EGL_EXT_gl_colorspace_scrgb_linear and Vulkan
   *         VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT as Windows-scRGB.
   * <p>
   *         This request can be used when the compositor advertises
   *         wp_color_manager_v1.feature.windows_scrgb.
   *         Otherwise this request raises the protocol error unsupported_feature.
   * <p>
   *         The resulting image description object does not allow get_information
   *         request. The wp_image_description_v1.ready event shall be sent.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionV1Proxy createWindowsScrgb(WpImageDescriptionV1Events implementation) {
    return marshalConstructor(6, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(1).set(0, 0));
  }

  /**
   * create an image description from a reference
   * <p>
   *
   *         This request retrieves the image description backing a reference.
   * <p>
   *         The get_information request can be used if and only if the request that
   *         creates the reference allows it.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   * @param reference 
   */
  public WpImageDescriptionV1Proxy getImageDescription(WpImageDescriptionV1Events implementation, @Nonnull WpImageDescriptionReferenceV1Proxy reference) {
    if (getVersion() < 2) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 2 is required for this operation.");
    }
    return marshalConstructor(7, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(2).set(0, 0).set(1, reference));
  }

  /**
   * create Windows-BT.2100 image description object
   * <p>
   *
   *         This creates a pre-defined image description for the so-called
   *         Windows-BT.2100 stimulus encoding. This comes from the Windows 10
   *         handling of its own definition of a BT.2100 color space for an HDR
   *         screen driven in BT.2100/PQ signalling mode.
   * <p>
   *         Windows-BT.2100 uses BT.2020 color primaries and white point.
   *         The transfer characteristic is st2084_pq.
   * <p>
   *         Windows-BT.2100 is generally displayed by Windows 10 without any
   *         adjustments to the signal to account for viewing conditions.
   * <p>
   *         The reference white level of Windows-BT.2100 is unknown. If a
   *         reference white level must be assumed for compositor processing, it
   *         should be 203 cd/m² of Report ITU-R BT.2408-7.
   * <p>
   *         The target color volume of Windows-BT.2100 is unknown. The color gamut
   *         may be anything up to BT.2100.
   * <p>
   *         This request can be used when the compositor advertises
   *         wp_color_manager_v1.feature.windows_bt2100.
   *         Otherwise this request raises the protocol error unsupported_feature.
   * <p>
   *         The resulting image description object does not allow get_information
   *         request. The wp_image_description_v1.ready event shall be sent.
   *       
   * @param implementation A protocol event listener for the newly created proxy.
   */
  public WpImageDescriptionV1Proxy createWindowsBt2100(WpImageDescriptionV1Events implementation) {
    if (getVersion() < 3) {
      throw new UnsupportedOperationException("This object is version "+getVersion()+" while version 3 is required for this operation.");
    }
    return marshalConstructor(8, implementation, getVersion(), org.freedesktop.wayland.client.WpImageDescriptionV1Proxy.class, Arguments.create(1).set(0, 0));
  }
}
