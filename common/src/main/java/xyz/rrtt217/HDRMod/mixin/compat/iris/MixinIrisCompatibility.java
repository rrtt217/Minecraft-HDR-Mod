package xyz.rrtt217.HDRMod.mixin.compat.iris;

import me.shedaniel.autoconfig.ConfigHolder;
import net.irisshaders.iris.Iris;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

@Mixin(value = IrisCompatibility.class, remap = false)
public class MixinIrisCompatibility {
    @Shadow
    private static Boolean previousEnableHDR;

    @Inject(method = "onConfigSave", at = @At("HEAD"))
    private static void hdr_mod$onConfigSaveIfIrisPresent(ConfigHolder<HDRModConfig> configHolder, HDRModConfig config, CallbackInfoReturnable<InteractionResult> ci) {
        if(previousEnableHDR != null && previousEnableHDR != config.enableHDR) {
            try {
                Iris.reload();
            }
            catch (Exception ignored) {}
        }
    }
}
