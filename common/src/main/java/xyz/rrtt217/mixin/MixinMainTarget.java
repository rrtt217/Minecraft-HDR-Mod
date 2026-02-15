package xyz.rrtt217.mixin;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.util.HDRModInjectHooks;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @Inject(method = "allocateAttachments", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/MainTarget;allocateColorAttachment(Lcom/mojang/blaze3d/pipeline/MainTarget$Dimension;)Lcom/mojang/blaze3d/textures/GpuTexture;", shift = At.Shift.BEFORE))
    private void hdr_mod$setShouldUpgradeOnAllocateColorAttachment(CallbackInfoReturnable<GpuTexture> cir) {
        HDRModInjectHooks.enableInject();
    }
    @Inject(method = "allocateAttachments", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/MainTarget;allocateColorAttachment(Lcom/mojang/blaze3d/pipeline/MainTarget$Dimension;)Lcom/mojang/blaze3d/textures/GpuTexture;", shift = At.Shift.AFTER))
    private void hdr_mod$setShouldNotUpgradeOnLeavingAllocateColorAttachment(CallbackInfoReturnable<GpuTexture> cir) {
        HDRModInjectHooks.disableInject();
    }
}
