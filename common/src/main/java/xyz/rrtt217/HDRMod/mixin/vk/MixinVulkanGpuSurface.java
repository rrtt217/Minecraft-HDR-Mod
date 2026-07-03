package xyz.rrtt217.HDRMod.mixin.vk;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuSurface;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import static org.lwjgl.vulkan.EXTSwapchainColorspace.*;
import static org.lwjgl.vulkan.VK10.*;

@Mixin(VulkanGpuSurface.class)
public class MixinVulkanGpuSurface {
    @Shadow
    @Final
    private VulkanDevice device;

    /**
     * @author rrtt217
     * @reason Allow HDR surface
     */
    @Overwrite
    public VkSurfaceFormatKHR pickSwapchainSurfaceFormat(final VkSurfaceFormatKHR.Buffer formats) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        for (VkSurfaceFormatKHR format : formats) {
            // If VK_EXT_SWAPCHAIN_COLOR_SPACE is not available, choose format and colorspace like vanilla.
            if(!this.device.instance().getEnabledExtensions().contains(VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME) && format.colorSpace() == 0 && (format.format() == 37 || format.format() == 44))
                return format;
            // On Wayland, we prefer passthrough, we choose format according to setting. As for colorspace, we use passthrough to prevent conflict with glfw side.
            if (GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND && format.colorSpace() == VK_COLOR_SPACE_PASS_THROUGH_EXT && (((format.format() == VK_FORMAT_A2R10G10B10_UNORM_PACK32 || format.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32 || format.format() == VK_FORMAT_R16G16B16A16_UNORM) && config.useUNORMWindowPixelFormat) || (format.format() == VK_FORMAT_R16G16B16A16_SFLOAT && !config.useUNORMWindowPixelFormat) ) ) {
                return format;
            }
            // On platforms other than Wayland, we choose both format and colorspace according to setting.
            if (!(GLX.getGlfwPlatform() == GLFW.GLFW_PLATFORM_WAYLAND) && (((format.format() == VK_FORMAT_A2R10G10B10_UNORM_PACK32 || format.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32 || format.format() == VK_FORMAT_R16G16B16A16_UNORM) && config.useUNORMWindowPixelFormat && format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) || (format.format() == VK_FORMAT_R16G16B16A16_SFLOAT && !config.useUNORMWindowPixelFormat && format.colorSpace() == VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT))){
                return format;
            }
        }

        throw new IllegalStateException("Could not find compatible swapchain format");
    }
}
