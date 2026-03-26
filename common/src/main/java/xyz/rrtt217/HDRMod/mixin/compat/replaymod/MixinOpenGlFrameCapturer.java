package xyz.rrtt217.HDRMod.mixin.compat.replaymod;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.render.capturer.OpenGlFrameCapturer;
import com.replaymod.render.frame.OpenGlFrame;
import dev.architectury.injectables.annotations.PlatformOnly;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.Enums;


import java.io.IOException;

import static xyz.rrtt217.HDRMod.HDRMod.ReplayColorTransformRenderer;

@Mixin(OpenGlFrameCapturer.class)
public class MixinOpenGlFrameCapturer {

    @Inject(method = "renderFrame(IFLcom/replaymod/render/capturer/CaptureData;)Lcom/replaymod/render/frame/OpenGlFrame;", at = @At("HEAD"))
    private void hdr_mod$onRenderFrameBegin(CallbackInfoReturnable<OpenGlFrame> cir) {
        HDRMod.isReplayRendering = true;
    }


    @Inject(method = "renderFrame(IFLcom/replaymod/render/capturer/CaptureData;)Lcom/replaymod/render/frame/OpenGlFrame;", at = @At(value = "INVOKE", target = "Lcom/replaymod/core/versions/MCVer;popMatrix()V"))
    private void hdr_mod$doReplayModColorTransform(CallbackInfoReturnable<OpenGlFrame> cir) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport){
            if(ReplayColorTransformRenderer == null){
                try {
                    ReplayColorTransformRenderer = new ColorTransformRenderer(Minecraft.getInstance().getMainRenderTarget(), "Replay");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ReplayColorTransformRenderer.updateColorTransformUniforms(
                config.replayUIBrightness,
                0,
                Enums.Primaries.BT2020,
                Enums.TransferFunction.ST2084_PQ
            );
            ReplayColorTransformRenderer.render();
        }
        HDRMod.isReplayRendering = false;
    }
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/utils/ByteBufferPool;allocate(I)Ljava/nio/ByteBuffer;"), index = 0)
    private int hdr_mod$enlargeByteBuffer(int size){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport){
            // Byte -> Short for HDR.
            return size * 2;
        }
        return size;
    }
    @Redirect(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"))
    private void hdr_mod$bindDstTexure(RenderTarget instance, boolean bl){
        GlStateManager._glBindFramebuffer(36160, ReplayColorTransformRenderer.getDstTextureFramebufferId());
        if (bl) {
            GlStateManager._viewport(0, 0, ReplayColorTransformRenderer.getSrcTarget().viewWidth, ReplayColorTransformRenderer.getSrcTarget().viewHeight);
        }
    }
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glReadPixels(IIIIIILjava/nio/ByteBuffer;)V"), index = 5)
    private int hdr_mod$modifyReadPixelsType(int x){
        return ReplayColorTransformRenderer.getDstReadPixelFormat();
    }

    @PlatformOnly(PlatformOnly.FABRIC)
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/frame/OpenGlFrame;<init>(ILcom/replaymod/lib/de/johni0702/minecraft/gui/utils/lwjgl/ReadableDimension;ILjava/nio/ByteBuffer;)V"), index = 2)
    private int hdr_mod$modifyOpenGlFrameReturn(int bpp){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
    @PlatformOnly(PlatformOnly.FORGE)
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/frame/OpenGlFrame;<init>(ILcom/replaymod/lib/de/johni0702/minecraft/gui/utils/lwjgl/ReadableDimension;ILjava/nio/ByteBuffer;)V"), index = 2)
    private int hdr_mod$modifyOpenGlFrameReturnNeoForge(int bpp){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
}
