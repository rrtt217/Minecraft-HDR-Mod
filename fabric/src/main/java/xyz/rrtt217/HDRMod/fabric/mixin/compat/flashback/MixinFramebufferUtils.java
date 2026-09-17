package xyz.rrtt217.HDRMod.fabric.mixin.compat.flashback;

import com.moulberry.flashback.FramebufferUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FramebufferUtils.class)
public class MixinFramebufferUtils {
    /*
    @ModifyArg(method = "resizeOrCreateFramebuffer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/TextureTarget;<init>(Ljava/lang/String;IIZLcom/mojang/blaze3d/GpuFormat;)V"), index = 4)
    private static GpuFormat hdr_mod$upgradeFlashbackBuffer(GpuFormat format){
        if(format == GpuFormat.RGBA8_UNORM) {return GpuFormat.RGBA16_FLOAT;}
        return format;
    }
     */
}
