package xyz.rrtt217.HDRMod.util.color;

public class VulkanColorManagementInfoProvider extends ColorManagementInfoProvider {
    // We will set these members in MixinVulkanGpuSurface, if not on Wayland.
    private Enums.Primaries primaries;
    private Enums.TransferFunction transferFunction;
    public VulkanColorManagementInfoProvider(int bitsPerChannel, Enums.Primaries primaries, Enums.TransferFunction transferFunction) {
        this.bitsPerChannel = bitsPerChannel;
        this.primaries = primaries;
        this.transferFunction = transferFunction;
    }
    @Override
    public int getBitsPerChannel(long handle) {
        return bitsPerChannel;
    }
    @Override
    public Enums.Primaries getWindowPrimaries(long handle) {
        return this.primaries;
    }

    public void setWindowPrimaries(Enums.Primaries primaries) {
        this.primaries = primaries;
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        return this.transferFunction;
    }

    public void setWindowTransferFunction(Enums.TransferFunction transferFunction) {
        this.transferFunction = transferFunction;
    }
}
