package xyz.rrtt217.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.core.BeforeBlitRenderer;

import java.util.OptionalInt;

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
        if (!BeforeBlitRenderer.isBeforeBlitReady) return;

        // If texture / textureView have not been created yet, create them.
        if (BeforeBlitRenderer.beforeBlitTexture == null) {
            BeforeBlitRenderer.beforeBlitTexture = RenderSystem.getDevice().createTexture(() -> "Before Blit Ping-pong Texture", 15, TextureFormat.RGBA8, this.width, this.height, 1, 1);
        }
        if(BeforeBlitRenderer.beforeBlitTextureView == null) {
            BeforeBlitRenderer.beforeBlitTextureView = RenderSystem.getDevice().createTextureView(BeforeBlitRenderer.beforeBlitTexture);
        }

        // Resize the texture on main render target resize.
        if (BeforeBlitRenderer.beforeBlitTexture.getHeight(0) != this.height || BeforeBlitRenderer.beforeBlitTexture.getWidth(0) != this.width) {
            BeforeBlitRenderer.beforeBlitTextureView.close();
            BeforeBlitRenderer.beforeBlitTexture.close();
            BeforeBlitRenderer.beforeBlitTexture = RenderSystem.getDevice().createTexture(() -> "Before Blit Ping-pong Texture", 15, TextureFormat.RGBA8, this.width, this.height, 1, 1);
            BeforeBlitRenderer.beforeBlitTextureView = RenderSystem.getDevice().createTextureView(BeforeBlitRenderer.beforeBlitTexture);
        }

        if (this.colorTexture != null) {
            RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(this.colorTexture, BeforeBlitRenderer.beforeBlitTexture, 0, 0, 0, 0, 0, this.width, this.height);
            //if(UiLuminanceUBO == null) UiLuminanceUBO = new CommonFloatUBO("UiLuminance");
            //GpuBufferSlice gpuBufferSlice = UiLuminanceUBO.getBuffer( 203.0f);
            if (this.colorTextureView != null) {
                try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Before blit", this.colorTextureView, OptionalInt.empty())) {
                    renderPass.setPipeline(BeforeBlitRenderer.BEFORE_BLIT);
                    RenderSystem.bindDefaultUniforms(renderPass);
                    //if(UiLuminanceUBO != null) renderPass.setUniform("UiLuminance", gpuBufferSlice);
                    renderPass.bindTexture("InSampler", BeforeBlitRenderer.beforeBlitTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                    renderPass.draw(0, 3);
                }
            } else {
                throw new IllegalStateException("colorTexture is null");
            }
        }
    }
}
