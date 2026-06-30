package xyz.rrtt217.HDRMod.mixin.compat.sodium;

import com.mojang.blaze3d.GpuFormat;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ShaderChunkRenderer.class)
public class MixinShaderChunkRenderer {
    @ModifyArg(method = "createShader", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V", ordinal = 0), index = 1)
    private GpuFormat hdr_mod$ModifySodiumChunkRendererPipeline(GpuFormat format){
        if(format == GpuFormat.RGBA8_UNORM) return GpuFormat.RGBA16_FLOAT;
        return format;
    }
}
