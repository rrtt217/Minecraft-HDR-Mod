package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.GpuFormat;
import net.minecraft.client.gui.render.GuiItemAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiItemAtlas.class)
public class MixinGuiItemAtlas {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuDevice;createTexture(Ljava/lang/String;ILcom/mojang/blaze3d/GpuFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;", ordinal = 0), index = 2)
    private GpuFormat hdr_mod$upgradeGuiAtlas(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM) {
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
    @ModifyArg(method = "computeTextureSizeFor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/DeviceLimits;maxTextureSizeForFormat(Lcom/mojang/blaze3d/GpuFormat;)I"), index = 0)
    private static GpuFormat hdr_mod$calculateTextureSize(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM) {
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
}
