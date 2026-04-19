package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;

import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.util.HDRModInjectHooks;

import java.io.IOException;
import java.nio.IntBuffer;

import static xyz.rrtt217.HDRMod.HDRMod.*;
import static xyz.rrtt217.HDRMod.HDRMod.PresentationColorTransformRenderer;

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

    @Inject(method = "_blitToScreen", at = @At("HEAD"))
    private void hdr_mod$doPresentationTransform(int i, int j, boolean bl, CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (bl) {
            HDRModInjectHooks.setTargetDisableBlend();
            if (!enableHDR) return;
            // Create PresentationColorTransformRenderer if there's not one.
            if (PresentationColorTransformRenderer == null) {
                try {
                    PresentationColorTransformRenderer = new ColorTransformRenderer(Minecraft.getInstance().getMainRenderTarget(), "Screenshot");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // Update PresentationColorTransformRenderer.srcTarget.
            if (colorTextureId != PresentationColorTransformRenderer.getSrcTarget().getColorTextureId()) {
                PresentationColorTransformRenderer.setSrcTarget((RenderTarget) (Object) this);
            }
            PresentationColorTransformRenderer.updateColorTransformUniforms(
                    config.uiBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().getWindow()) : config.uiBrightness, // For UI Brightness
                    config.customEotfEmulate < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().getWindow()) : config.customEotfEmulate,
                    config.autoSetPrimaries ? GLFWColorManagementUtils.glfwGetWindowPrimaries(Minecraft.getInstance().getWindow().getWindow()) : config.customPrimaries.getId(),
                    config.autoSetTransferFunction ? GLFWColorManagementUtils.glfwGetWindowTransfer(Minecraft.getInstance().getWindow().getWindow()) : config.customTransferFunction.getId()
            );
            PresentationColorTransformRenderer.render();
        }
    }

    @Redirect(method = "_blitToScreen", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;colorTextureId:I", opcode = Opcodes.GETFIELD))
    private int hdr_mod$replaceBlitTarget(RenderTarget instance) {
        if (HDRModInjectHooks.getTargetDisableBlend()) {
            HDRModInjectHooks.unsetTargetDisableBlend();
            return PresentationColorTransformRenderer.getDstTextureId();
        }
        return colorTextureId;
    }
}
