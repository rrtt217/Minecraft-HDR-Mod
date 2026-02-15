package xyz.rrtt217.mixin;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.util.HDRModInjectHooks;

@Mixin(GlCommandEncoder.class)
public class MixinGlCommandEncoder {
    @ModifyArgs(method = "copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;IIIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_readPixels(IIIIIIJ)V"))
    private void modifyReadPixelFormat(Args args){
        if(HDRModInjectHooks.isInjectEnabled()) args.set(5, GL30.GL_HALF_FLOAT);
    }
}
