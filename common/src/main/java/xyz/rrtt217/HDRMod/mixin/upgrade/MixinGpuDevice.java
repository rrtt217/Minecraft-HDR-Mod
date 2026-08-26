package xyz.rrtt217.HDRMod.mixin.upgrade;

import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.core.RenderPipelineFormatModifier;

@Mixin(GpuDevice.class)
public class MixinGpuDevice {
    @Inject(method = "precompilePipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/shaders/ShaderSource;)Lcom/mojang/blaze3d/pipeline/CompiledRenderPipeline;", at = @At("HEAD"))
    private void hdr_mod$saveShaderSourceMap(RenderPipeline pipeline, ShaderSource shaderSource, CallbackInfoReturnable<CompiledRenderPipeline> cir){
        RenderPipelineFormatModifier.savePipelineSource(pipeline, shaderSource);
    }
}
