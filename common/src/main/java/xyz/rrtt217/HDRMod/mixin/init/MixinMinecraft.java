package xyz.rrtt217.HDRMod.mixin.init;

import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.VanillaPackResources;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static xyz.rrtt217.HDRMod.HDRMod.*;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Mutable
    @Final
    @Shadow
    private final VanillaPackResources vanillaPackResources;
    public MixinMinecraft(VanillaPackResources vanillaPackResources) {
        this.vanillaPackResources = vanillaPackResources;
    }

    // Similar to preloadUiShader, because common resource manager have not yet initialized at this time.
    @Inject(method = "loadCriticalShaders()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;preloadUiShader(Lnet/minecraft/server/packs/resources/ResourceManager;)V",shift = At.Shift.AFTER))
    private void hdr_mod$preloadBeforeBlitShader(CallbackInfo ci) {
        GpuDevice device = RenderSystem.getDevice();
        final Map<Identifier, ShaderSource.CachedIncludeSource> includes = ShaderManager.listAllIncludes(this.vanillaPackResources.asResourceManager());
        ShaderSource shaderSource = new ShaderSource() {
            public @Nullable String getShader(final Identifier id, final ShaderType type) {
                Identifier location = type.idConverter().idToFile(id);
                try {
                    return vanillaPackResources.asResourceManager().getResourceOrThrow(location).readAllAsString();
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

        PipelineCache old = RenderSystem.setCurrentPipelineCache(new PipelineCache(device, shaderSource));
        RenderSystem.getCompiledPipeline(ColorTransformRenderer.COLOR_TRANSFORM);
        RenderSystem.getCompiledPipeline(ColorTransformRenderer.COLOR_TRANSFORM_PQ);
        //RenderSystem.setCurrentPipelineCache(old);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void hdr_mod$setupMinecraft(CallbackInfo ci) {
        minecraft = Minecraft.getInstance();
    }
}
