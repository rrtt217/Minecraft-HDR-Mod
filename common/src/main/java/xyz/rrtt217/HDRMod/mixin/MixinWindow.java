package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.util.List;
import java.util.Set;


@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    @Final
    private long handle;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = At.Shift.AFTER))
        private void hdr_mod$16BitWindowHint(WindowEventHandler arg, ScreenManager arg2, DisplayData arg3, String string, String string2, CallbackInfo ci) {
            // Get GLFW platform.
            int platform = GLFW.glfwGetPlatform();

            // Get config.
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();

            // Get GPU.
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
            boolean hasNvidiaCard = false;
            boolean hasIntelCard = false;
            boolean hasOnlyIntelCard = true;
            for (GraphicsCard card : graphicsCards) {
                if (card.getVendor().toLowerCase().contains("nvidia") && !hasNvidiaCard) {
                    hasNvidiaCard = true;
                }
                if (card.getVendor().toLowerCase().contains("intel") && !hasIntelCard) {
                    hasIntelCard = true;
                }
                if (!card.getVendor().toLowerCase().contains("intel") && !hdr_mod$isVirtualGraphicsCard(card) && hasOnlyIntelCard) {
                    hasOnlyIntelCard = false;
                }
            }
            hasOnlyIntelCard = hasOnlyIntelCard && hasIntelCard;
            boolean applyLinuxWorkaround = (platform == GLFW.GLFW_PLATFORM_X11 || (hasNvidiaCard && platform == GLFW.GLFW_PLATFORM_WAYLAND)) && !config.forceDisableGlfwWorkaround;
            boolean applyWindowsWorkaround = (hasOnlyIntelCard && platform == GLFW.GLFW_PLATFORM_WIN32) && !config.forceDisableGlfwWorkaround;
            if(platform != GLFW.GLFW_PLATFORM_X11 && HDRModMixinPlugin.hasGlfwLib) {
                // 10 bpc for int
                if(applyWindowsWorkaround && config.useUNORMWindowPixelFormat) {
                    GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 10);
                    GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 10);
                    GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 10);
                    GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, 2);
                }
                // 16 bpc for float
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
                // For float buffer.
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

        @Unique
        private static final Set<String> VIRTUAL_KEYWORDS = Set.of(
                "vmware", "virtualbox", "qxl", "virtio", "virtio-gpu",
                "hyper-v video", "microsoft basic display", "citrix",
                "parallels display", "rdpud", "idd", "mirage", "virtual"
        );
        @Unique
        private boolean hdr_mod$isVirtualGraphicsCard(GraphicsCard card) {
            String name = card.getName().toLowerCase();
            String vendor = card.getVendor().toLowerCase();

            for (String keyword : VIRTUAL_KEYWORDS) {
                if (name.contains(keyword) || vendor.contains(keyword)) {
                    return true;
                }
            }

            return false;
        }

        @Inject(method = "<init>", at = @At("RETURN"))
        private void hdr_mod$setupWindowData(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci)
        {
            int bpc = HDRMod.colorManagementInfoProvider.getBitsPerChannel(this.handle);
            float SDRWhiteLevel = HDRMod.colorManagementInfoProvider.getWindowSdrWhiteLevel(handle);
            float maxLuminance = HDRMod.colorManagementInfoProvider.getWindowMaxLuminance(handle);
            float minLuminance = HDRMod.colorManagementInfoProvider.getWindowMinLuminance(handle);
            Enums.Primaries primaries = HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle);
            Enums.TransferFunction tf = HDRMod.colorManagementInfoProvider.getWindowTransferFunction(handle);
            int platform = GLFW.glfwGetPlatform();
            HDRMod.LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ", bpc, SDRWhiteLevel, maxLuminance, minLuminance, primaries, tf);
            if(platform == GLFW.GLFW_PLATFORM_WAYLAND) HDRMod.LOGGER.info("SDR white level and luminances logged here may not be accurate at this time for Linux users.");
            if((platform == GLFW.GLFW_PLATFORM_WIN32 || platform == GLFW.GLFW_PLATFORM_WAYLAND) && (tf == Enums.TransferFunction.GAMMA22 || tf == Enums.TransferFunction.SRGB)) HDRMod.LOGGER.warn("Detected sRGB or Gamma2.2 EOTF, which probably means HDR isn't supported under current configuration.");
        }

        @Redirect(method = "setIcon", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetPlatform()I"))
        private int hdr_mod$bypassWaylandCheckOnSetIcon(){
            int i = GLFW.glfwGetPlatform();
            if(i == GLFW.GLFW_PLATFORM_WAYLAND) return GLFW.GLFW_PLATFORM_X11;
            return i;
        }
    }
