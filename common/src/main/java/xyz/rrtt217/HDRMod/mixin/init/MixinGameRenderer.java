package xyz.rrtt217.HDRMod.mixin.init;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @ModifyArg(method = "preloadUiShader", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setFallbackPipelineCache(Lcom/mojang/blaze3d/pipeline/PipelineCache;)V"))
    private static PipelineCache hdr_mod$setFallbackPipelineCache(PipelineCache pipelineCache, @Local(type = Map.class, ordinal = 0) Map<Identifier, ShaderSource.CachedIncludeSource> includes, @Local(argsOnly = true, ordinal = 0) ResourceManager resourceManager) {
        ShaderSource shaderSource = new ShaderSource() {
            public @Nullable String getShader(final Identifier id, final ShaderType type) {
                Identifier location = type.idConverter().idToFile(id);
                try {
                    return resourceManager.getResourceOrThrow(location).readAllAsString();
                } catch (Exception exception) {
                    // small hack
                    if(id.equals(Identifier.fromNamespaceAndPath("hdr_mod","color_transform")) && type.equals(ShaderType.FRAGMENT)) {
                        ClassLoader loader = HDRMod.class.getClassLoader();
                        try(InputStream is = loader.getResourceAsStream("assets/hdr_mod/shaders/color_transform.fsh")){
                            if (is != null) {
                                return new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
                            }
                        }
                        catch (IOException ignored) {
                        }
                    }
                    LOGGER.error("Couldn't preload shader {}", location, exception);
                    return null;
                }
            }

            public ShaderSource.@Nullable CachedIncludeSource getInclude(final Identifier id) {
                return (ShaderSource.CachedIncludeSource)includes.get(id);
            }
            public void close() {
                includes.values().forEach(ShaderSource.CachedIncludeSource::close);
            }
        };
        return new PipelineCache(RenderSystem.getDevice(), shaderSource);
    }

    @Inject(method = "preloadUiShader", at = @At("TAIL"))
    private static void hdr_mod$preloadHdrShader(final ResourceManager resourceManager, final CallbackInfo ci) {
        ColorTransformRenderer.COLOR_TRANSFORM_COMPILED = RenderSystem.getCompiledPipeline(ColorTransformRenderer.COLOR_TRANSFORM);
        ColorTransformRenderer.COLOR_TRANSFORM_COMPILED_PQ = RenderSystem.getCompiledPipeline(ColorTransformRenderer.COLOR_TRANSFORM_PQ);
    }
}
