package xyz.rrtt217.HDRMod.mixin.compat.iris;

import com.google.common.collect.ImmutableList;
import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;

import java.util.ArrayList;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(StandardMacros.class)
public class MixinStandardMacros {
    @Inject(method = "createStandardEnvironmentDefines", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/gl/shader/StandardMacros;define(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void hdr_mod$addDefines(CallbackInfoReturnable<ImmutableList<StringPair>> cir, ArrayList<StringPair> standardDefines){
        standardDefines.add(new StringPair("HDR_MOD_INSTALLED",""));
        if(enableHDR) {
            standardDefines.add(new StringPair("HDR_ENABLED", ""));
        }
    }
}
