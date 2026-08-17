package xyz.rrtt217.HDRMod.util.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SetupBeforeGLFWInit {
    @ExpectPlatform
    public static void setup(){
        throw new AssertionError();
    }
}
