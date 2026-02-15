package xyz.rrtt217.mixin;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.textures.TextureFormat;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod;
import xyz.rrtt217.config.HDRModConfig;
import xyz.rrtt217.util.HDRModInjectHooks;

@Mixin(GlDevice.class)
public class MixinGlDevice {
    //FIXME: The mixin upgrades all RGBA8 color texture created by createTexture, which may cause performance issues.
    //TODO: Only upgrade RGBA8 color texture belonging to a RenderTarget, or the main RenderTarget.
    @ModifyArg(method = "createTexture(Ljava/lang/String;ILcom/mojang/blaze3d/textures/TextureFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texImage2D(IIIIIIIILjava/nio/ByteBuffer;)V", ordinal = 1),index = 2 )
    private int hdr_mod$upgradeColorBufferFormat$0(int i)
    {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableHDR && i == GlConst.toGlInternalId(TextureFormat.RGBA8) && (!config.onlyUpgradeNecessaryTexture || HDRModInjectHooks.isInjectEnabled())) {
            return GL30.GL_RGBA16F;
        }
        return i;
    }
}
