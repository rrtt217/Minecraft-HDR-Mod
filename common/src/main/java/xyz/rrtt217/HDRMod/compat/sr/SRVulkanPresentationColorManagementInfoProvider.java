package xyz.rrtt217.HDRMod.compat.sr;

import xyz.rrtt217.HDRMod.util.color.ColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.util.color.SDLColorManagementInfoProvider;

public class SRVulkanPresentationColorManagementInfoProvider extends SDLColorManagementInfoProvider {
    @Override
    public Enums.Primaries getWindowPrimaries(long handle) {
        return Enums.Primaries.BT2020;
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        return Enums.TransferFunction.ST2084_PQ;
    }
}
