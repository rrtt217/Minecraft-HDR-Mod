package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

@Mixin(ColorTargetState.class)
public class MixinColorTargetState {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1)
    private static GpuFormat hdr_mod$modifyDefaultColorTargetFormat(GpuFormat format) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.modifyDefaultColorTargetState){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
    @ModifyArg(method = "<init>(Lcom/mojang/blaze3d/pipeline/BlendFunction;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1)
    private static GpuFormat hdr_mod$modifyDefaultInitColorTargetFormat(GpuFormat format) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.modifyDefaultColorTargetState){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }

}
