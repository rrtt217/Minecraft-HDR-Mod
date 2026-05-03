package xyz.rrtt217.HDRMod.mixin.compat.imblocker;

import com.sun.jna.Platform;
import io.github.reserveword.imblocker.common.IMManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.compat.imblocker.IMManagerLinuxEnhanced;

@Mixin(IMManager.class)
public class DebugMixinIMManager {
    @Mutable
    @Shadow
    @Final
    private static IMManager.PlatformIMManager INSTANCE;

    @Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
    private static void debugReplaceIMManagerInstance(CallbackInfo ci){
        if(Platform.isLinux()){
            INSTANCE = new IMManagerLinuxEnhanced();
            ci.cancel();
        }
    }
}
