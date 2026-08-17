package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.core.interop.GLFWGLInteropResourceManager;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
    private static boolean hdr_mod$isReplacingFbo = false;

    @ModifyVariable(method = "_glBindFramebuffer", at = @At("HEAD"), argsOnly = true, index = 1)
    private static int hdr_mod$useDxgiPbo(int value)
    {
        if(hdr_mod$isReplacingFbo) return value;
        if(!HDRMod.glInteropResourceManager.shouldReplaceFbo(value)) return value;
        hdr_mod$isReplacingFbo = true;
        try {
            return HDRMod.glInteropResourceManager.replaceFbo(value);
        } finally {
            hdr_mod$isReplacingFbo = false;
        }
    }
}
