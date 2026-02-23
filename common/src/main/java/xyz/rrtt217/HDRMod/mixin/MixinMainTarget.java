package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

import java.nio.IntBuffer;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @Redirect(
            method = "allocateColorAttachment",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V")
    )
    private void hdr_mod$upgradeMainTarget(int target, int level, int internalformat, int width, int height,
                                           int border, int format, int type, IntBuffer pixels) {
        if (enableHDR && internalformat == GL30.GL_RGBA8) {
            // To HDR Format
            GlStateManager._texImage2D(target, level, GL30.GL_RGBA16F, width, height, border,
                    format, GL30.GL_HALF_FLOAT, pixels);
        } else {
            // Keep original
            GlStateManager._texImage2D(target, level, internalformat, width, height, border,
                    format, type, pixels);
        }
    }
}