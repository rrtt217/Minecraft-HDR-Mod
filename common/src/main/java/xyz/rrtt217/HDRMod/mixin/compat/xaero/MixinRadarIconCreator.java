package xyz.rrtt217.HDRMod.mixin.compat.xaero;

import com.mojang.blaze3d.GpuFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;

@Mixin(RadarIconCreator.class)
public class MixinRadarIconCreator {
    @ModifyArg(method = "initFramebuffers", at = @At(value = "INVOKE", target = "Lxaero/common/graphics/ImprovedFramebuffer;<init>(IIZLcom/mojang/blaze3d/GpuFormat;)V"), index = 3)
    private GpuFormat hdr_mod$upgradeXaeroFramebuffers(GpuFormat gpuFormat) {
        if(gpuFormat == GpuFormat.RGBA8_UNORM) { return GpuFormat.RGBA16_FLOAT; }
        return gpuFormat;
    }
}
