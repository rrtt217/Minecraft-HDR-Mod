package xyz.rrtt217.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.TextureFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderTarget.class)
public class MixinRenderTarget {
    // This is useless now, since Mojang doesn't add any other texture format than RGBA8, RED8, RED8I and DEPTH32.
    @ModifyArg(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuDevice;createTexture(Ljava/util/function/Supplier;ILcom/mojang/blaze3d/textures/TextureFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;", ordinal = 1), index = 2)
    private TextureFormat hdr_mod$replaceColorBufferFormat(TextureFormat textureFormat)
    {
        // Here we mark
        if(textureFormat == TextureFormat.RGBA8) return TextureFormat.RGBA8;
        return textureFormat;
    }
}
