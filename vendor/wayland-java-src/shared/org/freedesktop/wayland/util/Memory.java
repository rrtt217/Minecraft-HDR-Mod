package org.freedesktop.wayland.util;

import java.lang.foreign.Arena;

public final class Memory {
    public static final Arena ARENA_AUTO = Arena.ofAuto();
    public static final Arena ARENA_SHARED = Arena.ofShared();
}
