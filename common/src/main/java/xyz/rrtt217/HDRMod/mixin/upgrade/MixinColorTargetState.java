package xyz.rrtt217.HDRMod.mixin.upgrade;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import dev.architectury.injectables.annotations.PlatformOnly;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

@Mixin(ColorTargetState.class)
public class MixinColorTargetState {
    // Tricks so that Iris won't crash in its init stage on NeoForge. Dev env still doesn't work, not sure why.

    @PlatformOnly(PlatformOnly.FABRIC)
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1)
    private static GpuFormat hdr_mod$modifyDefaultColorTargetFormat(GpuFormat format) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.modifyDefaultColorTargetState){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
    @PlatformOnly(PlatformOnly.FABRIC)
    @ModifyArg(method = "<init>(Lcom/mojang/blaze3d/pipeline/BlendFunction;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1)
    private static GpuFormat hdr_mod$modifyDefaultInitColorTargetFormat(GpuFormat format) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.modifyDefaultColorTargetState){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }

}
