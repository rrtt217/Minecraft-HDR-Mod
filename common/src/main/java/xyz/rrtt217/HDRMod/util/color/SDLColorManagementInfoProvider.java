package xyz.rrtt217.HDRMod.util.color;

import com.sun.jna.Platform;

import static org.lwjgl.sdl.SDLProperties.SDL_GetFloatProperty;
import static org.lwjgl.sdl.SDLVideo.*;

public class SDLColorManagementInfoProvider extends ColorManagementInfoProvider{
    @Override
    public int getBitsPerChannel(long handle) {
        return 16;
    }

    @Override
    public float getWindowSdrWhiteLevel(long handle) {
        if(Platform.isWindows()) return 80.0f * SDL_GetFloatProperty(SDL_GetWindowProperties(handle), SDL_PROP_WINDOW_SDR_WHITE_LEVEL_FLOAT, 1.0f);
        else if(Platform.isMac()) return 80.0f;
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

    @Override
    public Enums.Primaries getWindowPrimaries(long handle) {
        return Enums.Primaries.SRGB;
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        if(Platform.isWindows()) return Enums.TransferFunction.EXT_LINEAR;
        else return Enums.TransferFunction.EXT_SRGB;
    }
}
