package xyz.rrtt217.mixin;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.textures.TextureFormat;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.config.HDRModConfig;
import xyz.rrtt217.util.HDRModInjectHooks;

@Mixin(GlDevice.class)
public class MixinGlDevice {
    @ModifyArgs(method = "createTexture(Ljava/lang/String;ILcom/mojang/blaze3d/textures/TextureFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texImage2D(IIIIIIIILjava/nio/ByteBuffer;)V", ordinal = 1))
    private void hdr_mod$upgradeColorBufferFormat$0(Args args)
    {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableHDR && args.get(2).equals(GlConst.toGlInternalId(TextureFormat.RGBA8)) && (!config.onlyUpgradeNecessaryTexture || HDRModInjectHooks.isInjectEnabled())) {
            if(HDRModInjectHooks.isInject2Enabled() && config.useRGBA16UNORM) {
                args.set(2,GL30.GL_RGBA16);
                args.set(7,GL30.GL_UNSIGNED_SHORT);
            }
            else {
                args.set(2, GL30.GL_RGBA16F);
                args.set(7, GL30.GL_HALF_FLOAT);
            }
        }
    }
}
