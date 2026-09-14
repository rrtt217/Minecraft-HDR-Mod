package xyz.rrtt217.HDRMod.compat.sr;

import io.homo.superresolution.common.presentation.vulkan.VulkanPresentationFeature;
import io.homo.superresolution.common.presentation.window.PresentationWindowState;
import xyz.rrtt217.HDRMod.util.color.ColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.api.color.Enums;

import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasSr;

public class SRVulkanPresentationColorManagementInfoProvider extends ColorManagementInfoProvider {
    @Override
    public Enums.Primaries getWindowPrimaries(long handle) {
        return Enums.Primaries.BT2020;
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        return Enums.TransferFunction.ST2084_PQ;
    }
}
