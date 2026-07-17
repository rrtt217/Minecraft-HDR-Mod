package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;


@Mixin(RenderTarget.class)
public class MixinRenderTarget {

    @Shadow
    @Nullable
    protected GpuTexture colorTexture;

    @Inject(method = "blitToScreen", at = @At("HEAD"))
    private void hdr_mod$beforeBlitRenderer(CallbackInfo ci) {
        RenderSystem.assertOnRenderThread();

        long handle = Minecraft.getInstance().getWindow().handle();

        if(HDRMod.PresentationColorTransformRenderer == null)
            HDRMod.PresentationColorTransformRenderer = new ColorTransformRenderer((RenderTarget) (Object) this, "Presentation");

        HDRMod.PresentationColorTransformRenderer.updateColorTransformUniforms(
                HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(handle),
                HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle),
                HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle),
                HDRMod.colorManagementInfoProvider.getCurrentTransferFunction(handle)
        );
        if (this.colorTexture != null && !this.colorTexture.equals(HDRMod.PresentationColorTransformRenderer.getSrcTarget().getColorTexture()))
            HDRMod.PresentationColorTransformRenderer.setSrcTarget((RenderTarget) (Object) this);
        HDRMod.PresentationColorTransformRenderer.render();
    }
@ModifyArg(method = "blitToScreen", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;presentTexture(Lcom/mojang/blaze3d/textures/GpuTextureView;)V"), index = 0)
    private GpuTextureView hdr_mod$modifyTextureToBePresented(GpuTextureView gpuTextureView){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.forceDisableBeforeBlitPipeline) return gpuTextureView;
        return HDRMod.PresentationColorTransformRenderer.getDstTextureView();
    }
    @Inject(method = "createBuffers", at = @At("HEAD"))
    private void hdr_mod$setShouldUpgradeOnCreateBuffers(CallbackInfo ci) {
        TextureUpgradeUtils.setTargetTextureFormat(GL30.GL_RGBA16F);
        TextureUpgradeUtils.setTargetReadPixelFormat(GL30.GL_HALF_FLOAT);
    }
}
