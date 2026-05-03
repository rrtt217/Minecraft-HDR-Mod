package xyz.rrtt217.HDRMod.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformImpl {
    public static boolean isQuilt() {
        return false;
    }

    public static boolean isFabric() {
        return true;
    }

    public static boolean isFabricLike() {
        return true;
    }

    public static boolean isNeoForge() {
        return false;
    }

    public static boolean isForge() {
        return false;
    }

    public static boolean isForgeLike() {
        return false;
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
