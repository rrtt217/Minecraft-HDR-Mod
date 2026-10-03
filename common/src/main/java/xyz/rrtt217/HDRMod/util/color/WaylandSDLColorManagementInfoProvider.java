package xyz.rrtt217.HDRMod.util.color;

import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.platform.wayland.WaylandColorManagement;

import java.lang.foreign.MemorySegment;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class WaylandSDLColorManagementInfoProvider extends SDLColorManagementInfoProvider {
    private final WaylandColorManagement colorManagement;
    private final int primaries;
    private final int transferFunction;
    public WaylandSDLColorManagementInfoProvider(MemorySegment wlDisplayPtr, MemorySegment wlSurfacePtr, int timeoutMs) throws Throwable {
        // Init color management
        this.colorManagement = WaylandColorManagement.attach(wlDisplayPtr, wlSurfacePtr, timeoutMs);

        // Choose Tf and Primaries
        HDRModConfig config = HDRMod.configHolder.getConfig();
        if(!config.useUNORMWindowPixelFormat){
            int status = colorManagement.apply(Enums.TransferFunction.EXT_LINEAR.getId(), Enums.Primaries.SRGB.getId());
            if(status != 0){
                LOGGER.warn(colorManagement.message());
            }
            primaries = Enums.Primaries.SRGB.getId();
            transferFunction = Enums.TransferFunction.EXT_LINEAR.getId();
        }
        else{
            int status = colorManagement.apply(Enums.TransferFunction.ST2084_PQ.getId(), Enums.Primaries.BT2020.getId());
            if(status != 0){
                LOGGER.warn(colorManagement.message());
            }
            primaries = Enums.Primaries.BT2020.getId();
            transferFunction = Enums.TransferFunction.ST2084_PQ.getId();
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

    @Override
    public Enums.Primaries getWindowPrimaries(long handle) {
        return Enums.Primaries.fromId(primaries);
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        return Enums.TransferFunction.fromId(transferFunction);
    }

    public WaylandColorManagement getColorManagement() {
        return colorManagement;
    }
}
