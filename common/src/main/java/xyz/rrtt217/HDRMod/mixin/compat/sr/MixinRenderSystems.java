package xyz.rrtt217.HDRMod.mixin.compat.sr;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.homo.superresolution.core.RenderSystems;
import io.homo.superresolution.core.graphics.vulkan.VkRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.lwjgl.vulkan.EXTHdrMetadata.VK_EXT_HDR_METADATA_EXTENSION_NAME;
import static org.lwjgl.vulkan.EXTSwapchainColorspace.VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME;

@Mixin(RenderSystems.class)
public class MixinRenderSystems {
    @WrapOperation(method = "initVulkan", at = @At(value = "INVOKE", target = "Lio/homo/superresolution/core/graphics/vulkan/VkRenderSystem;addDeviceExtension(Ljava/lang/String;)Lio/homo/superresolution/core/graphics/vulkan/VkRenderSystem;", ordinal = 0))
    private static VkRenderSystem hdr_mod$addHdrMetadataExt(VkRenderSystem instance, String ext, Operation<VkRenderSystem> original){
        return original.call(instance, ext).addDeviceExtension(VK_EXT_HDR_METADATA_EXTENSION_NAME).addInstanceExtension(VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME);
    }
}
