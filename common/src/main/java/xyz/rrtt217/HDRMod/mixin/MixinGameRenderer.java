package xyz.rrtt217.HDRMod.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.core.MainTargetBlitShader;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "preloadUiShader",at = @At("HEAD"))
    private void preloadMainTargetBlitShader(ResourceProvider provider, CallbackInfo ci) {
        MainTargetBlitShader.preloadMaintargetBlitShader(provider);
    }
}
