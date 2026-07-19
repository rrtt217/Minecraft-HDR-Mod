package xyz.rrtt217.HDRMod.mixin.vk;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuSurface;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.sdl.SDLPlatform;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.color.Enums;
import xyz.rrtt217.HDRMod.util.color.VulkanColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.util.color.VulkanSDLColorManagementInfoProvider;

import java.util.Objects;

import static org.lwjgl.vulkan.EXTSwapchainColorspace.*;
import static org.lwjgl.vulkan.VK10.*;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasBlazeSdl;

@Mixin(VulkanGpuSurface.class)
public class MixinVulkanGpuSurface {
    @Shadow
    @Final
    private VulkanDevice device;

    @Unique
    private int hdr_mod$chosenColorspace = 0;

    /**
     * @author rrtt217
     * @reason Allow HDR surface
     */
    @Overwrite
    public VkSurfaceFormatKHR pickSwapchainSurfaceFormat(final VkSurfaceFormatKHR.Buffer formats) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        for (VkSurfaceFormatKHR format : formats) {
            // If VK_EXT_SWAPCHAIN_COLOR_SPACE is not available, choose format and colorspace like vanilla.
            if (hdr_mod$isVanillaFallback(format)) {
                hdr_mod$setupSdrProvider(8);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
            // On Wayland, we prefer passthrough, we choose format according to setting. As for colorspace, we use passthrough to prevent conflict with glfw side.
            if (hdr_mod$isWayland() && hdr_mod$matchesWaylandFormat(format, config, true)) {
                hdr_mod$applyWaylandProvider(format);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
            // On platforms other than Wayland, we choose both format and colorspace according to setting.
            if (!hdr_mod$isWayland() && hdr_mod$matchesNonWaylandFormat(format, config, true)) {
                hdr_mod$applyNonWaylandProvider(format);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
        }

        LOGGER.warn("Failed to find format and colorspace according to config, trying fallback...");

        // Try second time but not necessarily match the config.
        for (VkSurfaceFormatKHR format : formats) {
            // On Wayland, we prefer passthrough, we choose format according to setting. As for colorspace, we use passthrough to prevent conflict with glfw side.
            if (hdr_mod$isWayland() && hdr_mod$matchesWaylandFormat(format, config, false)) {
                hdr_mod$applyWaylandProvider(format);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
            // On platforms other than Wayland, we choose both format and colorspace according to setting.
            if (!hdr_mod$isWayland() && hdr_mod$matchesNonWaylandFormat(format, config, false)) {
                hdr_mod$applyNonWaylandProvider(format);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
        }
        throw new IllegalStateException("Could not find compatible swapchain format");
    }

    @ModifyArg(method = "configure", at = @At(value = "INVOKE", target = "Lorg/lwjgl/vulkan/VkSwapchainCreateInfoKHR;imageColorSpace(I)Lorg/lwjgl/vulkan/VkSwapchainCreateInfoKHR;"))
    private int hdr_mod$chooseCorrectColorspace(int value){
        if(value == 0) value = hdr_mod$chosenColorspace;
        return value;
    }

    @Unique
    private boolean hdr_mod$isWayland() {
        return (GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND && !hasBlazeSdl
                || (Objects.equals(SDLVideo.SDL_GetCurrentVideoDriver(), "wayland") && hasBlazeSdl)
        );
    }

    @Unique
    private boolean hdr_mod$isVanillaFallback(VkSurfaceFormatKHR format) {
        boolean hasColorSpaceExtension = this.device.instance().getEnabledExtensions().contains(VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME);
        return !hasColorSpaceExtension && format.colorSpace() == 0 && (format.format() == 37 || format.format() == 44);
    }

    @Unique
    private boolean hdr_mod$isUNORM10bitFormat(int format) {
        return format == VK_FORMAT_A2R10G10B10_UNORM_PACK32
            || format == VK_FORMAT_A2B10G10R10_UNORM_PACK32;
    }

    @Unique
    private boolean hdr_mod$isPassthroughOrHdr10ForSdl(int colorSpace) {
        return (colorSpace == VK_COLOR_SPACE_PASS_THROUGH_EXT && !hasBlazeSdl)
            || (colorSpace == VK_COLOR_SPACE_HDR10_ST2084_EXT && hasBlazeSdl);
    }

    @Unique
    private boolean hdr_mod$isPassthroughOrLinearForSdl(int colorSpace) {
        return (colorSpace == VK_COLOR_SPACE_PASS_THROUGH_EXT && !hasBlazeSdl)
            || (colorSpace == VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT && hasBlazeSdl);
    }

    @Unique
    private boolean hdr_mod$matchesWaylandFormat(VkSurfaceFormatKHR format, HDRModConfig config, boolean applyConfigCheck) {
        int fmt = format.format();
        int colorSpace = format.colorSpace();
        boolean unormMatches = hdr_mod$isUNORM10bitFormat(fmt)
            && (!applyConfigCheck || config.useUNORMWindowPixelFormat)
            && hdr_mod$isPassthroughOrHdr10ForSdl(colorSpace);
        boolean sfloatMatches = fmt == VK_FORMAT_R16G16B16A16_SFLOAT
            && (!applyConfigCheck || !config.useUNORMWindowPixelFormat)
            && hdr_mod$isPassthroughOrLinearForSdl(colorSpace);
        return unormMatches || sfloatMatches;
    }

    @Unique
    private boolean hdr_mod$matchesNonWaylandFormat(VkSurfaceFormatKHR format, HDRModConfig config, boolean applyConfigCheck) {
        int fmt = format.format();
        int colorSpace = format.colorSpace();
        boolean unormMatches = hdr_mod$isUNORM10bitFormat(fmt)
            && (!applyConfigCheck || config.useUNORMWindowPixelFormat)
            && colorSpace == VK_COLOR_SPACE_HDR10_ST2084_EXT;
        boolean sfloatMatches = fmt == VK_FORMAT_R16G16B16A16_SFLOAT
            && (!applyConfigCheck || !config.useUNORMWindowPixelFormat)
            && colorSpace == VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT;
        return unormMatches || sfloatMatches;
    }

    @Unique
    private void hdr_mod$setupSdrProvider(int bits) {
        if (hasBlazeSdl) {
            HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(bits, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
        } else {
            HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(bits, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
        }
    }

    @Unique
    private void hdr_mod$applyWaylandProvider(VkSurfaceFormatKHR format) {
        int bits = (format.format() == VK_FORMAT_R16G16B16A16_UNORM || format.format() == VK_FORMAT_R16G16B16A16_SFLOAT) ? 16 : 10;
        if (hasBlazeSdl) {
            if (format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) {
                HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(bits, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
            } else {
                HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(bits, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
            }
        } else {
            HDRMod.colorManagementInfoProvider.setBitsPerChannel(bits);
        }
    }

    @Unique
    private void hdr_mod$applyNonWaylandProvider(VkSurfaceFormatKHR format) {
        if (format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) {
            int bits = format.format() == VK_FORMAT_R16G16B16A16_UNORM ? 16 : 10;
            if (hasBlazeSdl) {
                HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(bits, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
            } else {
                HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(bits, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
            }
            LOGGER.info("Got HDR10 on Vulkan!");
        } else {
            if (hasBlazeSdl) {
                HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
            } else {
                HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
            }
            LOGGER.info("Got scRGB on Vulkan!");
        }
    }
}
