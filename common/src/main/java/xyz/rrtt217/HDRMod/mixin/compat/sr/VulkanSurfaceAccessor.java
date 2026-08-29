package xyz.rrtt217.HDRMod.mixin.compat.sr;

import io.homo.superresolution.common.presentation.vulkan.VulkanSurface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VulkanSurface.class)
public interface VulkanSurfaceAccessor {
    @Accessor
    boolean getShown();
}
