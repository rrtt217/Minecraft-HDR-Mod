package xyz.rrtt217.HDRMod.mixin.gl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder")
public class MixinGlCommandEncoder {
    @ModifyArg(method = "presentTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/DirectStateAccess;blitFrameBuffers(IIIIIIIIIIII)V"), index = 1)
    private int hdr_mod$modifyPresentTexture(int i){
        return DXGIStateManager.replaceFbo(i);
    }
}
