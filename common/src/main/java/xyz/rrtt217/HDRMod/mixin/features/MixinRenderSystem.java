package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.core.RenderPipelineFormatModifier;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {
    @Inject(method = "setCurrentPipelineCache", at = @At("TAIL"))
    private static void hdr_mod$saveCurrentPipelineCache(PipelineCache pipelineCache, CallbackInfoReturnable<PipelineCache> cir) {
        RenderPipelineFormatModifier.savePipelineCache(pipelineCache);
    }
    @Inject(method = "setFallbackPipelineCache", at = @At("TAIL"))
    private static void hdr_mod$saveFallbackPipelineCache(PipelineCache pipelineCache, CallbackInfo ci) {
        RenderPipelineFormatModifier.savePipelineCache(pipelineCache);
    }
}
