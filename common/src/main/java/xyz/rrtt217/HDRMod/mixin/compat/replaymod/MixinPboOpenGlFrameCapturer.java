package xyz.rrtt217.HDRMod.mixin.compat.replaymod;

import com.replaymod.render.capturer.PboOpenGlFrameCapturer;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

import static xyz.rrtt217.HDRMod.HDRMod.ReplayColorTransformRenderer;

@Mixin(PboOpenGlFrameCapturer.class)
public class MixinPboOpenGlFrameCapturer {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 4, ordinal = 0))
    public int hdr_mod$modifyBppConstant(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }

    @ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/capturer/PboOpenGlFrameCapturer;readFromPbo(Ljava/nio/ByteBuffer;IZ)Lcom/replaymod/render/rendering/Frame;", ordinal = 0), index = 1)
    public int hdr_mod$modifyProcessBppArgument(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }

    @ModifyConstant(method = "captureFrame(ILjava/lang/Enum;)Lcom/replaymod/render/frame/OpenGlFrame;", constant = @Constant(intValue = 4, ordinal = 0))
    public int hdr_mod$modifyCaptureFrameBppConstant(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
}
    /*
    @Redirect(method = "captureFrame(ILjava/lang/Enum;)Lcom/replaymod/render/frame/OpenGlFrame;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;ILjava/lang/Runnable;I)V"))
    private void hdr_mod$copyTextureToBufferPbo(CommandEncoder instance, GpuTexture gpuTexture, GpuBuffer gpuBuffer, int i, Runnable runnable, int j){
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