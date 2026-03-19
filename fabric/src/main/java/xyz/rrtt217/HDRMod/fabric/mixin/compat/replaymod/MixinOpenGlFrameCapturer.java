package xyz.rrtt217.HDRMod.fabric.mixin.compat.replaymod;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
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
                ReplayColorTransformRenderer = new ColorTransformRenderer(Minecraft.getInstance().getMainRenderTarget(), "Replay");
            }
            ReplayColorTransformRenderer.updateColorTransformUBO(
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
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuDevice;createBuffer(Ljava/util/function/Supplier;IJ)Lcom/mojang/blaze3d/buffers/GpuBuffer;"), index = 2)
    private long hdr_mod$enlargeGpuBuffer(long l){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport){
            // Byte -> Short for HDR.
            return l * 2;
        }
        return l;
    }
    @Redirect(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;I)V"))
    private void hdr_mod$copyTextureToBuffer(CommandEncoder instance, GpuTexture gpuTexture, GpuBuffer gpuBuffer, long l, Runnable runnable, int i){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport){
            TextureUpgradeUtils.setTargetReadPixelFormat(GL30.GL_UNSIGNED_SHORT);
            instance.copyTextureToBuffer(ReplayColorTransformRenderer.getDstTexture(), gpuBuffer, l, runnable, i);
        }
        else{
            instance.copyTextureToBuffer(gpuTexture, gpuBuffer, l, runnable, i);
        }
    }
    @ModifyArg(method = "captureFrame", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/frame/OpenGlFrame;<init>(ILcom/replaymod/lib/de/johni0702/minecraft/gui/utils/lwjgl/ReadableDimension;ILjava/nio/ByteBuffer;)V"), index = 2)
    private int hdr_mod$modifyOpenGlFrameReturn(int bpp){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
}
