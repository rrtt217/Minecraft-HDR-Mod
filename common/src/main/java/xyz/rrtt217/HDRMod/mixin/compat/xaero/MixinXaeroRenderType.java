package xyz.rrtt217.HDRMod.mixin.compat.xaero;

import com.mojang.blaze3d.GpuFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xaero.lib.client.graphics.XaeroRenderType;

@Mixin(XaeroRenderType.class)
public class MixinXaeroRenderType {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1)
    private static GpuFormat hdr_mod$modifyXaeroColorTargetStateFormat(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM) { return GpuFormat.RGBA16_FLOAT; }
        return format;
    }
}
