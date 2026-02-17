package xyz.rrtt217.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.config.HDRModConfig;
import xyz.rrtt217.core.BeforeBlitRenderer;
import xyz.rrtt217.core.FloatNumberUBO;
import xyz.rrtt217.util.GLFWColorManagement;
import xyz.rrtt217.util.HDRModInjectHooks;

import java.util.OptionalInt;

import static xyz.rrtt217.HDRMod.UiBrightnessUBO;

@Mixin(RenderTarget.class)
public class MixinRenderTarget {
    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    @Nullable
    protected GpuTexture colorTexture;

    @Shadow
    @Nullable
    protected GpuTextureView colorTextureView;

    @Inject(method = "blitToScreen", at = @At("HEAD"))
    private void hdr_mod$beforeBlitRenderer(CallbackInfo ci) {
        RenderSystem.assertOnRenderThread();


        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (!BeforeBlitRenderer.isBeforeBlitReady || config.forceDisableBeforeBlitPipeline) return;
        if (BeforeBlitRenderer.isGameRenderingCanceled){
            BeforeBlitRenderer.isGameRenderingCanceled = false;
            return;
        }

        // Still need reflection.
        if(Platform.isModLoaded("dynamic_fps")){
            try{
                Class<?> clazz = Class.forName("dynamic_fps.impl.DynamicFPSMod");
                java.lang.reflect.Method method = clazz.getMethod("renderedCurrentFrame");
                if(Minecraft.getInstance().noRender || !(boolean) method.invoke(null)) return;
            }
            catch (Exception ignored){}
        }

        // If texture / textureView have not been created yet, create them.
        if (BeforeBlitRenderer.beforeBlitTexture == null) {
            HDRModInjectHooks.enableInject();
            HDRModInjectHooks.enableInject2();
            BeforeBlitRenderer.beforeBlitTexture = RenderSystem.getDevice().createTexture(() -> "Before Blit Ping-pong Texture", 15, TextureFormat.RGBA8, this.width, this.height, 1, 1);
            HDRModInjectHooks.disableInject2();
            HDRModInjectHooks.disableInject();
        }

        if(BeforeBlitRenderer.beforeBlitTextureView == null) {
            BeforeBlitRenderer.beforeBlitTextureView = RenderSystem.getDevice().createTextureView(BeforeBlitRenderer.beforeBlitTexture);
        }

        // Resize the texture on main render target resize.
        if (BeforeBlitRenderer.beforeBlitTexture.getHeight(0) != this.height || BeforeBlitRenderer.beforeBlitTexture.getWidth(0) != this.width) {
            BeforeBlitRenderer.beforeBlitTextureView.close();
            BeforeBlitRenderer.beforeBlitTexture.close();
            HDRModInjectHooks.enableInject();
            HDRModInjectHooks.enableInject2();
            BeforeBlitRenderer.beforeBlitTexture = RenderSystem.getDevice().createTexture(() -> "Before Blit Ping-pong Texture", 15, TextureFormat.RGBA8, this.width, this.height, 1, 1);
            BeforeBlitRenderer.beforeBlitTextureView = RenderSystem.getDevice().createTextureView(BeforeBlitRenderer.beforeBlitTexture);
            HDRModInjectHooks.disableInject2();
            HDRModInjectHooks.disableInject();
        }

        if (this.colorTexture != null) {
            if (UiBrightnessUBO == null) UiBrightnessUBO = new FloatNumberUBO("HdrUIBrightness", 2);
            GpuBuffer gpuBuffer = UiBrightnessUBO.update(new float[]{
                    config.uiBrightness < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.uiBrightness, // For UI Brightness
                    config.customEotfEmulate < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.customEotfEmulate
            });
            if(config.writeBeforeBlitToMainTarget) {
                // Common ping-pong pipeline.
                RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(this.colorTexture, BeforeBlitRenderer.beforeBlitTexture, 0, 0, 0, 0, 0, this.width, this.height);
                if (this.colorTextureView != null) {
                    try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Before blit", this.colorTextureView, OptionalInt.empty())) {
                        renderPass.setPipeline(BeforeBlitRenderer.BEFORE_BLIT);
                        RenderSystem.bindDefaultUniforms(renderPass);
                        if (UiBrightnessUBO != null) renderPass.setUniform("HdrUIBrightness", gpuBuffer);
                        renderPass.bindTexture("InSampler", BeforeBlitRenderer.beforeBlitTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                        renderPass.draw(0, 3);
                    }
                } else {
                    throw new IllegalStateException("colorTexture is null");
                }
            }
            else{
                // Only do a render pass, no copy.
                if (BeforeBlitRenderer.beforeBlitTextureView != null) {
                    try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Before blit", BeforeBlitRenderer.beforeBlitTextureView, OptionalInt.empty())) {
                        renderPass.setPipeline(BeforeBlitRenderer.BEFORE_BLIT);
                        RenderSystem.bindDefaultUniforms(renderPass);
                        if (UiBrightnessUBO != null) renderPass.setUniform("HdrUIBrightness", gpuBuffer);
                        renderPass.bindTexture("InSampler", this.colorTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                        renderPass.draw(0, 3);
                    }
                } else {
                    throw new IllegalStateException("colorTexture is null");
                }
            }
        }
    }
    @ModifyArg(method = "blitToScreen", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;presentTexture(Lcom/mojang/blaze3d/textures/GpuTextureView;)V"), index = 0)
    private GpuTextureView hdr_mod$modifyTextureToBePresented(GpuTextureView gpuTextureView){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.writeBeforeBlitToMainTarget || config.forceDisableBeforeBlitPipeline) return gpuTextureView;
        return BeforeBlitRenderer.beforeBlitTextureView;
    }
    @Inject(method = "createBuffers", at = @At("HEAD"))
    private void hdr_mod$setShouldUpgradeOnCreateBuffers(CallbackInfo ci) {
        HDRModInjectHooks.enableInject();
    }
    @Inject(method = "createBuffers", at = @At("RETURN"))
    private void hdr_mod$setShouldNotUpgradeOnLeavingCreateBuffers(int i, int j, CallbackInfo ci) {
        HDRModInjectHooks.disableInject();
    }
}
