package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.core.RenderPipelineFormatModifier;

@Mixin(PipelineCache.class)
public class MixinPipelineCache {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void hdr_mod$saveEveryPipelineCache(GpuDevice device, ShaderSource shaderSource, CallbackInfo ci){
        RenderPipelineFormatModifier.savePipelineCache((PipelineCache) (Object) this);
    }
}
