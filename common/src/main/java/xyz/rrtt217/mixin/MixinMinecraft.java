package xyz.rrtt217.mixin;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.VanillaPackResources;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.core.BeforeBlitRenderer;
import xyz.rrtt217.util.LibraryExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static xyz.rrtt217.HDRMod.LOGGER;

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
    @Inject(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;preloadUiShader(Lnet/minecraft/server/packs/resources/ResourceProvider;)V",shift = At.Shift.AFTER))
    private void hdr_mod$preloadBeforeBlitShader(CallbackInfo ci) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        ShaderSource shaderSource = (identifier, shaderType) -> {
            Identifier identifier2 = shaderType.idConverter().idToFile(identifier);

            try (Reader reader = vanillaPackResources.asProvider().getResourceOrThrow(identifier2).openAsReader()) {
                return IOUtils.toString(reader);
            } catch (IOException iOException) {
                // small hack
                if(identifier.equals(Identifier.fromNamespaceAndPath("hdr_mod","before_blit"))) {
                    ClassLoader loader = LibraryExtractor.class.getClassLoader();
                    try(InputStream is = loader.getResourceAsStream("assets/hdr_mod/shaders/before_blit.fsh")){
                        if (is != null) {
                            return new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
                        }
                    }
                    catch (IOException ignored) {
                    }
                }
                LOGGER.error("Coudln't preload {} shader {}: {}", new Object[]{shaderType, identifier, iOException});
                return null;
            }
        };
        gpuDevice.precompilePipeline(BeforeBlitRenderer.BEFORE_BLIT, shaderSource);
        BeforeBlitRenderer.isBeforeBlitReady = true;
    }
}
