package xyz.rrtt217.HDRMod.mixin.compat.blazesdl;

import org.lwjgl.sdl.SDLVideo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.blazesdl.SDLGlSurface;
import xyz.rrtt217.HDRMod.HDRMod;

@Mixin(SDLGlSurface.class)
public class MixinSDLGlSurface {
    @Inject(method = "present", at = @At("HEAD"), cancellable = true)
    private void present(CallbackInfo ci) {
        if(HDRMod.glInteropResourceManager.presentSwapchain()) ci.cancel();
    }

    @Redirect(method = "configure", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDLVideo;SDL_GL_SetSwapInterval(I)Z"))
    private boolean configure(int interval) {
        if(!HDRMod.glInteropResourceManager.setSwapInterval(interval))
            SDLVideo.SDL_GL_SetSwapInterval(interval);
        return true;
    }
}