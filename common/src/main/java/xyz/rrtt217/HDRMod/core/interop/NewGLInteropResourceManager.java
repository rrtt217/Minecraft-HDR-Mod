package xyz.rrtt217.HDRMod.core.interop;

import com.mojang.blaze3d.platform.Window;
import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.WindowsUtil;
import windows.win32.graphics.dxgi.common.DXGI_FORMAT;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.WGLNVDXInterop.*;
import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowProperties;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_WIN32_HWND_POINTER;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.windows.WinBase.GetLastError;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.core.interop.InteropDXDevice.asRaw;

public class NewGLInteropResourceManager extends GLInteropResourceManager {
    private InteropDXDevice dxDevice;
    private long interopDeviceHandle;

    @Override
    void lazyResourceInit(Window window) {
        if(dxDevice == null){
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
            long pointer = SDL_GetPointerProperty(SDL_GetWindowProperties(window.handle()), SDL_PROP_WINDOW_WIN32_HWND_POINTER, 0);
            LOGGER.info("HWND pointer property is {}", pointer);
            dxDevice = new InteropDXDevice(pointer, config.useUNORMWindowPixelFormat ? DXGI_FORMAT.R10G10B10A2_UNORM : DXGI_FORMAT.R16G16B16A16_FLOAT);
            interopDeviceHandle = check(wglDXOpenDeviceNV(asRaw(dxDevice.device).address()));
        }
    }
}
