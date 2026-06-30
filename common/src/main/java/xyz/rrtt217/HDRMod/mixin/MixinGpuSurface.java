package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.textures.GpuTextureView;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;

import static xyz.rrtt217.HDRMod.HDRMod.PresentationColorTransformRenderer;
import static xyz.rrtt217.HDRMod.HDRMod.minecraft;

@Mixin(GpuSurface.class)
public class MixinGpuSurface {
    @ModifyArg(method = "blitFromTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurfaceBackend;blitFromTexture(Lcom/mojang/blaze3d/systems/CommandEncoderBackend;Lcom/mojang/blaze3d/textures/GpuTextureView;)V"), index = 1)
    private GpuTextureView hdr_mod$beforePresentationColorTransform(GpuTextureView textureView) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();

        if(HDRMod.PresentationColorTransformRenderer == null)
            HDRMod.PresentationColorTransformRenderer = new ColorTransformRenderer(textureView, "Presentation");

        HDRMod.PresentationColorTransformRenderer.updateColorTransformUniforms(
                config.uiBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.uiBrightness, // For UI Brightness
                config.customEotfEmulate < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.customEotfEmulate,
                config.autoSetPrimaries ? GLFWColorManagementUtils.glfwGetWindowPrimaries(Minecraft.getInstance().getWindow().handle()) : config.customPrimaries.getId(),
                config.autoSetTransferFunction ? GLFWColorManagementUtils.glfwGetWindowTransfer(Minecraft.getInstance().getWindow().handle()) : config.customTransferFunction.getId()
        );
        if (minecraft.gameRenderer.mainRenderTarget().getColorTextureView() != null && !textureView.equals(HDRMod.PresentationColorTransformRenderer.getSrcTextureView()))
            HDRMod.PresentationColorTransformRenderer.setSrcSrcTextureView(textureView);
        HDRMod.PresentationColorTransformRenderer.render();

        if(!config.forceDisableBeforeBlitPipeline) return PresentationColorTransformRenderer.getDstTextureView();
        return textureView;
    }
}
