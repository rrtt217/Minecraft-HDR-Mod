package xyz.rrtt217.HDRMod.mixin.compat.imblocker;

import io.github.reserveword.imblocker.common.gui.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;


@Mixin(targets = "io.github.reserveword.imblocker.common.IMManagerMac")
public class MixinIMManagerMac {
    @Shadow
    private static boolean state;
    @Unique
    private volatile int fontsize;

    // This works on original GLFW 3.4.1.
    @Inject(method = "setEnglishState", at = @At("HEAD"), cancellable = true, remap = false)
    private void hdr_mod$LinuxSetEnglishState(boolean englishState, CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableIMBlockerSetEnglishStateIntegration) return;
        GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().handle(),0x00033007, englishState ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
        ci.cancel();
    }
}
