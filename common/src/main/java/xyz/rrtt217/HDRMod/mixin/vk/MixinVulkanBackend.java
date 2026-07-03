package xyz.rrtt217.HDRMod.mixin.vk;

import com.mojang.blaze3d.vulkan.VulkanBackend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import static org.lwjgl.vulkan.EXTHdrMetadata.VK_EXT_HDR_METADATA_EXTENSION_NAME;

import java.util.Collection;

@Mixin(VulkanBackend.class)
public class MixinVulkanBackend {
    @ModifyArg(method = "createDevice(JLcom/mojang/blaze3d/shaders/ShaderSource;Lcom/mojang/blaze3d/shaders/GpuDebugOptions;Ljava/lang/Runnable;)Lcom/mojang/blaze3d/systems/GpuDevice;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vulkan/VulkanBackend;createDevice(Ljava/util/Collection;Lcom/mojang/blaze3d/vulkan/VulkanPhysicalDevice;Ljava/util/Set;)Lorg/lwjgl/vulkan/VkDevice;"), index = 0)
    private Collection<String> hdr_mod$addHDRMetadataExtension(Collection<String> deviceExtensions){
        deviceExtensions.add(VK_EXT_HDR_METADATA_EXTENSION_NAME);
        return deviceExtensions;
    }
}
