package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.MainTarget;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @ModifyArgs(method = "allocateColorAttachment", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"))
    private void upgradeMainTarget(Args args) {
        if(enableHDR && args.get(2).equals(GL30.GL_RGBA8)) {
            args.set(2, GL30.GL_RGBA16F);
            args.set(7, GL30.GL_HALF_FLOAT);
        }
    }
}
