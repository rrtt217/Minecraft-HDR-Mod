package xyz.rrtt217.HDRMod.mixin.debug;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.textures.GpuTexture;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(GpuTexture.class)
public class MixinGpuTexture {
    @Mutable
    @Final
    @Shadow
    private final GpuFormat format;

    public MixinGpuTexture(GpuFormat format) {
        this.format = format;
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void hdr_mod$scanRenderTarget(CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(format == GpuFormat.RGBA8_UNORM && config.debugRGBA8StackTrace) {
            LOGGER.info("RGBA8 GpuTexture StackTrace", new Throwable());
        }
    }
}
