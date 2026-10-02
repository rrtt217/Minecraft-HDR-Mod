package xyz.rrtt217.HDRMod.mixin.compat.sr;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.homo.superresolution.common.presentation.vulkan.VulkanSurface;
import io.homo.superresolution.core.graphics.vulkan.VulkanDevice;
import net.minecraft.client.Minecraft;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTHdrMetadata;
import org.lwjgl.vulkan.VkHdrMetadataEXT;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.EXTSwapchainColorspace.VK_COLOR_SPACE_HDR10_ST2084_EXT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_A2B10G10R10_UNORM_PACK32;

@Mixin(targets = "io.homo.superresolution.common.presentation.vulkan.VulkanSwapchain")
public class MixinVulkanSwapchain {
    @Mutable
    @Final
    @Shadow
    private final VulkanSurface surface;
    @Shadow
    private long swapchain;
    @Mutable
    @Final
    @Shadow
    private final VulkanDevice device;

    public MixinVulkanSwapchain(VulkanSurface surface, VulkanDevice device) {
        this.surface = surface;
        this.device = device;
    }

    @Definition(id = "candidate", local = @Local(type = VkSurfaceFormatKHR.class))
    @Definition(id = "colorSpace", method = "Lorg/lwjgl/vulkan/VkSurfaceFormatKHR;colorSpace()I")
    @Expression("candidate.colorSpace() == 0")
    @ModifyExpressionValue(method = "chooseSurfaceFormat", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean hdr_mod$changeRequiredColorSpace(boolean original, @Local(name = "candidate") VkSurfaceFormatKHR candidate) {
        return candidate.colorSpace() == VK_COLOR_SPACE_HDR10_ST2084_EXT;
    }

    @Definition(id = "candidate", local = @Local(type = VkSurfaceFormatKHR.class))
    @Definition(id = "format", method = "Lorg/lwjgl/vulkan/VkSurfaceFormatKHR;format()I")
    @Expression("candidate.format() == 44")
    @ModifyExpressionValue(method = "chooseSurfaceFormat", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean hdr_mod$changeRequiredFormat(boolean original, @Local(name = "candidate") VkSurfaceFormatKHR candidate) {
        return candidate.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32;
    }

    @Inject(method = "recreateLocked", at = @At(value = "INVOKE", target = "Lio/homo/superresolution/common/presentation/vulkan/VulkanSwapchain;destroySemaphores([J)V"))
    private void hdr_mod$setupHdrMetadata(CallbackInfo ci) {
        if(swapchain == 0) return;
        if(device.getVkDevice() == null) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSwapchains = stack.longs(swapchain);
            VkHdrMetadataEXT.Buffer pMetadata = VkHdrMetadataEXT.calloc(1, stack);
            pMetadata.sType$Default();
            pMetadata.displayPrimaryRed().set(0.708f, 0.292f);
            pMetadata.displayPrimaryGreen().set(0.170f, 0.797f);
            pMetadata.displayPrimaryBlue().set(0.131f, 0.046f);
            pMetadata.whitePoint().set(0.3127f, 0.3290f);
            pMetadata.maxLuminance(10000.0f);
            pMetadata.minLuminance(0.001f);
            pMetadata.maxContentLightLevel(HDRMod.colorManagementInfoProvider.getWindowMaxLuminance(Minecraft.getInstance().getWindow().handle()));
            pMetadata.maxFrameAverageLightLevel(400.0f);
            EXTHdrMetadata.vkSetHdrMetadataEXT(device.getVkDevice(), pSwapchains, pMetadata);
        }
    }
}
