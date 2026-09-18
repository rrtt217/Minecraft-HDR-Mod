package xyz.rrtt217.HDRMod.mixin.vk;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.renderpearl.backend.vulkan.VulkanFeatureSets;
import com.mojang.renderpearl.backend.vulkan.init.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

import static org.lwjgl.vulkan.EXTHdrMetadata.VK_EXT_HDR_METADATA_EXTENSION_NAME;

@Mixin(VulkanFeatureSets.class)
public class MixinVulkanFeatureSets {
    @Unique
    private static final FeatureSet HDR_METADATA_FEATURESET = new FeatureSet("HDR Metadata", Set.of(VK_EXT_HDR_METADATA_EXTENSION_NAME), Set.of());
    @ModifyReturnValue(method = "optionalFeatureSets", at = @At("RETURN"))
    private static Set<FeatureSet> modifyOptionalFeatureSets(Set<FeatureSet> featureSets) {
        featureSets.add(HDR_METADATA_FEATURESET);
        return featureSets;
    }
}
