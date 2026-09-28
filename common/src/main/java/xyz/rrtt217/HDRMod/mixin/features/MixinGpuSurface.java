package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.frontend.FrontendGpuSurface;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLVideo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.color.WaylandSDLColorManagementInfoProvider;

import java.lang.foreign.MemorySegment;
import java.util.Objects;

import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.*;
import static xyz.rrtt217.HDRMod.HDRMod.*;

@Mixin(FrontendGpuSurface.class)
public class MixinGpuSurface {
    @Unique
    private boolean firstPresent = true;


    @ModifyArg(method = "blitFromTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/backend/api/GpuSurfaceBackend;blitFromTexture(Lcom/mojang/renderpearl/backend/api/CommandEncoderBackend;Lcom/mojang/renderpearl/api/textures/GpuTextureView;)V"), index = 1)
    private GpuTextureView hdr_mod$beforePresentationColorTransform(GpuTextureView textureView) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        long handle = Minecraft.getInstance().getWindow().handle();

        // Upgrade to WaylandSDLColorManagementInfoProvider if we are on OpenGL.
        if(firstPresent) {
            GpuDevice device = RenderSystem.tryGetDevice();
            if (Objects.equals(SDLVideo.SDL_GetCurrentVideoDriver(), "wayland") && device != null && device.getDeviceInfo().backendName().toLowerCase().contains("gl")) {
                try {
                    long wlDisplayHandle = SDL_GetPointerProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_WAYLAND_DISPLAY_POINTER, 0);
                    long wlSurfaceHandle = SDL_GetPointerProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_WAYLAND_SURFACE_POINTER, 0);
                    LOGGER.info("WLDisplayHandle: " + wlDisplayHandle);
                    LOGGER.info("WLSurfaceHandle: " + wlSurfaceHandle);
                    HDRMod.colorManagementInfoProvider = new WaylandSDLColorManagementInfoProvider(MemorySegment.ofAddress(wlDisplayHandle), MemorySegment.ofAddress(wlSurfaceHandle), 3000);
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to load color management info", throwable);
                }
            }
            int bpc = HDRMod.colorManagementInfoProvider.getBitsPerChannel(handle);
            float SDRWhiteLevel = HDRMod.colorManagementInfoProvider.getWindowSdrWhiteLevel(handle);
            float maxLuminance = HDRMod.colorManagementInfoProvider.getWindowMaxLuminance(handle);
            float minLuminance = HDRMod.colorManagementInfoProvider.getWindowMinLuminance(handle);
            Enums.Primaries primaries = HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle);
            Enums.TransferFunction tf = HDRMod.colorManagementInfoProvider.getWindowTransferFunction(handle);
            LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ", bpc, SDRWhiteLevel, maxLuminance, minLuminance, primaries, tf);
            firstPresent = false;
        }

        if(HDRMod.PresentationColorTransformRenderer == null)
            HDRMod.PresentationColorTransformRenderer = new ColorTransformRenderer(textureView, "Presentation");

        HDRMod.PresentationColorTransformRenderer.updateColorTransformUniforms(
                HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle),
                HDRMod.colorManagementInfoProvider.getCurrentTransferFunction(handle)
        );

        if(minecraft == null) minecraft = Minecraft.getInstance();

        if (minecraft.gameRenderer.mainRenderTarget().getColorTextureView() != null && !textureView.equals(HDRMod.PresentationColorTransformRenderer.getSrcTextureView()))
            HDRMod.PresentationColorTransformRenderer.setSrcTextureView(textureView);
        HDRMod.PresentationColorTransformRenderer.render();

        if(!config.forceDisableBeforeBlitPipeline) return PresentationColorTransformRenderer.getDstTextureView();
        return textureView;
    }
}
