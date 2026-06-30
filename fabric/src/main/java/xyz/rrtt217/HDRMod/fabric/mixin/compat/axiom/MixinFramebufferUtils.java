package xyz.rrtt217.HDRMod.fabric.mixin.compat.axiom;

import com.mojang.blaze3d.GpuFormat;
import com.moulberry.axiom.utils.FramebufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FramebufferUtils.class)
public class MixinFramebufferUtils {
    @ModifyArg(method = "resizeOrCreateFramebuffer(Lcom/mojang/blaze3d/pipeline/RenderTarget;IIZ)Lcom/mojang/blaze3d/pipeline/RenderTarget;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/TextureTarget;<init>(Ljava/lang/String;IIZLcom/mojang/blaze3d/GpuFormat;)V"), index = 4)
    private static GpuFormat hdr_mod$upgradeFlashbackBuffer(GpuFormat format){
        if(format == GpuFormat.RGBA8_UNORM) {return GpuFormat.RGBA16_FLOAT;}
        return format;
    }
}
