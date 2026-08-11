package xyz.rrtt217.HDRMod.core.interop;

import com.mojang.blaze3d.platform.Window;
import me.shedaniel.autoconfig.AutoConfig;
import windows.win32.graphics.direct3d11.D3D11_BIND_FLAG;
import windows.win32.graphics.dxgi.common.DXGI_FORMAT;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import static org.lwjgl.opengl.WGLNVDXInterop.*;
import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowProperties;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_WIN32_HWND_POINTER;
import static org.lwjgl.system.Checks.check;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.core.interop.InteropDXDevice.asRaw;

public class NewGLInteropResourceManager extends GLInteropResourceManager {
    private InteropDXDevice dxDevice;
    private long interopDeviceHandle;
    private SharedTexture glTexture;

    @Override
    void lazyResourceInit(Window window) {
        if(dxDevice == null){
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
            long pointer = SDL_GetPointerProperty(SDL_GetWindowProperties(window.handle()), SDL_PROP_WINDOW_WIN32_HWND_POINTER, 0);
            LOGGER.info("HWND pointer property is {}", pointer);
            dxDevice = new InteropDXDevice(pointer, config.useUNORMWindowPixelFormat ? DXGI_FORMAT.R10G10B10A2_UNORM : DXGI_FORMAT.R16G16B16A16_FLOAT);
            interopDeviceHandle = check(wglDXOpenDeviceNV(asRaw(dxDevice.device).address()));
            glTexture = dxDevice.createSharedTexture(null, D3D11_BIND_FLAG.D3D11_BIND_RENDER_TARGET | D3D11_BIND_FLAG.D3D11_BIND_SHADER_RESOURCE, window.getWidth(), window.getHeight(), interopDeviceHandle);
        }
    }

    @Override
    int getNewTexture(long handle, int currentTexture) {
        return glTexture.getHandle();
    }

    @Override
    void resizeDxSwapchain(int width, int height) {
        dxDevice.resizeSwapChain(width, height);
    }

    @Override
    public boolean presentSwapchain() {
        dxDevice.blitSharedTextureToSwapChain(glTexture);
        dxDevice.swapChainPresent();
        return true;
    }

    @Override
    public boolean setSwapInterval(int swapInterval) {
        dxDevice.setSyncInterval(swapInterval);
        return true;
    }
}
