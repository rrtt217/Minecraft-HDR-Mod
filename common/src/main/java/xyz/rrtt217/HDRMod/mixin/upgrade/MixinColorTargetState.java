package xyz.rrtt217.HDRMod.mixin.upgrade;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import dev.architectury.injectables.annotations.PlatformOnly;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

@Mixin(ColorTargetState.class)
public class MixinColorTargetState {
    // Tricks so that Iris won't crash in its init stage on NeoForge. Dev env still doesn't work, not sure why.

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1, require = 0)
    private static GpuFormat hdr_mod$modifyDefaultColorTargetFormat(GpuFormat format) {
        if(HDRMod.configHolder == null) HDRMod.configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        HDRModConfig config = HDRMod.configHolder.getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.modifyDefaultColorTargetState){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }
    @ModifyArg(method = "<init>(Lcom/mojang/blaze3d/pipeline/BlendFunction;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/ColorTargetState;<init>(Ljava/util/Optional;Lcom/mojang/blaze3d/GpuFormat;I)V"), index = 1, require = 0)
    private static GpuFormat hdr_mod$modifyDefaultInitColorTargetFormat(GpuFormat format) {
        if(HDRMod.configHolder == null) HDRMod.configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        HDRModConfig config = HDRMod.configHolder.getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.modifyDefaultColorTargetState){
            return GpuFormat.RGBA16_FLOAT;
        }
        return format;
    }

}
