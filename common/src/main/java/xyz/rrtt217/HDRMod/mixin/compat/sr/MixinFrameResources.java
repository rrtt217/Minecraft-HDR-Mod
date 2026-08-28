package xyz.rrtt217.HDRMod.mixin.compat.sr;

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
import xyz.rrtt217.HDRMod.api.color.Enums;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(FrameResources.class)
public class MixinFrameResources {
    private ColorTransformRenderer finalColorTransformRenderer;
    private ColorTransformRenderer hudlessColorTransformRenderer;
    @ModifyArg(method = "copyFinalColor", at = @At(value = "INVOKE", target = "Lio/homo/superresolution/common/presentation/capture/FrameTextureResource;copyFrom(Lio/homo/superresolution/core/graphics/impl/texture/ITexture;Z)V"), index = 0)
    private ITexture hdr_mod$transformFinalColorTexture(ITexture texture) {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        try {
            if(finalColorTransformRenderer == null) {
                finalColorTransformRenderer = new ColorTransformRenderer(((FrameBufferTextureAdapterAccessor) texture).getFrameBuffer().asMcRenderTarget(), "SR Final Color");
                finalColorTransformRenderer.updateColorTransformUniforms(
                    HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                    HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                    Enums.Primaries.BT2020,
                    Enums.TransferFunction.ST2084_PQ
                );
            }
            finalColorTransformRenderer.render();
            return new GlOnlyNameTexture(() -> TextureFormat.RGBA16, () -> finalColorTransformRenderer.getSrcTarget().width, () -> finalColorTransformRenderer.getSrcTarget().height, () -> (long)(finalColorTransformRenderer.getDstTextureId()));
        } catch (Exception e) {
            LOGGER.warn("Error while trying to render gl texture", e);
            return texture;
        }
    }
    @ModifyArg(method = "copyHudlessColor", at = @At(value = "INVOKE", target = "Lio/homo/superresolution/common/presentation/capture/FrameTextureResource;copyFrom(Lio/homo/superresolution/core/graphics/impl/texture/ITexture;Z)V"), index = 0)
    private ITexture hdr_mod$transformHudlessColorTexture(ITexture texture) {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        try {
            if(hudlessColorTransformRenderer == null) {
                hudlessColorTransformRenderer = new ColorTransformRenderer(((FrameBufferTextureAdapterAccessor) texture).getFrameBuffer().asMcRenderTarget(), "SR Hudless Color");
                hudlessColorTransformRenderer.updateColorTransformUniforms(
                        HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                        HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                        Enums.Primaries.BT2020,
                        Enums.TransferFunction.ST2084_PQ
                );
            }
            hudlessColorTransformRenderer.render();
            return new GlOnlyNameTexture(() -> TextureFormat.RGBA16, () -> hudlessColorTransformRenderer.getSrcTarget().width, () -> hudlessColorTransformRenderer.getSrcTarget().height, () -> (long) hudlessColorTransformRenderer.getDstTextureId());
        } catch (Exception e) {
            LOGGER.warn("Error while trying to render gl texture", e);
            return texture;
        }
    }
}
