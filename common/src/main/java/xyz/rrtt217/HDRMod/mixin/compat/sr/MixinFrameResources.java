package xyz.rrtt217.HDRMod.mixin.compat.sr;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.homo.superresolution.common.minecraft.GpuTextureAdapter;
import io.homo.superresolution.common.presentation.capture.FrameResources;
import io.homo.superresolution.core.graphics.impl.texture.ITexture;
import io.homo.superresolution.core.graphics.impl.texture.TextureFormat;
import io.homo.superresolution.core.graphics.opengl.framebuffer.GlOnlyNameTexture;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.color.Enums;

import java.lang.reflect.Constructor;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(FrameResources.class)
public class MixinFrameResources {
    private ColorTransformRenderer finalColorTransformRenderer;
    private GpuTextureView finalColorTextureView;
    private ColorTransformRenderer hudlessColorTransformRenderer;
    private GpuTextureView hudlessColorTextureView;
    private Constructor gpuTextureAdapterConstructor;
    @ModifyArg(method = "copyFinalColor", at = @At(value = "INVOKE", target = "Lio/homo/superresolution/common/presentation/capture/FrameTextureResource;copyFrom(Lio/homo/superresolution/core/graphics/impl/texture/ITexture;Z)V"), index = 0)
    private ITexture hdr_mod$transformFinalColorTexture(ITexture texture) {
        long handle = Minecraft.getInstance().getWindow().handle();
        if(gpuTextureAdapterConstructor == null) {
            try {
                Class<?> clazz = GpuTextureAdapter.class;
                gpuTextureAdapterConstructor = clazz.getDeclaredConstructor(ITexture.class);
                gpuTextureAdapterConstructor.setAccessible(true);
            }
            catch (NoSuchMethodException e){
                return texture;
            }
        }
        try {
            GpuTextureAdapter adapter = (GpuTextureAdapter) gpuTextureAdapterConstructor.newInstance(texture);
            if(finalColorTextureView != null && finalColorTextureView.texture() != adapter){
                finalColorTextureView.close();
                finalColorTextureView = null;
            }
            if(finalColorTextureView == null) {
                finalColorTextureView = RenderSystem.getDevice().createTextureView(adapter);
                if(finalColorTransformRenderer != null)
                    finalColorTransformRenderer.setSrcTextureView(finalColorTextureView);
            }
            if(finalColorTransformRenderer == null) {
                finalColorTransformRenderer = new ColorTransformRenderer(finalColorTextureView, "SR Final Color");
                finalColorTransformRenderer.updateColorTransformUniforms(
                    HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                    HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                    Enums.Primaries.BT2020,
                    Enums.TransferFunction.ST2084_PQ
                );
            }
            finalColorTransformRenderer.render();
            return new GlOnlyNameTexture(() -> TextureFormat.RGBA16, () -> finalColorTransformRenderer.getDstTexture().getWidth(0), () -> finalColorTransformRenderer.getDstTexture().getHeight(0), () -> (long)((GlTexture) finalColorTransformRenderer.getDstTexture()).glId());
        } catch (Exception e) {
            LOGGER.warn("Error while trying to render gl texture", e);
            return texture;
        }
    }
    @ModifyArg(method = "copyHudlessColor", at = @At(value = "INVOKE", target = "Lio/homo/superresolution/common/presentation/capture/FrameTextureResource;copyFrom(Lio/homo/superresolution/core/graphics/impl/texture/ITexture;Z)V"), index = 0)
    private ITexture hdr_mod$transformHudlessColorTexture(ITexture texture) {
        long handle = Minecraft.getInstance().getWindow().handle();
        if(gpuTextureAdapterConstructor == null) {
            try {
                Class<?> clazz = GpuTextureAdapter.class;
                gpuTextureAdapterConstructor = clazz.getDeclaredConstructor(ITexture.class);
                gpuTextureAdapterConstructor.setAccessible(true);
            }
            catch (NoSuchMethodException e){
                return texture;
            }
        }
        try {
            GpuTextureAdapter adapter = (GpuTextureAdapter) gpuTextureAdapterConstructor.newInstance(texture);
            if(hudlessColorTextureView != null && hudlessColorTextureView.texture() != adapter){
                hudlessColorTextureView.close();
                hudlessColorTextureView = null;
            }
            if(hudlessColorTextureView == null) {
                hudlessColorTextureView = RenderSystem.getDevice().createTextureView(adapter);
                if(hudlessColorTransformRenderer != null)
                    hudlessColorTransformRenderer.setSrcTextureView(hudlessColorTextureView);
            }
            if(hudlessColorTransformRenderer == null) {
                hudlessColorTransformRenderer = new ColorTransformRenderer(hudlessColorTextureView, "SR Hudless Color");
                hudlessColorTransformRenderer.updateColorTransformUniforms(
                        HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                        HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                        Enums.Primaries.BT2020,
                        Enums.TransferFunction.ST2084_PQ
                );
            }
            hudlessColorTransformRenderer.render();
            return new GlOnlyNameTexture(() -> TextureFormat.RGBA16, () -> hudlessColorTransformRenderer.getDstTexture().getWidth(0), () -> hudlessColorTransformRenderer.getDstTexture().getHeight(0), () -> (long)((GlTexture) hudlessColorTransformRenderer.getDstTexture()).glId());
        } catch (Exception e) {
            LOGGER.warn("Error while trying to render gl texture", e);
            return texture;
        }
    }
}
