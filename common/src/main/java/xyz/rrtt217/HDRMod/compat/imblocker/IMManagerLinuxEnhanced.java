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

@SuppressWarnings("unused")
public class IMManagerLinuxEnhanced implements IMManager.PlatformIMManager{
    private LinuxIMFramework imFramework;
    private static boolean state = false;

    // This only works on our modified GLFW, not the original LWJGL 3.4.1 version!
    // Internally use zwp_text_input_v3::enable/disable or zwp_text_input_v1::activate/deactivate to completely disable IME when unneeded.
    // This is not the original API's intended behavior!
    @Override
    public void setState(boolean on) {
        if (state != on) {
            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(),0x00033007, on ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
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
        if(config.enableIMBlockerSetPreeditCallbackIntegration)
            UniversalIMEPreeditOverlay.getInstance().updateCaretPosition(pos.x(), pos.y());
    }

    public static void updatePreeditCursorRectanglePosition(int x, int y, int w, int h) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!state) return;
        long handle = Minecraft.getInstance().getWindow().getWindow();
        float xscaleValue = 1.0f;
        float yscaleValue = 1.0f;
        if(config.PreeditOverlayPositionFollowMonitorScale) {
            FloatBuffer xscale = BufferUtils.createFloatBuffer(1);
            FloatBuffer yscale = BufferUtils.createFloatBuffer(1);
            GLFW.glfwGetWindowContentScale(handle, xscale, yscale);
            xscaleValue = xscale.get();
            yscaleValue = yscale.get();
        }
        glfwSetPreeditCursorRectangle(Minecraft.getInstance().getWindow().getWindow(), (int) (x / xscaleValue), (int) (y / yscaleValue), (int) (w / xscaleValue), (int) (h / yscaleValue));
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
