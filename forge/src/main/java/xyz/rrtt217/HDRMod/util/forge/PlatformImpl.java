package xyz.rrtt217.HDRMod.util.forge;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;

public class PlatformImpl {
    public static boolean isQuilt() {
        return false;
    }

    public static boolean isFabric() {
        return false;
    }

    public static boolean isFabricLike() {
        return false;
    }

    public static boolean isNeoForge() {
        return false;
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isForgeLike() {
        return true;
    }

    public static boolean isDevelopmentEnvironment(){
        return !FMLLoader.isProduction();
    }

    public static boolean isModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}