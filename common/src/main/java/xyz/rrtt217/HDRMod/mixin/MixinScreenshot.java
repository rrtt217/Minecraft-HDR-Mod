package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.HDRModInjectHooks;

import java.io.File;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class MixinScreenshot {
    @Inject(method = "grab(Ljava/io/File;Ljava/lang/String;Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private static void onVanillaScreenshotCalled(File file, @Nullable String string, RenderTarget renderTarget, int i, Consumer<Component> consumer, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(HDRModInjectHooks.getVanillaF2Screenshot()){
            if (config.behaviorOnVanillaF2 != Enums.BehaviorOnVanillaScreenshotCalled.ONLY_VANILLA) {
                PngjHDRScreenshot.grab(file, string, renderTarget, consumer);
                if (config.behaviorOnVanillaF2 == Enums.BehaviorOnVanillaScreenshotCalled.ONLY_HDR)
                    ci.cancel();
            }
            HDRModInjectHooks.unsetVanillaF2Screenshot();
        }
        else {
            if (config.behaviorOnVanillaScreenshotCalled != Enums.BehaviorOnVanillaScreenshotCalled.ONLY_VANILLA) {
                PngjHDRScreenshot.grab(file, string, renderTarget, consumer);
                if (config.behaviorOnVanillaScreenshotCalled == Enums.BehaviorOnVanillaScreenshotCalled.ONLY_HDR)
                    ci.cancel();
            }
        }
    }
}
