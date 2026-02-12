package xyz.rrtt217.mixin.compat.iris;

import com.google.common.collect.ImmutableList;
import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod;
import xyz.rrtt217.config.HDRModConfig;
import xyz.rrtt217.util.Enums;

import java.util.ArrayList;

@Mixin(StandardMacros.class)
public class MixinStandardMacros {
    @Inject(method = "createStandardEnvironmentDefines", at = @At("TAIL"), cancellable = true)
    private static void addHDRModDefines(CallbackInfoReturnable<ImmutableList<StringPair>> cir){
        var defines = new ArrayList<>(cir.getReturnValue());
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        defines.add(new StringPair("HDR_MOD_INSTALLED",""));
        if(config.enableHDR) {
            defines.add(new StringPair("HDR_ENABLED", ""));
            defines.add(new StringPair("CURRENT_PRIMARIES", config.overridePrimaries ? config.primaries.toString() : HDRMod.WindowPrimaries.toString()));
            defines.add(new StringPair("CURRENT_TRANSFER_FUNCTION", config.overrideTransferFunction ? config.transferFunction.toString() : HDRMod.WindowTransferFunction.toString()));
            for(Enums.Primaries p : Enums.Primaries.values()) {
                defines.add(new StringPair(p.toString(), String.valueOf(p.getId())));
            }
            for(Enums.TransferFunction tf : Enums.TransferFunction.values()) {
                defines.add(new StringPair(tf.toString(), String.valueOf(tf.getId())));
            }
        }
        else{
            // Always set SRGB on non-HDR.
            defines.add(new StringPair("CURRENT_PRIMARIES", "SRGB"));
            defines.add(new StringPair("CURRENT_TRANSFER_FUNCTION", "SRGB"));
        }
        cir.setReturnValue(ImmutableList.copyOf(defines));
    }
}
