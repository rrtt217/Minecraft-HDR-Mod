package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPipeline.class)
public interface RenderPipelineAccessor {
    @Accessor
    VertexFormat[] getVertexFormatPerBuffer();
}
