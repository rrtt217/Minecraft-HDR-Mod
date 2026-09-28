package xyz.rrtt217.HDRMod.util.color;

import com.sun.jna.Platform;
import org.lwjgl.BufferUtils;
import xyz.rrtt217.HDRMod.api.color.Enums;

import java.nio.IntBuffer;

import static org.lwjgl.sdl.SDLProperties.SDL_GetFloatProperty;
import static org.lwjgl.sdl.SDLVideo.*;

public class SDLColorManagementInfoProvider extends ColorManagementInfoProvider{
    @Override
    public int getBitsPerChannel(long handle) {
        IntBuffer value = BufferUtils.createIntBuffer(1);
        boolean success = SDL_GL_GetAttribute(SDL_GL_RED_SIZE, value);
        if (success) {
            return value.get(0);
        }
        else  {
            return 0;
        }
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
        if(Platform.isWindows() && config.useUNORMWindowPixelFormat) return Enums.Primaries.BT2020;
        return Enums.Primaries.SRGB;
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        if(Platform.isWindows() && config.useUNORMWindowPixelFormat) return Enums.TransferFunction.ST2084_PQ;
        if(Platform.isWindows()) return Enums.TransferFunction.EXT_LINEAR;
        else return Enums.TransferFunction.EXT_SRGB;
    }
}
