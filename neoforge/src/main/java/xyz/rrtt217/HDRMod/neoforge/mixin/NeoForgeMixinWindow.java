package xyz.rrtt217.HDRMod.neoforge.mixin;

import com.mojang.blaze3d.platform.Window;
import net.neoforged.fml.loading.EarlyLoadingScreenController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Window.class)
public class NeoForgeMixinWindow {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/loading/EarlyLoadingScreenController;current()Lnet/neoforged/fml/loading/EarlyLoadingScreenController;"))
    private static EarlyLoadingScreenController hdr_mod$init() {
        return null;
    }
}
