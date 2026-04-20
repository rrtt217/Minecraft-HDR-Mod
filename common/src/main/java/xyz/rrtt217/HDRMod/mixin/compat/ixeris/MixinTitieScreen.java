package xyz.rrtt217.HDRMod.mixin.compat.ixeris;

import me.decce.ixeris.VersionCompatUtils;
import me.decce.ixeris.api.IxerisApi;
import me.decce.ixeris.core.threading.MainThreadDispatcher;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.compat.ixeris.CallbackDispatchers_334;

@Mixin(TitleScreen.class)
public class MixinTitieScreen {
    @Inject(method = "init", at = @At("HEAD"))
    private void hdr_mod$ixerisInit(CallbackInfo ci) {
        IxerisApi api = IxerisApi.getInstance();
        if (!api.isInitialized()) return;
        MainThreadDispatcher.runLater(() -> CallbackDispatchers_334.validateAll(VersionCompatUtils.getMinecraftWindow()));
    }
}
