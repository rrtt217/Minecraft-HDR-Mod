package xyz.rrtt217.HDRMod.util.platform.mac;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.RuntimeUtils;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;

import static org.lwjgl.sdl.SDLProperties.SDL_GetPointerProperty;
import static org.lwjgl.sdl.SDLVideo.*;

public class MacOpenGlEdr {
    private static final Client objc = Client.getInstance();
    private static final String SET_WANTS_EDR =
            "setWantsExtendedDynamicRangeOpenGLSurface:";

    public static Proxy getCurrentContentView() {
        long nsWindowPtr = SDL_GetPointerProperty(
                SDL_GetWindowProperties(Minecraft.getInstance().getWindow().handle()),
                SDL_PROP_WINDOW_COCOA_WINDOW_POINTER, 0);
        if (nsWindowPtr == 0L) {
            throw new IllegalStateException("Cannot obtain NSWindow pointer");
        }
        // No wrap() in 1.1，use sendProxy
        return objc.sendProxy(new Pointer(nsWindowPtr), "contentView");
    }

    public static void setWantsExtendedDynamicRangeOpenGLSurface(boolean wantsEdr) {
        Proxy view = getCurrentContentView();
        if (view.sendBoolean("respondsToSelector:", RuntimeUtils.sel(SET_WANTS_EDR))) {
            view.send(SET_WANTS_EDR, wantsEdr);
        }
    }
}