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
import xyz.rrtt217.HDRMod.util.GLFWColorManagement;

import java.util.ArrayList;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(StandardMacros.class)
public class MixinStandardMacros {
    @Inject(method = "createStandardEnvironmentDefines", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/gl/shader/StandardMacros;define(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void hdr_mod$addDefines(CallbackInfoReturnable<ImmutableList<StringPair>> cir, ArrayList<StringPair> standardDefines){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        standardDefines.add(new StringPair("HDR_MOD_INSTALLED",""));
        if(enableHDR) {
            standardDefines.add(new StringPair("HDR_ENABLED", ""));
            standardDefines.add(new StringPair("CURRENT_PRIMARIES", config.autoSetPrimaries ? Enums.Primaries.fromId(GLFWColorManagement.glfwGetWindowPrimaries(Minecraft.getInstance().getWindow().getWindow())).toString() : config.customPrimaries.toString()));
            standardDefines.add(new StringPair("CURRENT_TRANSFER_FUNCTION", config.autoSetTransferFunction ? Enums.TransferFunction.fromId(GLFWColorManagement.glfwGetWindowTransfer(Minecraft.getInstance().getWindow().getWindow())).toString() : config.customTransferFunction.toString()));
        }
        else{
            // Always set SRGB on non-HDR.
            standardDefines.add(new StringPair("CURRENT_PRIMARIES", "SRGB"));
            standardDefines.add(new StringPair("CURRENT_TRANSFER_FUNCTION", "SRGB"));
        }
        for(Enums.Primaries p : Enums.Primaries.values()) {
            standardDefines.add(new StringPair("PRIMARIES_"+p.toString(), String.valueOf(p.getId())));
        }
        for(Enums.TransferFunction tf : Enums.TransferFunction.values()) {
            standardDefines.add(new StringPair("TRANSFER_FUNCTION_"+tf.toString(), String.valueOf(tf.getId())));
        }
    }
}
