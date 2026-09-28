/*
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
 *
 *
 */
package org.freedesktop.wayland.generator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify and configure the generation of Wayland protocol bindings
 * from a directory containing multiple protocol XML files.
 * This annotation is applied at the package level and is processed at compile-time.
 *
 * <p>By default, it uses the directory specified by the command
 * {@code pkg-config --variable=pkgdatadir wayland-protocols}, which typically contains
 * subdirectories for stable, staging, and unstable protocols.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @WaylandProtocols(
 *     withStable = true,
 *     withStaging = false,
 *     withUnstable = true,
 *     generateClient = true,
 *     generateServer = false
 * )
 * package org.freedesktop.wayland;
 * }
 * </pre>
 *
 * @since 1.0
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface WaylandProtocols {

    /**
     * Specifies a custom path to the directory containing the protocol XML files.
     * Can be set to a path relative to the path resolved by pkg-config, an absolute path,
     * or a path relative to the {@code wayland.scanner.protocol.root} option.
     *
     * @return the custom path to the protocols directory
     */
    String path() default "";

    /**
     * Specifies the pkg-config package name to use for resolving the protocols directory.
     * By default, it uses "wayland-protocols".
     *
     * @return the pkg-config package name
     */
    String pkgConfig() default "wayland-protocols";

    /**
     * Determines whether to generate bindings for stable protocols.
     * Stable protocols are typically found in the "stable" subdirectory.
     *
     * @return true if stable protocol bindings should be generated, false otherwise
     */
    boolean withStable() default true;

    /**
     * Determines whether to generate bindings for staging protocols.
     * Staging protocols are typically found in the "staging" subdirectory.
     *
     * @return true if staging protocol bindings should be generated, false otherwise
     */
    boolean withStaging() default true;

    /**
     * Determines whether to generate bindings for unstable protocols.
     * Unstable protocols are typically found in the "unstable" subdirectory.
     *
     * @return true if unstable protocol bindings should be generated, false otherwise
     */
    boolean withUnstable() default true;

    /**
     * Specifies the package name for shared protocol elements.
     *
     * @return the package name for shared elements
     */
    String sharedPackage() default "shared";

    /**
     * Specifies the package name for client-side protocol elements.
     *
     * @return the package name for client-side elements
     */
    String clientPackage() default "client";

    /**
     * Specifies the package name for server-side protocol elements.
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
