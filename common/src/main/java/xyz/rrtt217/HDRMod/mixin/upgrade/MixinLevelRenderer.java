package xyz.rrtt217.HDRMod.mixin.upgrade;

import com.mojang.renderpearl.api.GpuFormat;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/TextureTarget;<init>(Ljava/lang/String;IILcom/mojang/renderpearl/api/GpuFormat;Lcom/mojang/renderpearl/api/GpuFormat;)V"), index = 4)
    private GpuFormat hdr_mod$upgradeEntityOutline(GpuFormat format) {
        if(format == GpuFormat.RGBA8_UNORM) {
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
}
