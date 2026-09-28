# vendor/ — how a new project depends on this

Everything here is **source**. No jextract, no annotation processor, no
`pkg-config`, and no vendored jars.

```
vendor/
  wayland-lite/       the library subproject — this is what you depend on
  slim/src/           11 .java  jextract output for libwayland + 6 libc calls
  gen-src/            36 .java  protocol stubs: wayland.xml + color-management-v1
  wayland-java-src/   26 .java  the wayland-java runtime sources actually used
  licenses/           third-party license texts
  prune.py            recomputes the reachable set for gen-src
  proguard/           optional keep-rules (see the bottom)
  slim/regen.sh       regenerates slim/src from libwayland headers
```

`vendorSummary` prints the current counts:

```
$ ./gradlew vendorSummary
  jextract binding : 11 source files (upstream: 145)
  protocol stubs   : 36 source files (188 generated, pruned to the reachable set)
  wayland-java src : 26 source files
  local jars       : 0 (0 = everything is source or Maven)
```

## Consuming it

`vendor/wayland-lite` is a plain `java-library` subproject, so inside this
repository you just use the project path:

```kotlin
dependencies { implementation(project(":vendor:wayland-lite")) }
```

From **another** project, point at this repository and use the coordinates:

```kotlin
// settings.gradle.kts
includeBuild("../path/to/wayland-java-cm")
```
```kotlin
dependencies { implementation("org.freedesktop.wayland:wayland-lite") }
```

`consumer/` in this repository is exactly that, and is the working example.

Without Gradle, compile the three source dirs and put two Maven jars on the
classpath:

```sh
javac -d out $(find vendor/slim/src vendor/gen-src vendor/wayland-java-src -name '*.java')
# runtime classpath: out + org.slf4j:slf4j-api:1.7.36
# build-time only:   com.google.code.findbugs:jsr305:3.0.2
java --enable-native-access=ALL-UNNAMED -cp "out:slf4j-api-1.7.36.jar" your.Main
```

## Why dependencies are source, not jars

wayland-java is **not published on Maven Central** — every coordinate
(`org.freedesktop.wayland:stubs-client`, `…:wayland-native`,
`…:wayland-protocols`) returns 404 on both Central and Aliyun, and JitPack cannot
build it because it needs jextract. So anything not vendored as source cannot be
referenced at all.

Everything else is a normal Maven coordinate: `slf4j-api` (needed by
`stubs-shared`) and `jsr305` (only for the `javax.annotation.*` references in the
generated sources, `compileOnly`).

Two reductions make the vendored tree small:

- **Only 23 Wayland functions are called.** `javap` over wayland-java's runtime
  jars yields the full set, so jextract runs with an `--include-function`
  allow-list: 145 classes / 1.6 MB → 11 source files.
- **152 of the 188 protocol stubs are unreachable.** `vendor/prune.py` computes
  the closure from the app plus the runtime sources and drops the rest (data
  device, input, shm, shell, subcompositor, …).

Note that the `C` / `C_1` split upstream's jextract output needs is gone: it
existed only to satisfy a reference in the *precompiled* stub jar. The vendored
sources call `C.fcntl`, so `fcntl` is generated into `C` and `regen.sh` needs
only two passes.

## Runtime requirements

| Need | Why |
|---|---|
| JDK 22+ (we use 25) | FFM is final; the bindings use `canonicalLayouts()` |
| `libwayland-client.so.0` | the actual Wayland client |
| `--enable-native-access=ALL-UNNAMED` | FFM downcalls |

One non-obvious trap: upstream's generated binding calls
`SymbolLookup.libraryLookup(System.mapLibraryName("wayland-client"))`, which asks
`dlopen` for the *unversioned* `libwayland-client.so`. That symlink ships in the
`-dev` package, and `dlopen` has no soname fallback — it needs an exact filename
match. A C shim records `DT_NEEDED: libwayland-client.so.0` and therefore only
needs the runtime package. The vendored copy prefers the real soname and falls
back, so it works with only the runtime package installed.

## Regenerating

- `vendor/slim/src` — `vendor/slim/regen.sh` (needs jextract 25 on `PATH`; the
  script also re-applies the soname fix, with an assertion so a change in
  jextract's output fails loudly).
- `vendor/gen-src` — re-run the annotation processor from the `wayland-java`
  submodule, then `python3 vendor/prune.py`.
- `vendor/wayland-java-src` — copy `stubs-shared/src/main/java` and
  `stubs-client/src/main/java` from the submodule.

## ProGuard (optional)

`vendor/proguard/wcm.pro` shrinks an application jar. Its first rule is a hard
boundary: **nothing outside `org.freedesktop.wayland.*` may be removed**, so the
application and any other input classes survive verbatim.

It does work on Java 25 — provided it is paired with `proguard-core` 9.4.0. An
older core misleadingly reports
`Unsupported version number [69.0] (maximum 68.65535)` and looks like ProGuard
cannot read Java 25 class files at all.

Measured on this repository:

| | |
|---|---|
| input | `app.jar` + `wayland-lite.jar`, 120 classes, 156 KB |
| output | 95 classes, 108 KB |
| removed | 25, **all** under `org.freedesktop.wayland.` |
| verified | hdr10 / scrgb / p3 / none all still apply |

What actually goes: the unused EGL helper, the compile-time-only annotation
classes, `ShmPool`/`ShmUtil`, the libc invokers (`C$mmap`, `C$fcntl`, …), and
the unused `LibWayland$…` holder classes. The 8% jar-size saving is real but
modest — the source-level pruning in `vendor/prune.py` is what removes bulk.

Two keep rules are easy to get wrong, and both fail with a misleading message:

- **the enum package must be kept whole.** `EnumUtil.buildEnumMap` calls
  `getEnumConstants()`; keeping only `getValue()` lets ProGuard strip the
  constants and `values()`, and the enum's static initialiser then dies with
  `ExceptionInInitializerError` caused by
  `NullPointerException: Cannot read the array length because "<local4>" is null`;
- **`org.freedesktop.wayland.raw`'s interfaces must be kept.** libwayland's
  dispatcher is installed as an upcall via
  `MethodHandles.lookup().findVirtual(...)`, which no static analysis sees.
  Losing it surfaces as wayland-java's generic
  `RuntimeException: "Uh oh, this is a bug!"`.

The rules are not exercised by CI; re-run them by hand if you change the
dependency versions.

