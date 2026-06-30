package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.GpuBackend;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;


@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    public abstract long handle();

    @Shadow
    @Final
    private long handle;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void hdr_mod$setupWindowData(WindowEventHandler eventHandler, DisplayData displayData, String fullscreenVideoModeString, boolean exclusiveFullscreen, String title, MonitorManager monitorManager, GpuBackend backend, CallbackInfo ci)    {
        int bpc = GLFW.glfwGetWindowAttrib(this.handle,GLFW.GLFW_RED_BITS);
        float SDRWhiteLevel = GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(this.handle);
        float maxLuminance = GLFWColorManagementUtils.glfwGetWindowMaxLuminance(this.handle);
        float minLuminance = GLFWColorManagementUtils.glfwGetWindowMinLuminance(this.handle);
        Enums.Primaries primaries = Enums.Primaries.fromId(GLFWColorManagementUtils.glfwGetWindowPrimaries(this.handle));
        Enums.TransferFunction tf = Enums.TransferFunction.fromId(GLFWColorManagementUtils.glfwGetWindowTransfer(this.handle));
        int platform = GLFW.glfwGetPlatform();
        LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ", bpc, SDRWhiteLevel, maxLuminance, minLuminance, primaries, tf);
        if(platform == GLFW.GLFW_PLATFORM_WAYLAND) LOGGER.info("SDR white level and luminances logged here may not be accurate at this time for Linux users.");
        if((platform == GLFW.GLFW_PLATFORM_WIN32) && (tf == Enums.TransferFunction.GAMMA22 || tf == Enums.TransferFunction.SRGB)) LOGGER.warn("Detected sRGB or Gamma2.2 EOTF, which probably means HDR isn't supported under current configuration.");
    }
    @Redirect(method = "setIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GLX;getGlfwPlatform()I"))
    private int hdr_mod$bypassWaylandCheckOnSetIcon(){
        int i = GLX.getGlfwPlatform();
        if(i == GLFW.GLFW_PLATFORM_WAYLAND) return GLFW.GLFW_PLATFORM_X11;
        return i;
    }
}
