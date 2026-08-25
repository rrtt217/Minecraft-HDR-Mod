package xyz.rrtt217.HDRMod.mixin.compat.vitrail;

import com.llamalad7.mixinextras.sugar.Local;
import dev.vitrail.pack.option.EngineDefines;
import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.helpers.StringPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.platform.Platform;

import java.util.Map;

@Mixin(EngineDefines.class)
public class MixinEngineDefines {
    @Inject(method = "table(Ldev/vitrail/pack/option/EngineDefines$Environment;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private static void hdr_mod$addDefines(EngineDefines.Environment environment, CallbackInfoReturnable<Map<String, String>> cir, @Local(name = "defines") Map<String, String> defines){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        defines.put("HDR_MOD_INSTALLED","");
        String version = Platform.getVersion();
        if(version.contains("-")) version = version.substring(0, version.indexOf("-"));
        defines.put("HDR_MOD_VERSION", version);
        if(config.enableHDR) {
            defines.put("HDR_ENABLED", "");
        }
    }
}
