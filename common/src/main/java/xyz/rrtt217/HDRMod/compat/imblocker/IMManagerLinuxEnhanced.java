package xyz.rrtt217.HDRMod.compat.imblocker;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.nio.FloatBuffer;

import static io.github.reserveword.imblocker.common.IMManager.calculateCaretPos;
import static xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils.glfwSetPreeditCursorRectangle;

public class IMManagerLinuxEnhanced implements IMManager.PlatformIMManager{
    private static boolean state;
    private static boolean englishState;
    private volatile int fontsize;

    // This only works on our modified GLFW, not the original LWJGL 3.4.1 version!
    // Internally use zwp_text_input_v3::enable/disable or zwp_text_input_v1::activate/deactivate to completely disable IME when unneeded.
    @Override
    public void setState(boolean on) {
        if (state != on) {
            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().handle(),0x00033007, on ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            state = on;
        }
    }

    // Use IMBlocker original setState for setEnglighState. This will only run once when opening chat with / and won't affect much performance.
    // Not 100% percent equal to IMBlocker setEnglishState behavior.
    @Override
    public void setEnglishState(boolean isEN) {
    }

    @Override
    public void updateCompositionWindowPos(Point pos) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableIMBlockerSetPreeditOverlayPositionIntegration) return;
        if(!state) return;
        long handle = Minecraft.getInstance().getWindow().handle();
        int height = Minecraft.getInstance().getWindow().getHeight();
        if(config.PreeditOverlayPositionFollowMonitorScale) {
            FloatBuffer xscale = BufferUtils.createFloatBuffer(1);
            FloatBuffer yscale = BufferUtils.createFloatBuffer(1);
            GLFW.glfwGetWindowContentScale(handle, xscale, yscale);
            float xscaleValue = xscale.get();
            float yscaleValue = yscale.get();
            if(fontsize == 0) glfwSetPreeditCursorRectangle(handle, (int) (pos.x() / xscaleValue), (int) (pos.y() / yscaleValue), 0, 0);
            else glfwSetPreeditCursorRectangle(handle, (int) (pos.x() / xscaleValue), (int) (pos.y() / yscaleValue), 0, (int) (- fontsize / yscaleValue * (pos.x() / yscaleValue > height - 2 * fontsize ? ((pos.x() / yscaleValue - height) / fontsize + 2 ) : 1 )));
        }
        else{
            glfwSetPreeditCursorRectangle(handle, pos.x(), pos.y(), 0, ( - fontsize * (pos.x() > height - 2 * fontsize ? ((pos.x() - height) / fontsize + 2 ) : 1 )));
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

    @Override
    public void updateCompositionFontSize(int fontSize) {
        this.fontsize = fontSize;
    }
}
