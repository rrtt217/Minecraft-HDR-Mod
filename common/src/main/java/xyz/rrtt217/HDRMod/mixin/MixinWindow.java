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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.util.List;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    public abstract long getWindow();
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 0))
    private void hdr_mod$16BitWindowHint(int hint, int value) {
        // Redo the original window hint.
        GLFW.glfwWindowHint(139265, 196609);

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
            boolean applyLinuxWorkaround = (platform == GLFW.GLFW_PLATFORM_X11 || (hasNvidiaCard && platform == GLFW.GLFW_PLATFORM_WAYLAND)) && !config.forceDisableGlfwWorkaround;
            boolean applyWindowsWorkaround = (hasOnlyIntelCard && platform == GLFW.GLFW_PLATFORM_WIN32) && !config.forceDisableGlfwWorkaround;
            if(platform != GLFW.GLFW_PLATFORM_X11 && enableHDR && HDRModMixinPlugin.hasGlfwLib) {
                // For 16 bits per channel.
                if(applyWindowsWorkaround && config.useUNORMWindowPixelFormat) {
                    GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 10);
                    GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 10);
                    GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 10);
                    GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, 2);
                }
                else {
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 16);
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 16);
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 16);
                }
                if(platform == GLFW.GLFW_PLATFORM_WIN32 && config.forceActivateGlDxInterop)
                {
                    GLFW.glfwWindowHint(0x00025003,GLFW.GLFW_TRUE);
                    GLFW.glfwWindowHint(0x00025004,GLFW.GLFW_TRUE);
                }
                // For float buffer. Note: Because Intel on Windows do not support float buffer (WGL_TYPE_RGBA_FLOAT_ARB), Intel users can't use this mod natively.
                if(!applyLinuxWorkaround && !applyWindowsWorkaround && !config.useUNORMWindowPixelFormat) {
                    GLFW.glfwWindowHint(0x00021011,GLFW.GLFW_TRUE);
                }
                else if(applyLinuxWorkaround) {
                    HDRMod.LOGGER.warn("A workaround (LinuxNvidiaMissingSupportForEGLFloatBuffer) has been applied for your platform and hardware. HDR Mod may or may not work.");
                }
                else if(applyWindowsWorkaround) {
                    if(!config.useUNORMWindowPixelFormat) GLFW.glfwWindowHint(0x00021011,GLFW.GLFW_TRUE);
                    if(!config.forceActivateGlDxInterop){ 
                        GLFW.glfwWindowHint(0x00025003,GLFW.GLFW_TRUE);
                        if(config.useUNORMWindowPixelFormat) GLFW.glfwWindowHint(0x00025004,GLFW.GLFW_TRUE);
                    }
                    HDRMod.LOGGER.warn("A workaround (WindowsIntelRequireGlDxInterop) has been applied for your platform and hardware. HDR Mod may or may not work.");
                }
            }
        }
    @Inject(method = "<init>", at = @At("RETURN"))
        private void hdr_mod$setupWindowData(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci)
        {
            HDRMod.LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ",
                    GLFW.glfwGetWindowAttrib(this.getWindow(),GLFW.GLFW_RED_BITS), GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(this.getWindow()), GLFWColorManagementUtils.glfwGetWindowMaxLuminance(this.getWindow()) ,GLFWColorManagementUtils.glfwGetWindowMinLuminance(this.getWindow()),Enums.Primaries.fromId(GLFWColorManagementUtils.glfwGetWindowPrimaries(this.getWindow())),Enums.TransferFunction.fromId(GLFWColorManagementUtils.glfwGetWindowTransfer(this.getWindow()))
            );
            if(GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WAYLAND) HDRMod.LOGGER.info("SDR white level and luminances logged here may not be accurate at this time for Linux users.");
        }
    @Inject(method = "onFramebufferResize", at = @At("HEAD"))
        private void hdr_mod$setIsMinimizedOnFramebufferResize(final long handle, final int newWidth, final int newHeight, CallbackInfo callbackInfo) {
            if(handle == getWindow()) {
                DXGIStateManager.setMinimized(newWidth == 0 || newHeight == 0);
            }
        }

    @Redirect(method = "setIcon", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetPlatform()I"))
    private int hdr_mod$bypassWaylandCheckOnSetIcon(){
        int i = GLFW.glfwGetPlatform();
        if(i == GLFW.GLFW_PLATFORM_WAYLAND) return GLFW.GLFW_PLATFORM_X11;
        return i;
    }
    }
