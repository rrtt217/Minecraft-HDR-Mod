package xyz.rrtt217.HDRMod.mixin.gl;

import com.mojang.blaze3d.opengl.GlBackend;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.interop.GLFWGLInteropResourceManager;
import xyz.rrtt217.HDRMod.core.interop.NewGLInteropResourceManager;
import xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin;
import xyz.rrtt217.HDRMod.util.color.GLFWDXColorManagementInfoProvider;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GlBackend.class)
public class MixinGlBackend {
    @Inject(method = "setWindowHints", at = @At("TAIL"))
    private void hdr_mod$16BitWindowHint(CallbackInfo ci) {
            // Get GLFW platform.
            int platform = GLFW.glfwGetPlatform();

            // Get config.
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();

            // Get GPU.
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
            boolean hasNvidiaCard = false;
            boolean nvidiaNeedsWaylandWorkaround = false;
            boolean hasIntelCard = false;
            boolean hasOnlyIntelCard = true;
            for (GraphicsCard card : graphicsCards) {
                if (card.getVendor().toLowerCase().contains("nvidia") && !hasNvidiaCard) {
                    hasNvidiaCard = true;
                    if (!hdr_mod$nvidiaWaylandFp16Supported(card)) {
                        nvidiaNeedsWaylandWorkaround = true;
                    }
                }
                if (card.getVendor().toLowerCase().contains("intel") && !hasIntelCard) {
                    hasIntelCard = true;
                }
                if (!card.getVendor().toLowerCase().contains("intel") && !hdr_mod$isVirtualGraphicsCard(card) && hasOnlyIntelCard) {
                    hasOnlyIntelCard = false;
                }
            }
            hasOnlyIntelCard = hasOnlyIntelCard && hasIntelCard;
            boolean applyLinuxWorkaround = (platform == GLFW.GLFW_PLATFORM_X11 || (nvidiaNeedsWaylandWorkaround && platform == GLFW.GLFW_PLATFORM_WAYLAND)) && !config.forceDisableGlfwWorkaround;
            boolean applyWindowsWorkaround = (hasOnlyIntelCard && platform == GLFW.GLFW_PLATFORM_WIN32) && !config.forceDisableGlfwWorkaround;
            if(applyWindowsWorkaround || config.forceDisableGlfwWorkaround) {
                if(config.useNewGlDxInteropInGLFW) {
                    HDRMod.glInteropResourceManager = new NewGLInteropResourceManager();
                    HDRMod.colorManagementInfoProvider = new GLFWDXColorManagementInfoProvider(((NewGLInteropResourceManager) HDRMod.glInteropResourceManager).getDXDevice());
                    HDRMod.colorManagementInfoProvider.setBitsPerChannel(config.useUNORMWindowPixelFormat ? 10 : 16);
                }
                else HDRMod.glInteropResourceManager = new GLFWGLInteropResourceManager();
            }
            if(platform != GLFW.GLFW_PLATFORM_X11 || HDRModMixinPlugin.hasGlfwLib) {
                if(!config.useNewGlDxInteropInGLFW) {
                    // 10 bpc for int
                    if (applyWindowsWorkaround || config.useUNORMWindowPixelFormat) {
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
                        HDRMod.LOGGER.warn("A workaround (LinuxNvidiaMissingSupportForEGLFloatBuffer) has been applied for your platform and hardware. HDR Mod may or may not work. This is expected for Nvidia drivers older than R610 or when running under X11.");
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

        // Still we can't ensure that it works. Need more test. GLFW side has made correct preparation though.

        @Unique
        private static final int NVIDIA_WAYLAND_FP16_MIN_DRIVER = 610;

        @Unique
        private boolean hdr_mod$nvidiaWaylandFp16Supported(GraphicsCard card) {
            if (!card.getVendor().toLowerCase().contains("nvidia")) return false;
            String version = card.getVersionInfo();
            if (version == null) return false;
            Matcher m = Pattern.compile("^(\\d+)").matcher(version.trim());
            if (!m.find()) return false;
            return Integer.parseInt(m.group(1)) >= NVIDIA_WAYLAND_FP16_MIN_DRIVER;
        }
}
