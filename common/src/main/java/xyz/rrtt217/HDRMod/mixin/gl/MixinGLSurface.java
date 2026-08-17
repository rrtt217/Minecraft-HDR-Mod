package xyz.rrtt217.HDRMod.mixin.gl;

import com.mojang.blaze3d.opengl.GlSurface;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;

@Mixin(GlSurface.class)
public class MixinGLSurface {
    @Inject(method = "present", at = @At("HEAD"), cancellable = true)
    private void present(CallbackInfo ci) {
        if(HDRMod.glInteropResourceManager.presentSwapchain()) ci.cancel();
    }

    @Redirect(method = "configure", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapInterval(I)V"))
    private void configure(int interval) {
        if(!HDRMod.glInteropResourceManager.setSwapInterval(interval))
            GLFW.glfwSwapInterval(interval);
    }
}
