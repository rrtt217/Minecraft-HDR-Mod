package xyz.rrtt217.HDRMod.mixin.compat.blazesdl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.blazesdl.SDLGlSurface;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;

@Mixin(SDLGlSurface.class)
public class MixinSDLGlSurface {
    /**
     * @author rrtt217
     * @reason
     */
    @Overwrite
    public void present() {
        DXGIStateManager.presentDxSwapChain(0);
    }

    @Redirect(method = "configure", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDLVideo;SDL_GL_SetSwapInterval(I)Z"))
    private boolean configure(int interval){
        return true;
    }
}
