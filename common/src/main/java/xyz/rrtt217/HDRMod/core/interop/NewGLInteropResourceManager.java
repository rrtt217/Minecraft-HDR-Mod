package xyz.rrtt217.HDRMod.core.interop;

import com.mojang.blaze3d.platform.Window;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWNativeWin32;
import windows.win32.graphics.direct3d11.D3D11_BIND_FLAG;
import windows.win32.graphics.dxgi.common.DXGI_FORMAT;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.color.GLFWDXColorManagementInfoProvider;

import static org.lwjgl.opengl.WGLNVDXInterop.*;
import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowProperties;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_WIN32_HWND_POINTER;
import static org.lwjgl.system.Checks.check;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.core.interop.InteropDXDevice.asRaw;
import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasBlazeSdl;

public class NewGLInteropResourceManager extends GLInteropResourceManager {
    private InteropDXDevice dxDevice;
    private long interopDeviceHandle;
    private SharedTexture glTexture;

    @Override
    void lazyResourceInit(Window window) {
        if(dxDevice == null){
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
            long pointer;
            if(hasBlazeSdl) pointer = SDL_GetPointerProperty(SDL_GetWindowProperties(window.handle()), SDL_PROP_WINDOW_WIN32_HWND_POINTER, 0);
            else pointer = GLFWNativeWin32.glfwGetWin32Window(window.handle());
            LOGGER.info("HWND pointer property is {}", pointer);
            dxDevice = new InteropDXDevice(pointer, config.useUNORMWindowPixelFormat ? DXGI_FORMAT.R10G10B10A2_UNORM : DXGI_FORMAT.R16G16B16A16_FLOAT);
            interopDeviceHandle = check(wglDXOpenDeviceNV(asRaw(dxDevice.device).address()));
            glTexture = dxDevice.createSharedTexture(null, D3D11_BIND_FLAG.D3D11_BIND_RENDER_TARGET | D3D11_BIND_FLAG.D3D11_BIND_SHADER_RESOURCE, window.getWidth(), window.getHeight(), interopDeviceHandle);
            dxDevice.makeCurrent();
            if(HDRMod.colorManagementInfoProvider instanceof GLFWDXColorManagementInfoProvider) {
                ((GLFWDXColorManagementInfoProvider) HDRMod.colorManagementInfoProvider).setDxDevice(dxDevice);
            }
        }
    }

    @Override
    int getNewTexture(long handle, int currentTexture) {
        return glTexture != null ? glTexture.getHandle() : 0;
    }

    @Override
    public void resize(int width, int height) {
        if (dxDevice == null) return;
        if (width == currentGlTextureWidth && height == currentGlTextureHeight && Minecraft.getInstance().getWindow().isMinimized() == currentIsMinimized) return;
        if (width < 1) width = 1;
        if (height < 1) height = 1;

        if (glTexture != null) {
            glTexture.close();
            glTexture = null;
        }
        currentGlTexture = 0;

        dxDevice.resizeSwapChain(width, height);

        glTexture = dxDevice.createSharedTexture(null,
                D3D11_BIND_FLAG.D3D11_BIND_RENDER_TARGET | D3D11_BIND_FLAG.D3D11_BIND_SHADER_RESOURCE,
                width, height, interopDeviceHandle);
    }

    @Override
    public boolean presentSwapchain() {
        if (glTexture == null) return false;
        dxDevice.waitForSwapChainSignal();
        dxDevice.blitSharedTextureToSwapChain(glTexture);
        dxDevice.swapChainPresent();
        return true;
    }

    @Override
    public boolean setSwapInterval(int swapInterval) {
        dxDevice.setSyncInterval(swapInterval);
        return true;
    }

    public InteropDXDevice getDXDevice() {
        return dxDevice;
    }
}
