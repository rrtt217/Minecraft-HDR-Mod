package xyz.rrtt217.HDRMod.mixin.upgrade;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.blaze3d.pipeline.MainTarget;
import dev.architectury.injectables.annotations.PlatformOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @PlatformOnly(PlatformOnly.FABRIC)
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;<init>(Ljava/lang/String;Lcom/mojang/renderpearl/api/GpuFormat;Lcom/mojang/renderpearl/api/GpuFormat;)V"), index = 1)
    private static GpuFormat hdr_mod$upgradeOnCreateBuffers(GpuFormat format) {
        return GpuFormat.RGBA16_FLOAT;
    }

    // DO NOT WORK YET
    @PlatformOnly("neoforge")
    @ModifyArg(method = "<init>(IIZ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;<init>(Ljava/lang/String;Lcom/mojang/renderpearl/api/GpuFormat;Lcom/mojang/renderpearl/api/GpuFormat;)V"), index = 3)
    private static GpuFormat hdr_mod$upgradeOnCreateBuffersNeoForge(GpuFormat format) {
        return GpuFormat.RGBA16_FLOAT;
    }
}
