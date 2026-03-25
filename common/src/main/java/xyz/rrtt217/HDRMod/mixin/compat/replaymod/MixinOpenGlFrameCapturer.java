package xyz.rrtt217.HDRMod.mixin.compat.replaymod;

import com.replaymod.render.capturer.OpenGlFrameCapturer;
import com.replaymod.render.frame.OpenGlFrame;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;
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
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;


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
    /*
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;ILjava/lang/Runnable;I)V"), index = 2)
    private int hdr_mod$enlargeGpuBuffer(int i){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport){
            // Byte -> Short for HDR.
            return i * 2;
        }
        return i;
    }
    */
    /*
    @Redirect(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;ILjava/lang/Runnable;I)V"))
    private void hdr_mod$copyTextureToBuffer(CommandEncoder instance, GpuTexture gpuTexture, GpuBuffer gpuBuffer, int i, Runnable runnable, int j){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport){
            TextureUpgradeUtils.setTargetReadPixelFormat(GL30.GL_UNSIGNED_SHORT);
            instance.copyTextureToBuffer(ReplayColorTransformRenderer.getDstTexture(), gpuBuffer, i, runnable, j);
        }
        else{
            instance.copyTextureToBuffer(gpuTexture, gpuBuffer, i, runnable, j);
        }
    }
    */
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/frame/OpenGlFrame;<init>(ILcom/replaymod/lib/de/johni0702/minecraft/gui/utils/lwjgl/ReadableDimension;ILjava/nio/ByteBuffer;)V"), index = 2)
    private int hdr_mod$modifyOpenGlFrameReturn(int bpp){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
}
