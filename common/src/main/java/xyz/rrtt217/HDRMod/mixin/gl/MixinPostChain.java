package xyz.rrtt217.HDRMod.mixin.gl;

import com.mojang.blaze3d.GpuFormat;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PostChain.class)
public class MixinPostChain {
    @ModifyArg(method = "addToFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/resource/RenderTargetDescriptor;<init>(IIZLorg/joml/Vector4fc;Lcom/mojang/blaze3d/GpuFormat;)V"), index = 4)
    private GpuFormat hdr_mod$upgradePostChainRenderTargetDescriptor(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM) {return GpuFormat.RGBA16_FLOAT;}
        return format;
    }
}
