package xyz.rrtt217.HDRMod.mixin.compat.imblocker;

import io.github.reserveword.imblocker.common.LinuxIMFramework;
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

import java.nio.FloatBuffer;

import static io.github.reserveword.imblocker.common.IMManager.calculateCaretPos;
import static org.lwjgl.glfw.GLFW.glfwSetPreeditCursorRectangle;

@Mixin(targets = "io.github.reserveword.imblocker.common.IMManagerLinux")
public class MixinIMManagerLinux {
    @Shadow
    private static boolean state;
    @Shadow
    private void checkIMFramework() {}
    @Shadow
    private LinuxIMFramework imFramework;

    // This only works on our modified GLFW, not the original LWJGL 3.4.1 version!
    // Internally use zwp_text_input_v3::enable/disable or zwp_text_input_v1::activate/deactivate to completely disable IME when unneeded.
    @Inject(method = "setState", at = @At("HEAD"), cancellable = true, remap = false)
    private void hdr_mod$LinuxSetState(boolean on, CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableIMBlockerSetStateIntegration) return;
        if (state != on) {
            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().handle(),0x00033007, on ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            state = on;
        }
        ci.cancel();
    }

    // Use IMBlocker original setState for setEnglighState. This will only run once when opening chat with / and won't affect much performance.
    @Inject(method = "setEnglishState", at = @At("HEAD"), cancellable = true, remap = false)
    private void hdr_mod$LinuxSetEnglishState(boolean englishState, CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableIMBlockerSetEnglishStateIntegration) return;
        this.checkIMFramework();
        this.imFramework.setState(!englishState);
        ci.cancel();
    }
}
