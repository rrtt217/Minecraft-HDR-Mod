package xyz.rrtt217.HDRMod.compat.imblocker;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.LinuxIMFramework;
import io.github.reserveword.imblocker.common.gui.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class IMManagerLinuxEnhanced implements IMManager.PlatformIMManager{
    private LinuxIMFramework imFramework;
    private static boolean state = false;

    // This only works on our modified GLFW, not the original LWJGL 3.4.1 version!
    // Internally use zwp_text_input_v3::enable/disable or zwp_text_input_v1::activate/deactivate to completely disable IME when unneeded.
    // This is not the original API's intended behavior!
    @Override
    public void setState(boolean on) {
        if (state != on) {
            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().handle(),GLFW.GLFW_IME, on ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
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
