package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL30;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.MainTargetBlitShader;
import xyz.rrtt217.HDRMod.util.GLFWColorManagement;

import java.nio.IntBuffer;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(RenderTarget.class)
public class MixinRenderTarget {
    @Shadow
    protected int colorTextureId;

    @Redirect(
            method = "createBuffers",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V")
    )
    private void hdr_mod$upgradeCreateBuffers(int target, int level, int internalformat, int width, int height,
                                              int border, int format, int type, IntBuffer pixels) {
        if (enableHDR && internalformat == GL30.GL_RGBA8) {
            GlStateManager._texImage2D(target, level, GL30.GL_RGBA16F, width, height, border,
                    format, GL30.GL_HALF_FLOAT, pixels);
        } else {
            GlStateManager._texImage2D(target, level, internalformat, width, height, border,
                    format, type, pixels);
        }
    }
    @Redirect(method = "_blitToScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;blitShader:Lnet/minecraft/client/renderer/ShaderInstance;", opcode = Opcodes.GETFIELD))
    private ShaderInstance replaceBlitShader(GameRenderer instance){
        if(colorTextureId == Minecraft.getInstance().getMainRenderTarget().getColorTextureId()) {
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
            if(MainTargetBlitShader.blitShader != null && !config.forceDisableBlitShaderReplacement){
                // Setup necessary uniforms.
                var handle = Minecraft.getInstance().getWindow().getWindow();
                MainTargetBlitShader.blitShader.safeGetUniform("CurrentPrimaries").set(config.autoSetPrimaries ? HDRMod.WindowPrimaries.getId() : config.customPrimaries.getId());
                MainTargetBlitShader.blitShader.safeGetUniform("CurrentTransferFunction").set(config.autoSetTransferFunction? HDRMod.WindowTransferFunction.getId(): config.customTransferFunction.getId());
                MainTargetBlitShader.blitShader.safeGetUniform("UiBrightness").set(config.uiBrightness < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle): config.uiBrightness);
                MainTargetBlitShader.blitShader.safeGetUniform("EotfEmulate").set(config.customEotfEmulate < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle) : config.customEotfEmulate);
                return MainTargetBlitShader.blitShader;
            }
        }
        return Minecraft.getInstance().gameRenderer.blitShader;
    }
}
