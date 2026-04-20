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

import java.nio.FloatBuffer;

import static io.github.reserveword.imblocker.common.IMManager.calculateCaretPos;
import static xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils.glfwSetPreeditCursorRectangle;

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
        GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(),0x00033007, englishState ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
        ci.cancel();
    }

    // This works on original LWJGL 3.4.1 GLFW. Upstream accepted similar change in 26.1 and this will be unnecessary.
    @Unique
    public void updateCompositionWindowPos(Point pos) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableIMBlockerSetPreeditOverlayPositionIntegration) return;
        if(!state) return;
        long handle = Minecraft.getInstance().getWindow().getWindow();
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
