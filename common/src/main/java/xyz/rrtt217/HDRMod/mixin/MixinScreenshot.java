package xyz.rrtt217.HDRMod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.ScreenshotStateListener;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class MixinScreenshot {
    @Unique
    private static GpuTexture hdr_mod$vanillaScreenshotFlipTexture;
    @Inject(method = "grab(Ljava/io/File;Ljava/lang/String;Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private static void onVanillaScreenshotCalled(File file, @Nullable String string, RenderTarget renderTarget, int i, Consumer<Component> consumer, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(ScreenshotStateListener.getVanillaF2Screenshot()){
            if (config.behaviorOnVanillaF2 != Enums.BehaviorOnVanillaScreenshotCalled.ONLY_VANILLA) {
                PngjHDRScreenshot.grab(file, string, renderTarget, consumer);
                if (config.behaviorOnVanillaF2 == Enums.BehaviorOnVanillaScreenshotCalled.ONLY_HDR)
                    ci.cancel();
            }
            ScreenshotStateListener.unsetVanillaF2Screenshot();
        }
        else {
            if (config.behaviorOnVanillaScreenshotCalled != Enums.BehaviorOnVanillaScreenshotCalled.ONLY_VANILLA) {
                PngjHDRScreenshot.grab(file, string, renderTarget, consumer);
                if (config.behaviorOnVanillaScreenshotCalled == Enums.BehaviorOnVanillaScreenshotCalled.ONLY_HDR)
                    ci.cancel();
            }
        }
    }
    @WrapOperation(method = "takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;getColorTexture()Lcom/mojang/blaze3d/textures/GpuTexture;", ordinal = 0))
    private static GpuTexture hdr_mod$convertVanillaScreenshotFormat(RenderTarget instance, Operation<GpuTexture> original){
        if(hdr_mod$vanillaScreenshotFlipTexture != null){
            hdr_mod$vanillaScreenshotFlipTexture.close();
        }
        hdr_mod$vanillaScreenshotFlipTexture = RenderSystem.getDevice().createTexture("vanilla screenshot format convert flip texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, instance.width, instance.height, 1, 1);
        if (instance.getColorTexture() != null) {
            RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(instance.getColorTexture(), hdr_mod$vanillaScreenshotFlipTexture, 0, 0, 0, 0, 0, instance.width, instance.height);
        }
        return hdr_mod$vanillaScreenshotFlipTexture;
    }
    @ModifyArg(method = "takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;I)V"), index = 3)
    private static Runnable hdr_mod$CloseTextureAfterScreenshot(Runnable callback){
        return () ->{
            callback.run();
            hdr_mod$vanillaScreenshotFlipTexture.close();
            hdr_mod$vanillaScreenshotFlipTexture = null;
        };
    }
}
