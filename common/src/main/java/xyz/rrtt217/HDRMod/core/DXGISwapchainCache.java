package xyz.rrtt217.HDRMod.core;

public final class DXGISwapchainCache {
    public static volatile int texture    = 0;
    public static volatile int lastWidth  = 0;
    public static volatile int lastHeight = 0;

    private DXGISwapchainCache() {}
}
