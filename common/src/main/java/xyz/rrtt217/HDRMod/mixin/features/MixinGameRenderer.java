package xyz.rrtt217.HDRMod.mixin.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(
            method = "resize(II)V",
            at = @At("TAIL")
    )
    void resizeSwapChain(int width, int height, CallbackInfo ci) {
        HDRMod.glInteropResourceManager.resize(width, height);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    void waitForSwapChain(ProfilerFiller instance, String s, Operation<Void> original) {
        original.call(instance, s);
    }
}
