package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.pipeline.RenderTarget;
import io.homo.superresolution.common.presentation.vulkan.VulkanPresentationFeature;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.lwjgl.opengl.GL30;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;

import xyz.rrtt217.HDRMod.util.HDRModInjectHooks;

import java.io.IOException;

import static xyz.rrtt217.HDRMod.HDRMod.PresentationColorTransformRenderer;
import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasSr;

@Mixin(RenderTarget.class)
public class MixinRenderTarget {
    @Shadow
    protected int colorTextureId;

    @ModifyArgs(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"))
    private void createBuffers(Args args) {
        if (args.get(2).equals(GL30.GL_RGBA8)) {
            args.set(2, GL30.GL_RGBA16F);
            args.set(7, GL30.GL_HALF_FLOAT);
        }
    }

    @Inject(method = "_blitToScreen", at = @At("HEAD"))
    private void hdr_mod$doPresentationTransform(int i, int j, boolean bl, CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (bl) {
            long handle = Minecraft.getInstance().getWindow().getWindow();
            HDRModInjectHooks.setTargetDisableBlend();
            // Create PresentationColorTransformRenderer if there's not one.
            if (PresentationColorTransformRenderer == null) {
                try {
                    PresentationColorTransformRenderer = new ColorTransformRenderer(Minecraft.getInstance().getMainRenderTarget(), "Present");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // Update PresentationColorTransformRenderer.srcTarget.
            if (colorTextureId != PresentationColorTransformRenderer.getSrcTarget().getColorTextureId()) {
                PresentationColorTransformRenderer.setSrcTarget((RenderTarget) (Object) this);
            }
            PresentationColorTransformRenderer.updateColorTransformUniforms(
                    HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                    HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                    HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle),
                    HDRMod.colorManagementInfoProvider.getCurrentTransferFunction(handle)
            );
            PresentationColorTransformRenderer.render();
        }
    }

    @Redirect(method = "_blitToScreen", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;colorTextureId:I", opcode = Opcodes.GETFIELD))
    private int hdr_mod$replaceBlitTarget(RenderTarget instance) {
        if(hasSr && VulkanPresentationFeature.isRequested()) return colorTextureId;
        if (HDRModInjectHooks.getTargetDisableBlend()) {
            HDRModInjectHooks.unsetTargetDisableBlend();
            return PresentationColorTransformRenderer.getDstTextureId();
        }
        return colorTextureId;
    }
}