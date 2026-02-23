package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.WindowEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.util.List;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(GLFW.class)
public class MixinGLFW {
    @Inject(method = "glfwDefaultWindowHints",at = @At("RETURN"))
    private static void hdr_mod$16BitWindowHint(CallbackInfo ci) {
        // Get GLFW platform.
        int platform = GLFW.glfwGetPlatform();

        // Get config.
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();

        // Get GPU.
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
        boolean hasNvidiaCard = false;
        boolean hasOnlyIntelCard = true;
        for (GraphicsCard card : graphicsCards) {
            if (card.getVendor().toLowerCase().contains("nvidia") && !hasNvidiaCard) {
                hasNvidiaCard = true;
            }
            if (!card.getVendor().toLowerCase().contains("intel") && hasOnlyIntelCard) {
                hasOnlyIntelCard = false;
            }
        }
        boolean applyWorkaround = (platform == GLFW.GLFW_PLATFORM_X11 || (hasNvidiaCard && platform == GLFW.GLFW_PLATFORM_WAYLAND) || (hasOnlyIntelCard && platform == GLFW.GLFW_PLATFORM_WIN32)) && !config.forceDisableGlfwWorkaround;
        if(platform != GLFW.GLFW_PLATFORM_X11 && enableHDR && HDRMod.hasglfwLib) {
            // For 16 bits per channal.
            GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 16);
            GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 16);
            GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 16);
            // For float buffer. Note: Because Intel on Windows do not support float buffer (WGL_TYPE_RGBA_FLOAT_ARB), Intel users can't use this mod natively.
            if(!applyWorkaround && !config.UseUNORMBufferOnLinux) {
                GLFW.glfwWindowHint(0x00021011,GLFW.GLFW_TRUE);
            }
            else if(applyWorkaround) {
                HDRMod.LOGGER.warn("A workaround has been applied for your platform and hardware. HDR Mod may or may not work.");
            }
        }
    }
}
