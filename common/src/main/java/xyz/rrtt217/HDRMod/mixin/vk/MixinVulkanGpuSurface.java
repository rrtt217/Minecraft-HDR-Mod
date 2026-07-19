package xyz.rrtt217.HDRMod.mixin.vk;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuSurface;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.color.Enums;
import xyz.rrtt217.HDRMod.util.color.VulkanColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.util.color.VulkanSDLColorManagementInfoProvider;

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
            if(!this.device.instance().getEnabledExtensions().contains(VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME) && format.colorSpace() == 0 && (format.format() == 37 || format.format() == 44)) {
                if(hasBlazeSdl) HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(8, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
                else HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(8, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
            // On Wayland, we prefer passthrough, we choose format according to setting. As for colorspace, we use passthrough to prevent conflict with glfw side.
            if (GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND && format.colorSpace() == VK_COLOR_SPACE_PASS_THROUGH_EXT && (((format.format() == VK_FORMAT_A2R10G10B10_UNORM_PACK32 || format.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32 || format.format() == VK_FORMAT_R16G16B16A16_UNORM) && config.useUNORMWindowPixelFormat) || (format.format() == VK_FORMAT_R16G16B16A16_SFLOAT && !config.useUNORMWindowPixelFormat) ) ) {
                if(format.format() == VK_FORMAT_R16G16B16A16_UNORM || format.format() == VK_FORMAT_R16G16B16A16_SFLOAT) {
                    HDRMod.colorManagementInfoProvider.setBitsPerChannel(16);
                }
                else HDRMod.colorManagementInfoProvider.setBitsPerChannel(10);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
            // On platforms other than Wayland, we choose both format and colorspace according to setting.
            if (!(GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND) && (((format.format() == VK_FORMAT_A2R10G10B10_UNORM_PACK32 || format.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32 || format.format() == VK_FORMAT_R16G16B16A16_UNORM) && config.useUNORMWindowPixelFormat && format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) || (format.format() == VK_FORMAT_R16G16B16A16_SFLOAT && !config.useUNORMWindowPixelFormat && format.colorSpace() == VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT))){
                if(format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) {
                    if(hasBlazeSdl) HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(format.format() == VK_FORMAT_R16G16B16A16_UNORM ? 16 : 10, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
                    else HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(format.format() == VK_FORMAT_R16G16B16A16_UNORM ? 16 : 10, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
                    LOGGER.info("Got HDR10 on Vulkan!");
                }
                else {
                    if(hasBlazeSdl) HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
                    else HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
                    LOGGER.info("Got scRGB on Vulkan!");
                }
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
        }

        LOGGER.warn("Failed to find format and colorspace according to config, trying fallback...");

        // Try second time but not necessarily match the config.
        for (VkSurfaceFormatKHR format : formats) {
            // On Wayland, we prefer passthrough, we choose format according to setting. As for colorspace, we use passthrough to prevent conflict with glfw side.
            if (GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND && format.colorSpace() == VK_COLOR_SPACE_PASS_THROUGH_EXT && (((format.format() == VK_FORMAT_A2R10G10B10_UNORM_PACK32 || format.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32 || format.format() == VK_FORMAT_R16G16B16A16_UNORM)) || (format.format() == VK_FORMAT_R16G16B16A16_SFLOAT) ) ) {
                if(format.format() == VK_FORMAT_R16G16B16A16_UNORM || format.format() == VK_FORMAT_R16G16B16A16_SFLOAT) {
                    HDRMod.colorManagementInfoProvider.setBitsPerChannel(16);
                }
                else HDRMod.colorManagementInfoProvider.setBitsPerChannel(10);
                hdr_mod$chosenColorspace = format.colorSpace();
                return format;
            }
            // On platforms other than Wayland, we choose both format and colorspace according to setting.
            if (!(GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND) && (((format.format() == VK_FORMAT_A2R10G10B10_UNORM_PACK32 || format.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32 || format.format() == VK_FORMAT_R16G16B16A16_UNORM) && format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) || (format.format() == VK_FORMAT_R16G16B16A16_SFLOAT && format.colorSpace() == VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT))){
                if(format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) {
                    if(hasBlazeSdl) HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(format.format() == VK_FORMAT_R16G16B16A16_UNORM ? 16 : 10, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
                    else HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(format.format() == VK_FORMAT_R16G16B16A16_UNORM ? 16 : 10, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
                    LOGGER.info("Got HDR10 on Vulkan!");
                }
                else {
                    if(hasBlazeSdl) HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
                    else HDRMod.colorManagementInfoProvider = new VulkanColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
                    LOGGER.info("Got scRGB on Vulkan!");
                }
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
}
