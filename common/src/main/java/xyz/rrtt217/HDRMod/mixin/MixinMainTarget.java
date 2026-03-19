package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @Inject(method = "allocateAttachments", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/MainTarget;allocateColorAttachment(Lcom/mojang/blaze3d/pipeline/MainTarget$Dimension;)Lcom/mojang/blaze3d/textures/GpuTexture;", shift = At.Shift.BEFORE))
    private void hdr_mod$setShouldUpgradeOnAllocateColorAttachment(CallbackInfoReturnable<GpuTexture> cir) {
        TextureUpgradeUtils.setTargetTextureFormat(GL30.GL_RGBA16F);
        TextureUpgradeUtils.setTargetReadPixelFormat(GL30.GL_HALF_FLOAT);
    }
}
