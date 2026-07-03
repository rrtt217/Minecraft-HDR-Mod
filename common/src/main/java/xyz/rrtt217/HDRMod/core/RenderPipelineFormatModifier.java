package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import xyz.rrtt217.HDRMod.mixin.RenderPipelineAccessor;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderPipelineFormatModifier {
    public record PipelineCacheKey(RenderPipeline renderPipeline, List<GpuFormat> gpuFormats) {}
    private static final Map<PipelineCacheKey,RenderPipeline> pipelineCache = new HashMap<PipelineCacheKey,RenderPipeline>();
    private static Constructor<RenderPipeline> pipelineConstructor;
    public static RenderPipeline modifyRenderPipelineFormat(RenderPipeline pipeline, GpuFormat[] formats) {
        return modifyRenderPipelineFormat(pipeline, Arrays.asList(formats));
    }
    public static RenderPipeline modifyRenderPipelineFormat(RenderPipeline pipeline, List<GpuFormat> formats) {
        PipelineCacheKey cacheKey = new PipelineCacheKey(pipeline, formats);
        RenderPipeline cached = pipelineCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        ColorTargetState[] original = pipeline.getColorTargetStates();
        if (original.length != formats.size()) {
            throw new IllegalArgumentException("Color target state count mismatch: pipeline has " + original.length + " but " + formats.size() + " formats were provided");
        }

        ColorTargetState[] modified = new ColorTargetState[original.length];
        for (int i = 0; i < original.length; i++) {
            ColorTargetState state = original[i];
            modified[i] = new ColorTargetState(state.blendFunction(), formats.get(i), state.writeMask());
        }

        try {
            if (pipelineConstructor == null) {
                pipelineConstructor = RenderPipeline.class.getDeclaredConstructor(
                        Identifier.class, Identifier.class, Identifier.class, ShaderDefines.class,
                        List.class, ColorTargetState[].class, DepthStencilState.class,
                        PolygonMode.class, boolean.class, VertexFormat[].class,
                        PrimitiveTopology.class, int.class);
                pipelineConstructor.setAccessible(true);
            }

            RenderPipeline result = pipelineConstructor.newInstance(
                    pipeline.getLocation(),
                    pipeline.getVertexShader(),
                    pipeline.getFragmentShader(),
                    pipeline.getShaderDefines(),
                    pipeline.getBindGroupLayouts(),
                    modified,
                    pipeline.getDepthStencilState(),
                    pipeline.getPolygonMode(),
                    pipeline.isCull(),
                    ((RenderPipelineAccessor) pipeline).getVertexFormatPerBuffer(),
                    pipeline.getPrimitiveTopology(),
                    pipeline.getSortKey());

            pipelineCache.put(cacheKey, result);
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create modified RenderPipeline for " + pipeline, e);
        }
    }
}
