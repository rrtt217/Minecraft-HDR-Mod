package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.util.state.ScreenshotStateListener;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "handleGlobalKeyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;grab(Lnet/minecraft/client/Minecraft;Z)V",shift = At.Shift.BEFORE))
    private void hdr_mod$onVanillaF2Screenshot(InputConstants.Key key, boolean controlDown, CallbackInfoReturnable<Boolean> cir){
        ScreenshotStateListener.setVanillaF2Screenshot();
    }
}