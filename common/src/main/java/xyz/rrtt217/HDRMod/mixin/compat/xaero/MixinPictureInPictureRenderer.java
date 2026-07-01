package xyz.rrtt217.HDRMod.mixin.compat.xaero;

import com.mojang.blaze3d.GpuFormat;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PictureInPictureRenderer.class)
public class MixinPictureInPictureRenderer {
    @ModifyArg(method = "prepareTexturesAndProjection", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuDevice;createTexture(Ljava/util/function/Supplier;ILcom/mojang/blaze3d/GpuFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;"), index = 2)
    private GpuFormat hdr_mod$upgradeXaeroPipTexture(GpuFormat gpuFormat) {
        if(gpuFormat == GpuFormat.RGBA8_UNORM) { return GpuFormat.RGBA16_FLOAT; }
        return gpuFormat;
    }
}
