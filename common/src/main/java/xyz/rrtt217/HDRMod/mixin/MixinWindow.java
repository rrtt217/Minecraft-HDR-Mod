package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagement;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.util.List;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    public abstract long getWindow();
        @Inject(method = "<init>", at = @At("RETURN"))
        private void hdr_mod$setupWindowData(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci)
        {
            HDRMod.WindowPrimaries = Enums.Primaries.fromId(GLFWColorManagement.glfwGetWindowPrimaries(this.getWindow()));
            HDRMod.WindowTransferFunction = Enums.TransferFunction.fromId(GLFWColorManagement.glfwGetWindowTransfer(this.getWindow()));
            HDRMod.LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ",
               GLFW.glfwGetWindowAttrib(this.getWindow(),GLFW.GLFW_RED_BITS), GLFWColorManagement.glfwGetWindowSdrWhiteLevel(this.getWindow()), GLFWColorManagement.glfwGetWindowMaxLuminance(this.getWindow()) ,GLFWColorManagement.glfwGetWindowMinLuminance(this.getWindow()),HDRMod.WindowPrimaries,HDRMod.WindowTransferFunction
            );
            HDRMod.LOGGER.info("SDR white level and luminances logged here may not be accurate at this time for Linux users.");
        }
    }
