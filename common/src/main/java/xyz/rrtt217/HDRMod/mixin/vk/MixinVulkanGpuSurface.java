package xyz.rrtt217.HDRMod.mixin.vk;

import com.mojang.renderpearl.api.device.GpuSurface;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuSurface;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTHdrMetadata;
import org.lwjgl.vulkan.VkHdrMetadataEXT;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.util.color.VulkanSDLColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.util.color.WaylandVulkanSDLColorManagementInfoProvider;

import java.lang.foreign.MemorySegment;
import java.nio.LongBuffer;
import java.util.Objects;

import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.*;
import static org.lwjgl.vulkan.EXTSwapchainColorspace.*;
import static org.lwjgl.vulkan.VK10.*;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(VulkanGpuSurface.class)
public class MixinVulkanGpuSurface {
    @Shadow
    @Final
    private VulkanDevice device;

    @Shadow
    private long swapchain;

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
        return Objects.equals(SDLVideo.SDL_GetCurrentVideoDriver(), "wayland");
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
        return colorSpace == VK_COLOR_SPACE_PASS_THROUGH_EXT;
    }

    @Unique
    private boolean hdr_mod$isPassthroughOrLinearForSdl(int colorSpace) {
        return colorSpace == VK_COLOR_SPACE_PASS_THROUGH_EXT;
    }

    @Unique
    private boolean hdr_mod$matchesWaylandFormat(VkSurfaceFormatKHR format, HDRModConfig config, boolean applyConfigCheck) {
        int fmt = format.format();
        int colorSpace = format.colorSpace();
        boolean unormMatches = hdr_mod$isUNORM10bitFormat(fmt)
                && (!applyConfigCheck || config.useUNORMWindowPixelFormat)
                && colorSpace == VK_COLOR_SPACE_PASS_THROUGH_EXT;;
        boolean sfloatMatches = fmt == VK_FORMAT_R16G16B16A16_SFLOAT
                && (!applyConfigCheck || !config.useUNORMWindowPixelFormat)
                && colorSpace == VK_COLOR_SPACE_PASS_THROUGH_EXT;;
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
        HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(bits, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
    }

    @Unique
    private void hdr_mod$applyWaylandProvider(VkSurfaceFormatKHR format) {
        int bits = (format.format() == VK_FORMAT_R16G16B16A16_UNORM || format.format() == VK_FORMAT_R16G16B16A16_SFLOAT) ? 16 : 10;
        long handle = Minecraft.getInstance().getWindow().handle();
        long wlDisplayHandle = SDL_GetPointerProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_WAYLAND_DISPLAY_POINTER, 0);
        long wlSurfaceHandle = SDL_GetPointerProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_WAYLAND_SURFACE_POINTER, 0);
        LOGGER.info("WLDisplayHandle: " + wlDisplayHandle);
        LOGGER.info("WLSurfaceHandle: " + wlSurfaceHandle);
        try {
            if (format.format() != VK_FORMAT_R16G16B16A16_SFLOAT) {
                HDRMod.colorManagementInfoProvider = new WaylandVulkanSDLColorManagementInfoProvider(MemorySegment.ofAddress(wlDisplayHandle), MemorySegment.ofAddress(wlSurfaceHandle),bits, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ, 3000);
                LOGGER.info("Got HDR10 on Vulkan!");
            } else {
                HDRMod.colorManagementInfoProvider = new WaylandVulkanSDLColorManagementInfoProvider(MemorySegment.ofAddress(wlDisplayHandle), MemorySegment.ofAddress(wlSurfaceHandle),16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR, 3000);
                LOGGER.info("Got scRGB on Vulkan!");
            }
        } catch (Throwable ignored) {
        }
    }

    @Unique
    private void hdr_mod$applyNonWaylandProvider(VkSurfaceFormatKHR format) {
        if (format.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT) {
            int bits = format.format() == VK_FORMAT_R16G16B16A16_UNORM ? 16 : 10;
            HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(bits, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
            LOGGER.info("Got HDR10 on Vulkan!");
        } else {
            HDRMod.colorManagementInfoProvider = new VulkanSDLColorManagementInfoProvider(16, Enums.Primaries.SRGB, Enums.TransferFunction.EXT_LINEAR);
            LOGGER.info("Got scRGB on Vulkan!");
        }
    }

    @Unique
    private boolean hdr_mod$hasSetHdrMetadata = false;

    @Inject(method = "acquireNextTexture", at = @At("HEAD"))
    private void hdr_mod$setHdrMetadata(CallbackInfo ci){
        if(hdr_mod$hasSetHdrMetadata || HDRMod.colorManagementInfoProvider instanceof WaylandVulkanSDLColorManagementInfoProvider){
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSwapchains = stack.longs(swapchain);
            VkHdrMetadataEXT.Buffer pMetadata = VkHdrMetadataEXT.calloc(1, stack);
            pMetadata.sType$Default();
            pMetadata.displayPrimaryRed().set(0.708f, 0.292f);
            pMetadata.displayPrimaryGreen().set(0.170f, 0.797f);
            pMetadata.displayPrimaryBlue().set(0.131f, 0.046f);
            pMetadata.whitePoint().set(0.31271f, 0.32902f);
            pMetadata.maxLuminance(10000.0f);
            pMetadata.minLuminance(0.001f);
            pMetadata.maxContentLightLevel(HDRMod.colorManagementInfoProvider.getWindowMaxLuminance(Minecraft.getInstance().getWindow().handle()));
            pMetadata.maxFrameAverageLightLevel(400.0f);
            EXTHdrMetadata.vkSetHdrMetadataEXT(device.vkDevice(), pSwapchains, pMetadata);
        }

        hdr_mod$hasSetHdrMetadata = true;
    }
}
