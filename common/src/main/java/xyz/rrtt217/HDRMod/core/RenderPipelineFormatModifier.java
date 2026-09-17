package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.rrtt217.HDRMod.mixin.features.PipelineCacheAccessor;
import xyz.rrtt217.HDRMod.mixin.features.RenderPipelineAccessor;
import xyz.rrtt217.HDRMod.mixin.features.RenderSystemAccessor;

import java.lang.reflect.Constructor;
import java.util.*;


public class RenderPipelineFormatModifier {
    public record PipelineFormatModifyCacheKey(RenderPipeline renderPipeline, List<GpuFormat> gpuFormats) {}
    private static final Map<PipelineFormatModifyCacheKey,RenderPipeline> pipelineFormatModifyCache = new HashMap<PipelineFormatModifyCacheKey,RenderPipeline>();

    private static List<PipelineCache> pipelineCaches = new ArrayList<PipelineCache>();
    private static Constructor<RenderPipeline> pipelineConstructor;
    public static void savePipelineCache(PipelineCache cache){
        pipelineCaches.add(cache);
    }

    public static @Nullable RenderPipeline getRenderPipelineFromCache(PipelineCache cache, CompiledRenderPipeline compiledRenderPipeline){
        Map<RenderPipeline, CompiledRenderPipeline> currentMap = ((PipelineCacheAccessor) cache).getCache();
        List<RenderPipeline> pipelines = currentMap.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(),compiledRenderPipeline)).map(Map.Entry::getKey).toList();
        if(pipelines.isEmpty()) return null;
        else return pipelines.getFirst();
    }

    public static CompiledRenderPipeline modifyRenderPipelineFormat(CompiledRenderPipeline compiledRenderPipeline, GpuFormat[] formats){
        PipelineCache current = RenderSystemAccessor.getCurrentPipelineCache();
        if(current != null){
            RenderPipeline pipeline = getRenderPipelineFromCache(current, compiledRenderPipeline);
            if(pipeline != null){
                RenderPipeline modified = modifyRenderPipelineFormat(pipeline, formats);
                return RenderSystem.getCompiledPipeline(modified);
            }
        }
        for(PipelineCache cache: pipelineCaches){
            RenderPipeline pipeline = getRenderPipelineFromCache(cache, compiledRenderPipeline);
            if(pipeline != null){
                RenderPipeline modified = modifyRenderPipelineFormat(pipeline, formats);
                return RenderSystem.getCompiledPipeline(modified);
            }
        }
        return null;
    }

    public static RenderPipeline modifyRenderPipelineFormat(RenderPipeline pipeline, GpuFormat[] formats) {
        return modifyRenderPipelineFormat(pipeline, Arrays.asList(formats));
    }
    public static RenderPipeline modifyRenderPipelineFormat(RenderPipeline pipeline, List<GpuFormat> formats) {
        PipelineFormatModifyCacheKey cacheKey = new PipelineFormatModifyCacheKey(pipeline, formats);
        RenderPipeline cached = pipelineFormatModifyCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<ColorTargetState> original = pipeline.getColorTargetStates();
        if (original.size() != formats.size()) {
            throw new IllegalArgumentException("Color target state count mismatch: pipeline has " + original.size() + " but " + formats.size() + " formats were provided");
        }

        ColorTargetState[] modified = new ColorTargetState[original.size()];
        for (int i = 0; i < original.size(); i++) {
            ColorTargetState state = original.get(i);
            modified[i] = new ColorTargetState(state.blendFunction(), formats.get(i), state.writeMask());
            //LOGGER.info("Original Format {}: {}", i, state.format());
            //LOGGER.info("Modified Format {}: {}", i, formats.get(i).toString());
        }

        try {
            if (pipelineConstructor == null) {
                pipelineConstructor = RenderPipeline.class.getDeclaredConstructor(
                        Identifier.class, Map.class, ShaderDefines.class,
                        Collection.class, ColorTargetState[].class, DepthStencilState.class,
                        PolygonMode.class, boolean.class, VertexFormat[].class,
                        PrimitiveTopology.class, int.class, int.class);
                pipelineConstructor.setAccessible(true);
            }

            RenderPipeline result = pipelineConstructor.newInstance(
                    pipeline.getLocation(),
                    pipeline.getShaders(),
                    pipeline.getShaderDefines(),
                    pipeline.getBindGroupLayouts(),
                    modified,
                    pipeline.getDepthStencilState(),
                    pipeline.getPolygonMode(),
                    pipeline.isCull(),
                    ((RenderPipelineAccessor) pipeline).getVertexFormatPerBuffer().toArray(new VertexFormat[0]),
                    pipeline.getPrimitiveTopology(),
                    pipeline.pushConstantSize(),
                    pipeline.getSortKey());

            pipelineFormatModifyCache.put(cacheKey, result);

            /*
             * Some mods (like vitrail) use custom ShaderSource when calling GpuDevice.precompilePipeline() by themselves, which is reasonable.
             * We capture the custom ShaderSource they use when they call GpuDevice.precompilePipeline(), and call GpuDevice.precompilePipeline()
             * again by us when modifyRenderPipelineFormat is called, using their custom ShaderSource, so no "Couldn't find source" will happen
             * when rebuilding programs on-the-fly with fixed ColorTargetState format.
             */
            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create modified RenderPipeline for " + pipeline, e);
        }
    }
}
