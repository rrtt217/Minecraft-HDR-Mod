package xyz.rrtt217.mixin;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.textures.TextureFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.lwjgl.opengl.GL30;

@Mixin(GlDevice.class)
public class MixinGlDevice {
    @ModifyArg(method = "createTexture(Ljava/lang/String;ILcom/mojang/blaze3d/textures/TextureFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texImage2D(IIIIIIIILjava/nio/ByteBuffer;)V", ordinal = 1),index = 2 )
    private int hdr_mod$replaceColorBufferFormat$0(int i)
    {
        if(i == GlConst.toGlInternalId(TextureFormat.RGBA8)) {
            return GL30.GL_RGBA16;
        }
        return i;
    }
}
