package xyz.rrtt217.HDRMod.mixin.vk;

import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.vulkan.VulkanInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.EXTSwapchainColorspace.VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(VulkanInstance.class)
public class MixinVulkanInstance {
    @Shadow @Final private final Set<String> enabledExtensions = new HashSet<>();
    @Shadow private Set<String> getSupportedInstanceExtensions() throws BackendCreationException {
        throw new UnsupportedOperationException("Implemented via mixin");
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vulkan/VulkanDebug;create(IZLjava/util/Set;Ljava/util/Set;)Lcom/mojang/blaze3d/vulkan/VulkanDebug;", ordinal = 0))
    private void hdr_mod$addSwapchainColorspaceExtension(CallbackInfo ci) throws BackendCreationException {
        if(this.getSupportedInstanceExtensions().contains(VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME)){
            this.enabledExtensions.add(VK_EXT_SWAPCHAIN_COLOR_SPACE_EXTENSION_NAME);
        }
        else{
            LOGGER.warn("VulkanInstance does not support swapchain colorspace extensions, HDR may not work!");
        }
    }
}
