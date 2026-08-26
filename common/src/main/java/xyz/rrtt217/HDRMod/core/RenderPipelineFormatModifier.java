package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import xyz.rrtt217.HDRMod.mixin.features.RenderPipelineAccessor;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class RenderPipelineFormatModifier {
    public record PipelineCacheKey(RenderPipeline renderPipeline, List<GpuFormat> gpuFormats) {}
    private static final Map<PipelineCacheKey,RenderPipeline> pipelineCache = new HashMap<PipelineCacheKey,RenderPipeline>();
    private static final Map<RenderPipeline, ShaderSource> pipelineSourceMap = new HashMap<>();
    private static Constructor<RenderPipeline> pipelineConstructor;
    public static void savePipelineSource(RenderPipeline pipeline, ShaderSource shaderSource) {
        pipelineSourceMap.put(pipeline, shaderSource);
    }
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
            //LOGGER.info("Original Format {}: {}", i, state.format());
            //LOGGER.info("Modified Format {}: {}", i, formats.get(i).toString());
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

            /*
             * Some mods (like vitrail) use custom ShaderSource when calling GpuDevice.precompilePipeline() by themselves, which is reasonable.
             * We capture the custom ShaderSource they use when they call GpuDevice.precompilePipeline(), and call GpuDevice.precompilePipeline()
             * again by us when modifyRenderPipelineFormat is called, using their custom ShaderSource, so no "Couldn't find source" will happen
             * when rebuilding programs on-the-fly with fixed ColorTargetState format.
             */

            ShaderSource customSource = pipelineSourceMap.get(pipeline);
            if (customSource != null) {
                GpuDevice device = RenderSystem.tryGetDevice();
                if (device != null) {
                    device.precompilePipeline(result, customSource);
                }
            }
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create modified RenderPipeline for " + pipeline, e);
        }
    }
}
