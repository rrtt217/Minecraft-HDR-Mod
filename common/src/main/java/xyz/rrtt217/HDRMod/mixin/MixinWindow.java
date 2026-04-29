package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.GpuBackend;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.util.List;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    public abstract long handle();

    @Shadow
    @Final
    private long handle;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void hdr_mod$setupWindowData(WindowEventHandler eventHandler, DisplayData displayData, String fullscreenVideoModeString, String title, GpuBackend backend, CallbackInfo ci)
    {
        HDRMod.LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ",
            GLFW.glfwGetWindowAttrib(this.handle(),GLFW.GLFW_RED_BITS), GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(this.handle()), GLFWColorManagementUtils.glfwGetWindowMaxLuminance(this.handle()) , GLFWColorManagementUtils.glfwGetWindowMinLuminance(this.handle()), GLFWColorManagementUtils.glfwGetWindowPrimaries(this.handle), GLFWColorManagementUtils.glfwGetWindowTransfer(this.handle())
        );
        if(GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WAYLAND) HDRMod.LOGGER.info("SDR white level and luminances logged here may not be accurate at this time for Linux users.");
    }
}
