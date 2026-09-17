package xyz.rrtt217.HDRMod.mixin.upgrade;

import com.mojang.renderpearl.api.GpuFormat;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PostChain.class)
public class MixinPostChain {
    @ModifyArg(method = "addToFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/resource/RenderTargetDescriptor$TextureProperties;<init>(Lorg/joml/Vector4fc;Lcom/mojang/renderpearl/api/GpuFormat;)V"), index = 1)
    private GpuFormat hdr_mod$upgradePostChainRenderTargetDescriptor(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM) {return GpuFormat.RGBA16_FLOAT;}
        return format;
    }
}
