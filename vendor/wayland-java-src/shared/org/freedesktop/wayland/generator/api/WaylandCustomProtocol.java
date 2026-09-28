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
 * Annotation used to specify a custom Wayland protocol for code generation.
 * This annotation is applied at the package level and is processed at compile-time.
 *
 * <p>It allows for fine-grained control over the generation of Wayland protocol
 * bindings, including specifying the protocol XML file, package names for generated
 * code, and which parts of the protocol to generate (client, server, or both).</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @WaylandCustomProtocols(
 * @WaylandCustomProtocol(
 *     path = "foo.xml",
 *     pkgConfig = "wayland-foo",
 *     generateClient = true,
 *     generateServer = false
 * ))
 * package my.domain.wayland;
 * }
 * </pre>
 *
 * @since 1.0
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface WaylandCustomProtocol {

    /**
     * Specifies the path to the Wayland protocol XML file.
     * This can be an absolute path, a path relative to the {@code wayland.scanner.protocol.root} option,
     * or a filename to be resolved relative to the pkgConfig setting.
     *
     * @return the path to the protocol XML file
     */
    String path();

    /**
     * Specifies the pkg-config package name to use for resolving the protocol file path.
     * If set, the system will use `pkg-config --variable=pkgdatadir [pkgConfig]`
     * to determine the directory containing the protocol file.
     *
     * @return the pkg-config package name, or an empty string if not used
     */
    String pkgConfig() default "";

    /**
     * Indicates whether to use DTD validation when parsing the protocol XML.
     *
     * @return true if DTD validation should be used, false otherwise
     */
    boolean dtd() default false;

    /**
     * Specifies the package name for shared protocol elements.
     *
     * @return the package name for shared elements
     */
    String sharedPackage() default "shared";

    /**
     * Specifies the subpackage name for client-side protocol elements.
     *
     * @return the package name for client-side elements
     */
    String clientPackage() default "client";

    /**
     * Specifies the subpackage name for server-side protocol elements.
     *
     * @return the package name for server-side elements
     */
    String serverPackage() default "server";

    /**
     * Determines whether to generate client-side protocol bindings.
     *
     * @return true if client-side bindings should be generated, false otherwise
     */
    boolean generateClient() default true;

    /**
     * Determines whether to generate server-side protocol bindings.
     *
     * @return true if server-side bindings should be generated, false otherwise
     */
    boolean generateServer() default true;
}
