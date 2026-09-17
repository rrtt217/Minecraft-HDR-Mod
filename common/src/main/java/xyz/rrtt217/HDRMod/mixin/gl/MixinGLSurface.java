package xyz.rrtt217.HDRMod.mixin.gl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.renderpearl.backend.opengl.GlSurface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;

@Mixin(GlSurface.class)
public class MixinGLSurface {
    @Inject(method = "present", at = @At("HEAD"), cancellable = true)
    private void present(CallbackInfo ci) {
        if(HDRMod.glInteropResourceManager.presentSwapchain()) ci.cancel();
    }

    @WrapOperation(method = "configure", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDLVideo;SDL_GL_SetSwapInterval(I)Z"))
    private boolean configure(int interval, Operation<Void> original) {
        if(!HDRMod.glInteropResourceManager.setSwapInterval(interval))
            original.call(interval);
        return false;
    }
}
