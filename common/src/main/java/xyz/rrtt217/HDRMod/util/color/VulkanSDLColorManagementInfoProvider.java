package xyz.rrtt217.HDRMod.util.color;

import com.sun.jna.Platform;

import static org.lwjgl.sdl.SDLProperties.SDL_GetFloatProperty;
import static org.lwjgl.sdl.SDLVideo.*;

public class VulkanSDLColorManagementInfoProvider extends VulkanColorManagementInfoProvider {
    public VulkanSDLColorManagementInfoProvider(int bitsPerChannel, Enums.Primaries primaries, Enums.TransferFunction transferFunction) {
        super(bitsPerChannel, primaries, transferFunction);
    }

    @Override
    public float getWindowSdrWhiteLevel(long handle) {
        if(Platform.isWindows()) return 80.0f * SDL_GetFloatProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_SDR_WHITE_LEVEL_FLOAT, 1.0f);
        else if(Platform.isMac()) return 80.0f;
        else if(getWindowTransferFunction(handle) == Enums.TransferFunction.EXT_LINEAR) return 80.0f;
        else return 203.0f;
    }

    @Override
    public float getWindowMinLuminance(long handle) {
        return 0.0f;
    }

    @Override
    public float getWindowMaxLuminance(long handle) {
        return getWindowSdrWhiteLevel(handle) * SDL_GetFloatProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_HDR_HEADROOM_FLOAT, 1.0f);
    }
}
