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
import static xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils.glfwSetPreeditCursorRectangle;

@Mixin(targets = "io.github.reserveword.imblocker.common.IMManagerLinux")
public class MixinIMManagerLinux {
    @Shadow
    private static boolean state;
    @Shadow
    private void checkIMFramework() {}
    @Shadow
    private LinuxIMFramework imFramework;
    @Unique
    private volatile int fontsize;


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


    // This works on original LWJGL 3.4.1 GLFW. Upstream accepted similar change in 26.1 and this will be unnecessary.
    @Unique
    public void updateCompositionWindowPos(Point pos) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableIMBlockerSetPreeditOverlayPositionIntegration) return;
        if(!state) return;
        long handle = Minecraft.getInstance().getWindow().handle();
        if(config.enableIMBlockerSetPreeditOverlayPositionIntegration) {
            FloatBuffer xscale = BufferUtils.createFloatBuffer(1);
            FloatBuffer yscale = BufferUtils.createFloatBuffer(1);
            GLFW.glfwGetWindowContentScale(handle, xscale, yscale);
            glfwSetPreeditCursorRectangle(handle, (int) (pos.x() / xscale.get()), (int) (pos.y() / yscale.get()), 0, -fontsize);
        }
        else{
            glfwSetPreeditCursorRectangle(handle, pos.x(), pos.y(), 0, 0);
        }
        FocusableObject focusedWidget = FocusManager.getFocusOwner();
        if(focusedWidget != null) {
            Point caretPos = calculateCaretPos(focusedWidget, false);
            UniversalIMEPreeditOverlay.getInstance().
                    updateCaretPosition(caretPos.x(), caretPos.y());
            UniversalIMECandidateOverlay.getInstance().
                    updateCaretPosition(caretPos.x(), caretPos.y());
        }
    }

    @Unique
    public void updateCompositionFontSize(int fontSize) {
        this.fontsize = fontSize;
    }
}
