package xyz.rrtt217.HDRMod.mixin.compat.replaymod;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.render.capturer.PboOpenGlFrameCapturer;
import dev.architectury.injectables.annotations.PlatformOnly;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import static xyz.rrtt217.HDRMod.HDRMod.ReplayColorTransformRenderer;

@Mixin(PboOpenGlFrameCapturer.class)
public class MixinPboOpenGlFrameCapturer {

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 4, ordinal = 0), remap = false)
    public int hdr_mod$modifyBppConstant(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }

    // Replay Mod and ReForgedPlay have different readFromPbo().
    @PlatformOnly(PlatformOnly.FABRIC)
    @ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/capturer/PboOpenGlFrameCapturer;readFromPbo(Ljava/nio/ByteBuffer;IZ)Lcom/replaymod/render/rendering/Frame;", ordinal = 0), index = 1, remap = false)
    public int hdr_mod$modifyProcessBppArgument(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
    @PlatformOnly(PlatformOnly.FORGE)
    @ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lcom/replaymod/render/capturer/PboOpenGlFrameCapturer;readFromPbo(Ljava/nio/ByteBuffer;I)Lcom/replaymod/render/rendering/Frame;", ordinal = 0), index = 1, remap = false)
    public int hdr_mod$modifyProcessBppArgumentNeoForge(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
    @ModifyConstant(method = "captureFrame(ILjava/lang/Enum;)Lcom/replaymod/render/frame/OpenGlFrame;", constant = @Constant(intValue = 4, ordinal = 0), remap = false)
    public int hdr_mod$modifyCaptureFrameBppConstant(int bpp) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return bpp * 2;
        return bpp;
    }
    @Coerce
    @Redirect(method = "captureFrame(ILjava/lang/Enum;)Lcom/replaymod/render/frame/OpenGlFrame;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"))
    private void hdr_mod$bindDstTexure(RenderTarget instance, boolean bl){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) {
            GlStateManager._glBindFramebuffer(36160, ReplayColorTransformRenderer.getDstTextureFramebufferId());
            if (bl) {
                GlStateManager._viewport(0, 0, ReplayColorTransformRenderer.getSrcTarget().width, ReplayColorTransformRenderer.getSrcTarget().height);
            }
        }
        else{
            ((OpenGlFrameCapturerAccessor) this).callFrameBuffer().bindWrite(bl);
        }
    }

    @ModifyArg(method = "captureFrame(ILjava/lang/Enum;)Lcom/replaymod/render/frame/OpenGlFrame;", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glReadPixels(IIIIIIJ)V", ordinal = 0), index = 5, remap = false)
    private int hdr_mod$modifyReadPixelsType(int x){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (config.enableReplayHDRVideoExport) return ReplayColorTransformRenderer.getDstReadPixelFormat();
        return x;
    }
}