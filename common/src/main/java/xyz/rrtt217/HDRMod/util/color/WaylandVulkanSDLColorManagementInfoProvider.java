package xyz.rrtt217.HDRMod.util.color;

import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.util.platform.wayland.WaylandColorManagement;

import java.lang.foreign.MemorySegment;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class WaylandVulkanSDLColorManagementInfoProvider extends VulkanSDLColorManagementInfoProvider {
    private final WaylandColorManagement colorManagement;
    public WaylandVulkanSDLColorManagementInfoProvider(MemorySegment wlDisplayPtr, MemorySegment wlSurfacePtr, int bitsPerChannel, Enums.Primaries primaries, Enums.TransferFunction transferFunction, int timeoutMs) throws Throwable {
        super(bitsPerChannel, primaries, transferFunction);
        // Init color management
        this.colorManagement = WaylandColorManagement.attach(wlDisplayPtr, wlSurfacePtr, timeoutMs);
        int status = colorManagement.apply(transferFunction.getId(), primaries.getId());
        if(status != 0){
            LOGGER.warn(colorManagement.message());
        }
    }

    @Override
    public float getWindowSdrWhiteLevel(long handle) {
        return this.colorManagement.sdrWhiteLevel();
    }

    @Override
    public float getWindowMinLuminance(long handle) {
        return this.colorManagement.minLuminance();
    }

    @Override
    public float getWindowMaxLuminance(long handle) {
        return this.colorManagement.maxLuminance();
    }

    public WaylandColorManagement getColorManagement() {
        return colorManagement;
    }
}
