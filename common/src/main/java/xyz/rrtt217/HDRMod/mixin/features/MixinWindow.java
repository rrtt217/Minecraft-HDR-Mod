package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.color.WaylandSDLColorManagementInfoProvider;

@Mixin(Window.class)
public class MixinWindow {
    // Refresh preferred brightness when we receive SDLEvents.SDL_EVENT_WINDOW_HDR_STATE_CHANGED. The event is fired on Wayland when SDL receives preferredChanged(2) and the
    // WpImageDescriptionInfoV1Events.done() is called in their code.
    @Inject(method = "handleEvent", at = @At("HEAD"))
    public void hdr_mod$handleHDRStateChangeEvent(final SDL_Event event, final CallbackInfo ci) {
        if (event.type() == SDLEvents.SDL_EVENT_WINDOW_HDR_STATE_CHANGED) {

            // We can add auto HDR toggle here in the future.

            if(HDRMod.colorManagementInfoProvider instanceof WaylandSDLColorManagementInfoProvider){
                try {
                    ((WaylandSDLColorManagementInfoProvider) HDRMod.colorManagementInfoProvider).getColorManagement().refreshPreferred(1000);
                } catch (Throwable ignored) {
                }
            }
        }
    }
}
