package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.frontend.FrontendRenderPipeline;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.pipeline.IrisPipelines;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import xyz.rrtt217.HDRMod.mixin.features.PipelineCacheAccessor;
import xyz.rrtt217.HDRMod.mixin.features.RenderPipelineAccessor;
import xyz.rrtt217.HDRMod.mixin.features.RenderSystemAccessor;

import java.lang.reflect.Constructor;
import java.util.*;

import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasIris;


public class RenderPipelineFormatModifier {
    public record PipelineFormatModifyCacheKey(CompiledRenderPipeline compiledRenderPipeline, List<GpuFormat> gpuFormats) {}
    private static final Map<PipelineFormatModifyCacheKey, CompiledRenderPipeline> pipelineFormatModifyCache = new HashMap<PipelineFormatModifyCacheKey, CompiledRenderPipeline>();

    private static List<PipelineCache> pipelineCaches = new ArrayList<PipelineCache>();
    private static Constructor<RenderPipeline> pipelineConstructor;
    public static void savePipelineCache(PipelineCache cache){
        pipelineCaches.add(cache);
    }

    // Loosen "equal" standard for matching pipelines so that a pipeline modified by Iris can still be matched.
    // The keys are exactly the ones that Iris do not modify.
    record MatchKey(
            String name,
            boolean wantsDepthTexture,
            int pushConstantSize,
            Object2IntMap<String> uniformIndices,
            List<BindGroupLayout.UniformDescription> uniforms,
            List<@Nullable ColorTargetState> colorTargetStates
    ) {
        static MatchKey of(FrontendRenderPipeline p) {
            return new MatchKey(
                    p.name(),
                    p.wantsDepthTexture(),
                    p.pushConstantSize(),
                    p.uniformIndices(),
                    p.uniforms(),
                    p.colorTargetStates()
            );
        }
    }

    public static @Nullable RenderPipeline getRenderPipelineFromCache(PipelineCache cache, CompiledRenderPipeline compiledRenderPipeline){
        Map<RenderPipeline, CompiledRenderPipeline> currentMap = ((PipelineCacheAccessor) cache).getCache();

        MatchKey target;
        if (compiledRenderPipeline instanceof FrontendRenderPipeline fe) {
            target = MatchKey.of(fe);
        } else {
            target = null;
        }

        return currentMap.entrySet().stream()
                .filter(e -> {
                    CompiledRenderPipeline v = e.getValue();
                    if (target != null) {
                        return v instanceof FrontendRenderPipeline fe
                                && MatchKey.of(fe).equals(target);
                    }
                    return Objects.equals(v, compiledRenderPipeline);
                }
                )
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static CompiledRenderPipeline modifyRenderPipelineFormat(CompiledRenderPipeline compiledRenderPipeline, GpuFormat[] formats){
        PipelineFormatModifyCacheKey key = new PipelineFormatModifyCacheKey(compiledRenderPipeline, Arrays.asList(formats));
        if(pipelineFormatModifyCache.containsKey(key)) {
            return pipelineFormatModifyCache.get(key);
        }
        PipelineCache current = RenderSystemAccessor.getCurrentPipelineCache();
        if(current != null){
            RenderPipeline pipeline = getRenderPipelineFromCache(current, compiledRenderPipeline);
            if(pipeline != null){
                RenderPipeline modified = modifyRenderPipelineFormat(pipeline, formats);
                // Use RenderSystem.getCompiledPipelineNullable; Iris will swap it on need.
                CompiledRenderPipeline compiled = RenderSystem.getCompiledPipelineNullable(modified);
                pipelineFormatModifyCache.put(new PipelineFormatModifyCacheKey(compiledRenderPipeline, Arrays.asList(formats)), compiled);
                if(compiled != null) return compiled;
            }
        }
        for(PipelineCache cache: pipelineCaches){
            RenderPipeline pipeline = getRenderPipelineFromCache(cache, compiledRenderPipeline);
            if(pipeline != null){
                RenderPipeline modified = modifyRenderPipelineFormat(pipeline, formats);
                RenderSystem.setCurrentPipelineCache(cache);
                // Use RenderSystem.getCompiledPipelineNullable; Iris will swap it on need.
                CompiledRenderPipeline compiled1 = RenderSystem.getCompiledPipelineNullable(modified);
                RenderSystem.setCurrentPipelineCache(current);
                pipelineFormatModifyCache.put(new PipelineFormatModifyCacheKey(compiledRenderPipeline, Arrays.asList(formats)), compiled1);
                if(compiled1 != null) return compiled1;
            }
        }
        return null;
    }

    public static RenderPipeline modifyRenderPipelineFormat(RenderPipeline pipeline, GpuFormat[] formats) {
        return modifyRenderPipelineFormat(pipeline, Arrays.asList(formats));
    }
    public static RenderPipeline modifyRenderPipelineFormat(RenderPipeline pipeline, List<GpuFormat> formats) {
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

            if(hasIris){
                IrisPipelines.copyPipeline(pipeline, result);
            }

            return result;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create modified RenderPipeline for " + pipeline, e);
        }
    }
}
