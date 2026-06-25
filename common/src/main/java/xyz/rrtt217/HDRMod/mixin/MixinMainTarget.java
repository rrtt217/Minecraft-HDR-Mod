package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.MainTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;<init>(Ljava/lang/String;ZLcom/mojang/blaze3d/GpuFormat;)V"), index = 2)
    private static GpuFormat hdr_mod$upgradeOnCreateBuffers(GpuFormat format) {
        return GpuFormat.RGBA16_FLOAT;
    }
}
