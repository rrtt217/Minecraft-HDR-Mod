package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.MainTargetBlitShader;
import xyz.rrtt217.HDRMod.util.GLFWColorManagement;

import java.util.Objects;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(RenderTarget.class)
public class MixinRenderTarget {
    @Shadow
    protected int colorTextureId;

    @ModifyArgs(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"))
    private void createBuffers(Args args) {
        if(enableHDR && args.get(2).equals(GL30.GL_RGBA8)) {
            args.set(2, GL30.GL_RGBA16F);
            args.set(7, GL30.GL_HALF_FLOAT);
        }
    }
    @ModifyArg(method = "_blitToScreen", at = @At(value = "INVOKE", target = "Ljava/util/Objects;requireNonNull(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;"), index = 0)
    private Object replaceBlitShader(Object obj){
        if(colorTextureId == Minecraft.getInstance().getMainRenderTarget().getColorTextureId()) {
            if(MainTargetBlitShader.blitShader != null){
                // Setup necessary uniforms.
                var handle = Minecraft.getInstance().getWindow().getWindow();
                HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
                MainTargetBlitShader.blitShader.safeGetUniform("CurrentPrimaries").set(HDRMod.WindowPrimaries.getId());
                MainTargetBlitShader.blitShader.safeGetUniform("CurrentTransferFunction").set(HDRMod.WindowTransferFunction.getId());
                MainTargetBlitShader.blitShader.safeGetUniform("UiBrightness").set(config.uiBrightness < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle) : config.uiBrightness);
                MainTargetBlitShader.blitShader.safeGetUniform("EotfEmulate").set(config.customEotfEmulate < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle) : config.customEotfEmulate);
                return MainTargetBlitShader.blitShader;
            }
        }
        return obj;
    }
}
