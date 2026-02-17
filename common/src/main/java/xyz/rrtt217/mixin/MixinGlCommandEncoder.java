package xyz.rrtt217.mixin;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.config.HDRModConfig;
import xyz.rrtt217.util.HDRModInjectHooks;

@Mixin(GlCommandEncoder.class)
public class MixinGlCommandEncoder {
    @ModifyArg(method = "copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;IIIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_readPixels(IIIIIIJ)V"), index = 5)
    private int modifyReadPixelFormat(int i){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(HDRModInjectHooks.isInjectEnabled()){
            if(config.useRGBA16UNORM) return GL30.GL_UNSIGNED_SHORT;
            return GL30.GL_HALF_FLOAT;
        }
        return i;
    }
}
