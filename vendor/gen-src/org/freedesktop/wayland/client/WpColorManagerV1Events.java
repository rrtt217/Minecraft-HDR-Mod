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
public interface WpColorManagerV1Events {
  int VERSION = 1;

  /**
   * supported rendering intent
   * <p>
   *
   *         When this object is created, it shall immediately send this event once
   *         for each rendering intent the compositor supports.
   * <p>
   *         A compositor must not advertise intents that are deprecated in the
   *         bound version of the interface.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param renderIntent rendering intent
   */
  public void supportedIntent(WpColorManagerV1Proxy emitter, int renderIntent);

  /**
   * supported features
   * <p>
   *
   *         When this object is created, it shall immediately send this event once
   *         for each compositor supported feature listed in the enumeration.
   * <p>
   *         A compositor must not advertise features that are deprecated in the
   *         bound version of the interface.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param feature supported feature
   */
  public void supportedFeature(WpColorManagerV1Proxy emitter, int feature);

  /**
   * supported named transfer characteristic
   * <p>
   *
   *         When this object is created, it shall immediately send this event once
   *         for each named transfer function the compositor supports with the
   *         parametric image description creator.
   * <p>
   *         A compositor must not advertise transfer functions that are deprecated
   *         in the bound version of the interface.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param tf Named transfer function
   */
  public void supportedTfNamed(WpColorManagerV1Proxy emitter, int tf);

  /**
   * supported named primaries
   * <p>
   *
   *         When this object is created, it shall immediately send this event once
   *         for each named set of primaries the compositor supports with the
   *         parametric image description creator.
   * <p>
   *         A compositor must not advertise names that are deprecated in the
   *         bound version of the interface.
   *       
   * @param emitter The protocol object that emitted the event.
   * @param primaries Named color primaries
   */
  public void supportedPrimariesNamed(WpColorManagerV1Proxy emitter, int primaries);

  /**
   * all features have been sent
   * <p>
   *
   *         This event is sent when all supported rendering intents, features,
   *         transfer functions and named primaries have been sent.
   *       
   * @param emitter The protocol object that emitted the event.
   */
  public void done(WpColorManagerV1Proxy emitter);
}
