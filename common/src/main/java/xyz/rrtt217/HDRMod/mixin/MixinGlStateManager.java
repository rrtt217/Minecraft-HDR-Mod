package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
    @ModifyVariable(method = "_glBindFramebuffer", at = @At("HEAD"), argsOnly = true, index = 1)
    private static int hdr_mod$useDxgiPbo(int value){
        return DXGIStateManager.replaceFbo(value);
    }
}
