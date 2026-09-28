/*
 * Copyright © 2015 Erik De Rijcke
 * Copyright © 2024 Casey Link
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freedesktop.wayland.generator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that allows specifying multiple custom Wayland protocols
 * for code generation. This annotation is applied at the package level and is
 * processed at compile-time.
 *
 * <p>This annotation serves as a wrapper for multiple {@link WaylandCustomProtocol}
 * annotations, allowing developers to define bindings for several custom Wayland
 * protocols in a single declaration.</p>
 *
 * <p>Usage example:</p> refer to {@link WaylandCustomProtocol}
 *
 * </pre>
 *
 * <p>This annotation is particularly useful when you need to generate bindings
 * for multiple custom Wayland protocols that are not part of the standard
 * wayland-protocols package or when you need fine-grained control over the
 * generation process for each protocol.</p>
 *
 * @see WaylandCustomProtocol
 * @since 1.0
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface WaylandCustomProtocols {

    /**
     * An array of {@link WaylandCustomProtocol} annotations, each specifying
     * a custom Wayland protocol for which bindings should be generated.
     *
     * @return an array of WaylandCustomProtocol annotations
     */
    WaylandCustomProtocol[] value();
}
