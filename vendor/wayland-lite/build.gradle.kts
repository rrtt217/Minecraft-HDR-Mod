// Vendored, self-contained build of everything this project needs from
// wayland-java. No jextract, no annotation processor, no pkg-config.
//
// Dependencies come from two places only:
//   * source in this repository (our own jextract output, our pruned protocol
//     stubs, and the vendored wayland-java runtime sources)
//   * real Maven coordinates (wayland-java is NOT on Maven Central, so
//     whatever is not vendored as source cannot be referenced)
// There are deliberately no file()/flatDir references to local jars.
plugins { `java-library` }

group = "org.freedesktop.wayland"
version = "1.0-vendored"

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

repositories { mavenCentral() }

// One copy of the sources, kept where they are (no duplication in git):
//   ../slim/src              jextract output for libwayland + 6 libc calls (13)
//   ../gen-src               wayland.xml + staging color-management-v1 (36)
//   ../wayland-java-src/*    the parts of wayland-java we actually use (26)
sourceSets.main {
    java.srcDirs(
        "../slim/src",
        "../gen-src",
        "../wayland-java-src/shared",
        "../wayland-java-src/client",
    )
}

dependencies {
    // stubs-shared logs through SLF4J; it is part of the public API surface.
    api("org.slf4j:slf4j-api:1.7.36")
    // javax.annotation.* appears in the generated sources. CLASS retention, so
    // this never reaches consumers.
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}
