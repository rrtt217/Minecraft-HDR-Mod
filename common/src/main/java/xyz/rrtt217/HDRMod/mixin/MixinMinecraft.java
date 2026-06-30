package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import me.shedaniel.autoconfig.AutoConfig;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.util.LibraryExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

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
    @Inject(method = "loadCriticalShaders()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;preloadUiShader(Lnet/minecraft/server/packs/resources/ResourceProvider;)V",shift = At.Shift.AFTER))
    private void hdr_mod$preloadBeforeBlitShader(CallbackInfo ci) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        ShaderSource shaderSource = (identifier, shaderType) -> {
            Identifier identifier2 = shaderType.idConverter().idToFile(identifier);

            try (Reader reader = vanillaPackResources.asProvider().getResourceOrThrow(identifier2).openAsReader()) {
                return IOUtils.toString(reader);
            } catch (IOException iOException) {
                // small hack
                if(identifier.equals(Identifier.fromNamespaceAndPath("hdr_mod","color_transform"))) {
                    ClassLoader loader = LibraryExtractor.class.getClassLoader();
                    try(InputStream is = loader.getResourceAsStream("assets/hdr_mod/shaders/color_transform.fsh")){
                        if (is != null) {
                            return new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
                        }
                    }
                    catch (IOException ignored) {
                    }
                }
                LOGGER.error("Couldn't preload {} shader {}: {}", new Object[]{shaderType, identifier, iOException});
                return null;
            }
        };
        gpuDevice.precompilePipeline(ColorTransformRenderer.COLOR_TRANSFORM, shaderSource);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void hdr_mod$setupMinecraft(CallbackInfo ci) {
        minecraft = Minecraft.getInstance();
    }

    @Inject(method = "renderFrame(Z)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;getColorTextureView()Lcom/mojang/blaze3d/textures/GpuTextureView;"))
    private void hdr_mod$presentationColorTransformRenderer(CallbackInfo ci) {
        RenderSystem.assertOnRenderThread();

    }

    @ModifyArg(method = "renderFrame(Z)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurface;blitFromTexture(Lcom/mojang/blaze3d/systems/CommandEncoder;Lcom/mojang/blaze3d/textures/GpuTextureView;)V"), index = 1)
    private GpuTextureView hdr_mod$presentationColorTransformRenderer(GpuTextureView textureView) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();

        if(HDRMod.PresentationColorTransformRenderer == null)
            HDRMod.PresentationColorTransformRenderer = new ColorTransformRenderer(textureView, "Presentation");

        HDRMod.PresentationColorTransformRenderer.updateColorTransformUniforms(
                config.uiBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.uiBrightness, // For UI Brightness
                config.customEotfEmulate < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.customEotfEmulate,
                config.autoSetPrimaries ? GLFWColorManagementUtils.glfwGetWindowPrimaries(Minecraft.getInstance().getWindow().handle()) : config.customPrimaries.getId(),
                config.autoSetTransferFunction ? GLFWColorManagementUtils.glfwGetWindowTransfer(Minecraft.getInstance().getWindow().handle()) : config.customTransferFunction.getId()
        );
        if (minecraft.gameRenderer.mainRenderTarget().getColorTextureView() != null && !textureView.equals(HDRMod.PresentationColorTransformRenderer.getSrcTextureView()))
            HDRMod.PresentationColorTransformRenderer.setSrcSrcTextureView(textureView);
        HDRMod.PresentationColorTransformRenderer.render();

        if(!config.forceDisableBeforeBlitPipeline) return PresentationColorTransformRenderer.getDstTextureView();
        return textureView;
    }
}
