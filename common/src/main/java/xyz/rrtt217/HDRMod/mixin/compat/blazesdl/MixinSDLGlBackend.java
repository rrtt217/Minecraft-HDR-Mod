package xyz.rrtt217.HDRMod.mixin.compat.blazesdl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.blazesdl.SDLGlBackend;

import static org.lwjgl.sdl.SDLVideo.*;

@Mixin(SDLGlBackend.class)
public class MixinSDLGlBackend {
    @Inject(method = "setWindowHints", at = @At("TAIL"))
    private void hdr_mod$setWindowHints(CallbackInfo ci){
        SDL_GL_SetAttribute(SDL_GL_RED_SIZE, 16);
        SDL_GL_SetAttribute(SDL_GL_GREEN_SIZE, 16);
        SDL_GL_SetAttribute(SDL_GL_BLUE_SIZE, 16);
        SDL_GL_SetAttribute(SDL_GL_FLOATBUFFERS, 1);
    }
}