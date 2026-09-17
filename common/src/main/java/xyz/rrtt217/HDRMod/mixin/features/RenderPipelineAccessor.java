package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RenderPipeline.class)
public interface RenderPipelineAccessor {
    @Accessor
    List<VertexFormat> getVertexFormatPerBuffer();
}
