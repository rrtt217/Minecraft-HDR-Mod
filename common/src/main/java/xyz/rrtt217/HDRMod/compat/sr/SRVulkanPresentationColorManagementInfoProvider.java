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

    @Override
    public float getWindowSdrWhiteLevel(long handle) {
        if(hasSr && VulkanPresentationFeature.isRequested()) return super.getWindowSdrWhiteLevel(PresentationWindowState.presentationHandle());
        return super.getWindowSdrWhiteLevel(handle);
    }

    @Override
    public float getWindowMinLuminance(long handle) {
        if(hasSr && VulkanPresentationFeature.isRequested()) return super.getWindowMinLuminance(PresentationWindowState.presentationHandle());
        return super.getWindowMinLuminance(handle);
    }

    @Override
    public float getWindowMaxLuminance(long handle) {
        if(hasSr && VulkanPresentationFeature.isRequested()) return super.getWindowMaxLuminance(PresentationWindowState.presentationHandle());
        return super.getWindowMaxLuminance(handle);
    }
}
