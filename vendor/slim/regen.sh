#!/bin/sh
# Regenerate the minimal wayland-native bindings.
#
# Produces vendor/slim/src (13 .java files, ~128 KB) which replaces upstream's
# 145-file / 1.6 MB jextract output. Only the symbols wayland-java actually
# calls are bound, so unused libc types (FILE, fd_set, div_t, ...) are gone.
#
# Requires: jextract 25 on PATH (see ../../BUILD_java_env.md).
#
# Two passes: one for libwayland, one for the handful of libc calls the
# vendored wayland-java sources use (they reference C.fcntl, C.mmap, ...).
set -eu

JX="${JX:-jextract}"
OUT="$(dirname "$0")/src"
PKG=org.freedesktop.wayland.raw

rm -rf "$OUT"

# ---- pass 1: wayland-client core -------------------------------------------
# Exactly the 23 functions referenced by stubs-client/stubs-shared
# (extracted with: javap -c on both jars | grep -o 'LibWayland\.[a-z_]*').
WAYLAND_FUNCS="
wl_display_cancel_read wl_display_connect wl_display_connect_to_fd
wl_display_create_queue wl_display_disconnect wl_display_dispatch
wl_display_dispatch_pending wl_display_dispatch_queue
wl_display_dispatch_queue_pending wl_display_flush wl_display_get_error
wl_display_get_fd wl_display_prepare_read wl_display_prepare_read_queue
wl_display_read_events wl_display_roundtrip wl_event_queue_destroy
wl_proxy_add_dispatcher wl_proxy_destroy wl_proxy_get_id
wl_proxy_marshal_array wl_proxy_marshal_array_constructor wl_proxy_set_queue
"
set -- # build the arg list
for f in $WAYLAND_FUNCS; do set -- "$@" --include-function "$f"; done
for t in wl_interface wl_message wl_array wl_proxy wl_object wl_list; do
    set -- "$@" --include-struct "$t"
done
set -- "$@" --include-union wl_argument
for t in wl_notify_func_t wl_dispatcher_func_t wl_log_func_t wl_destroy_func_t; do
    set -- "$@" --include-typedef "$t"
done
"$JX" --output "$OUT" --target-package "$PKG" --header-class-name LibWayland \
    -l wayland-client "$@" /usr/include/wayland-client-core.h

# ---- pass 2: the libc bits the vendored sources need, in class C ----------
set --
for f in close ftruncate mkstemp mmap munmap fcntl; do set -- "$@" --include-function "$f"; done
"$JX" --output "$OUT" --target-package "$PKG" --header-class-name C "$@" \
    /usr/include/unistd.h /usr/include/sys/mman.h /usr/include/stdlib.h /usr/include/fcntl.h

echo "generated $(find "$OUT" -name '*.java' | wc -l) files in $OUT"

# ---- compiling -------------------------------------------------------------
# Do not compile in place: this directory is source-only, and the build compiles
# $OUT through vendor/wayland-lite. If you need the classes for a manual javac
# run, send them to a throwaway directory:
#   "${JAVA_HOME?}/bin/javac" -d /tmp/slim-classes $(find "$OUT" -name '*.java')

# ---- POST-PROCESS (required, do not skip) ----------------------------------
# Upstream jextract emits dlopen("libwayland-client.so") (the unversioned name,
# from System.mapLibraryName). That symlink lives in the -devel package, which
# end users do not have, and dlopen() has no soname fallback. Replace the lookup
# with one that asks for the real SONAME first. See vendor/README.md.
LIB="$OUT/org/freedesktop/wayland/raw/LibWayland.java"
python3 - "$LIB" <<'PYEOF'
import sys
p = sys.argv[1]
s = open(p).read()
old = 'SymbolLookup.libraryLookup(System.mapLibraryName("wayland-client"), LIBRARY_ARENA)'
new = 'loadWaylandClient()'
assert old in s, "lookup line not found - jextract output changed"
s = s.replace(old, new)
helper = '''
    private static SymbolLookup loadWaylandClient() {
        for (final String name : new String[] { "libwayland-client.so.0", "libwayland-client.so" }) {
            try {
                return SymbolLookup.libraryLookup(name, LIBRARY_ARENA);
            } catch (final Throwable ignored) {
            }
        }
        throw new IllegalStateException("cannot load libwayland-client");
    }
'''
s = s.replace('    private static class wl_event_queue_destroy {', helper + '\n    private static class wl_event_queue_destroy {', 1)
open(p, 'w').write(s)
PYEOF
echo "patched libwayland-client lookup to prefer the versioned soname"
