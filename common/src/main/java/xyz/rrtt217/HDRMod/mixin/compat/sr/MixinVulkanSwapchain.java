package xyz.rrtt217.HDRMod.mixin.compat.sr;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.homo.superresolution.common.presentation.vulkan.VulkanSurface;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.lwjgl.vulkan.EXTSwapchainColorspace.VK_COLOR_SPACE_HDR10_ST2084_EXT;
import static org.lwjgl.vulkan.EXTSwapchainColorspace.VK_COLOR_SPACE_PASS_THROUGH_EXT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_A2B10G10R10_UNORM_PACK32;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

@Mixin(targets = "io.homo.superresolution.common.presentation.vulkan.VulkanSwapchain")
public class MixinVulkanSwapchain {
    @Mutable
    @Final
    @Shadow
    private final VulkanSurface surface;

    public MixinVulkanSwapchain(VulkanSurface surface) {
        this.surface = surface;
    }

    @Definition(id = "candidate", local = @Local(type = VkSurfaceFormatKHR.class))
    @Definition(id = "colorSpace", method = "Lorg/lwjgl/vulkan/VkSurfaceFormatKHR;colorSpace()I")
    @Expression("candidate.colorSpace() == 0")
    @ModifyExpressionValue(method = "chooseSurfaceFormat", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean hdr_mod$changeRequiredColorSpace(boolean original, @Local(name = "candidate") VkSurfaceFormatKHR candidate) {
        return candidate.colorSpace() == VK_COLOR_SPACE_PASS_THROUGH_EXT;
    }

    @Definition(id = "candidate", local = @Local(type = VkSurfaceFormatKHR.class))
    @Definition(id = "format", method = "Lorg/lwjgl/vulkan/VkSurfaceFormatKHR;format()I")
    @Expression("candidate.format() == 44")
    @ModifyExpressionValue(method = "chooseSurfaceFormat", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean hdr_mod$changeRequiredFormat(boolean original, @Local(name = "candidate") VkSurfaceFormatKHR candidate) {
        return candidate.format() == VK_FORMAT_A2B10G10R10_UNORM_PACK32;
    }

    @Inject(method = "presentImage(IZJJJZ)I", at = @At("HEAD"), cancellable = true)
    private void hdr_mod$cancelPresentBeforeShown(int imageIndex, boolean outOfBandPresent, long targetSwapchain, long presentReadyBinary, long immutablePresentId, boolean applicationManaged, CallbackInfoReturnable<Integer> cir){
        if(!((VulkanSurfaceAccessor) (Object) surface).getShown()) cir.setReturnValue(VK_SUCCESS);
    }
}
