package xyz.rrtt217.HDRMod.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

// Used in a Mixin Plugin.
public class Platform {
    @ExpectPlatform
    public static boolean isFabric(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isQuilt(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isFabricLike(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isNeoForge(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isForge(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isForgeLike(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isDevelopmentEnvironment(){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isModLoaded(String modId){
        throw new AssertionError();
    }
}
