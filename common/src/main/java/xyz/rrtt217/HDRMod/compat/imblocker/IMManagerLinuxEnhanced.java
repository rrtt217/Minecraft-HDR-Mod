package xyz.rrtt217.HDRMod.compat.imblocker;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.LinuxIMFramework;
import io.github.reserveword.imblocker.common.gui.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static io.github.reserveword.imblocker.common.IMManager.calculateCaretPos;
import static xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils.glfwSetPreeditCursorRectangle;

public class IMManagerLinuxEnhanced implements IMManager.PlatformIMManager{
    private LinuxIMFramework imFramework;
    private static boolean state = false;
    private volatile int fontsize;

    // This only works on our modified GLFW, not the original LWJGL 3.4.1 version!
    // Internally use zwp_text_input_v3::enable/disable or zwp_text_input_v1::activate/deactivate to completely disable IME when unneeded.
    // This is not the original API's intended behavior!
    @Override
    public void setState(boolean on) {
        if (state != on) {
            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().handle(),0x00033007, on ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            state = on;
        }
    }

    // Use IMBlocker original setState for setEnglighState. This will only run once when opening chat with / and won't affect much performance.
    // Not 100% percent equal to IMBlocker setEnglishState behavior; IBus has an actual EnglighState in ibus-libpinyin while fcitx has not.
    @Override
    public void setEnglishState(boolean isEN) {
        this.checkIMFramework();
        // isEN ~ state off, !isEN ~ state on.
        this.imFramework.setState(!isEN);
    }

    @Override
    public void updateCompositionWindowPos(Point pos) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!state) return;
        long handle = Minecraft.getInstance().getWindow().handle();
        float xscaleValue = 1.0f;
        float yscaleValue = 1.0f;
        if(config.PreeditOverlayPositionFollowMonitorScale) {
            FloatBuffer xscale = BufferUtils.createFloatBuffer(1);
            FloatBuffer yscale = BufferUtils.createFloatBuffer(1);
            GLFW.glfwGetWindowContentScale(handle, xscale, yscale);
            xscaleValue = xscale.get();
            yscaleValue = yscale.get();
        }
        FocusableObject focusOwner = FocusManager.getFocusOwner();
        if(focusOwner != null) {
            Point caretPos = calculateCaretPos(focusOwner, false);
            if(config.enableIMBlockerSetPreeditCallbackIntegration) UniversalIMEPreeditOverlay.getInstance().updateCaretPosition(caretPos.x(), caretPos.y());
            double widgetGuiScale = focusOwner.getGuiScale();
            int widgetFontSize = focusOwner.getFontHeight();
            int containerFontSize;
            double containerGuiScale;
            Rectangle compositionBorder;
            if(focusOwner instanceof FocusableWidget focusedWidget) {
                containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
                containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
                compositionBorder = focusedWidget.getFocusContainer().getBoundsAbs();
            } else {
                containerFontSize = widgetFontSize;
                containerGuiScale = widgetGuiScale;
                compositionBorder = focusOwner.getBoundsAbs();
            }

            int inputHeight = (int) (widgetFontSize * widgetGuiScale + 5 * containerGuiScale);
            int compositionX = pos.x(), compositionY = pos.y() + inputHeight,
                    //compositionWidth = (int) (preEditTextWidth * containerGuiScale),
                    compositionHeight = (int) (containerFontSize * containerGuiScale);
            //if(compositionX + compositionWidth > compositionBorder.width()) {
            //    compositionX = compositionBorder.width() - compositionWidth;
            //}
            if(compositionY + compositionHeight > compositionBorder.height()) {
                compositionY = (int) (pos.y() - (6 + containerFontSize) * containerGuiScale);
            }
            int scaledMargin = (int) (2 * containerGuiScale);
            Rectangle preeditCursorRect = new Rectangle(compositionX - scaledMargin, Math.min(pos.y(), compositionY) - scaledMargin,
                    0, compositionHeight + inputHeight + scaledMargin * 2);
            glfwSetPreeditCursorRectangle(Minecraft.getInstance().getWindow().handle(),
                    (int) (preeditCursorRect.x() / xscaleValue), (int) (preeditCursorRect.y() / yscaleValue), (int) (preeditCursorRect.width() / xscaleValue), (int) (preeditCursorRect.height() / yscaleValue));
            // We do not have an IMECandidateOverlay, do not update.
        }
    }

    @Override
    public void updateCompositionFontSize(int fontSize) {
        this.fontsize = fontSize;
    }

    private void checkIMFramework() {
        String fcitx5State = "";

        try {
            Process process = Runtime.getRuntime().exec("pgrep -l fcitx5".split(" "));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            fcitx5State = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.imFramework = fcitx5State == null ? LinuxIMFramework.IBUS : LinuxIMFramework.FCITX5;
    }
}
