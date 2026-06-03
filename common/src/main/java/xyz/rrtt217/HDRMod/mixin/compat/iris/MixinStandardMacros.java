package xyz.rrtt217.HDRMod.mixin.compat.iris;

import com.google.common.collect.ImmutableList;
import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.Platform;

import java.util.ArrayList;

@Mixin(StandardMacros.class)
public class MixinStandardMacros {
    @Inject(method = "createStandardEnvironmentDefines", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/gl/shader/StandardMacros;define(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void hdr_mod$addDefines(CallbackInfoReturnable<ImmutableList<StringPair>> cir, ArrayList<StringPair> standardDefines){
        standardDefines.add(new StringPair("HDR_MOD_INSTALLED",""));
        String version = Platform.getVersion();
        if(version.contains("-")) version = version.substring(0, version.indexOf("-"));
        standardDefines.add(new StringPair("HDR_MOD_VERSION", version));
        if(config.enableHDR) {
            standardDefines.add(new StringPair("HDR_ENABLED", ""));
        }
    }
}
