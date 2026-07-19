package xyz.rrtt217.HDRMod.mixin.compat.blazesdl;

import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLVideo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.fifthlight.blazesdl.SDLGlSurface;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;

import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.SDL_GetWindowProperties;
import static org.lwjgl.sdl.SDLVideo.SDL_PROP_WINDOW_WIN32_HWND_POINTER;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.core.DXGIStateManager.interopShimContext;

@Mixin(SDLGlSurface.class)
public class MixinSDLGlSurface {
    /**
     * @author rrtt217
     * @reason
     */
    @Overwrite
    public void present() {
        if(interopShimContext == 0){
            long pointer = SDL_GetPointerProperty(SDL_GetWindowProperties(Minecraft.getInstance().getWindow().handle()), SDL_PROP_WINDOW_WIN32_HWND_POINTER, 0);
            LOGGER.info("HWND pointer property is {}", pointer);
            DXGIStateManager.createDxDevice(pointer, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        DXGIStateManager.presentDxSwapChain(0);
    }
}
