package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(RenderPipelines.class)
public class MixinRenderPipelines {
    // Common.
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1)
    private static GpuFormat hdr_mod$modifyPipelinesColorTargetFormat(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
    // ANIMATE_SPRITE
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;buildSnippet()Lcom/mojang/blaze3d/pipeline/RenderPipeline$Snippet;", ordinal = 23))
    private static RenderPipeline.Snippet hdr_mod$modifyAnimationPipelineSnippet(RenderPipeline.Builder builder) {
        return RenderPipeline.builder(RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.GLOBALS).buildSnippet())
                .withVertexShader("core/animate_sprite")
                .withBindGroupLayout(BindGroupLayouts.SPRITE_ANIMATION_INFO)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                .withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA8_UNORM, 15))
                .buildSnippet();
    }
    // LIGHTMAP
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;build()Lcom/mojang/blaze3d/pipeline/RenderPipeline;", ordinal = 84))
    private static RenderPipeline builder(RenderPipeline.Builder builder) {
        return 	RenderPipeline.builder(RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.GLOBALS).buildSnippet())
                .withLocation("pipeline/lightmap")
                .withVertexShader("core/screenquad")
                .withFragmentShader("core/lightmap")
                .withBindGroupLayout(BindGroupLayouts.LIGHTMAP_INFO)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                .withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA8_UNORM, 15))
                .build();
    }
}
