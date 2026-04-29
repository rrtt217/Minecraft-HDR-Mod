package xyz.rrtt217.HDRMod.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.util.HDRModInjectHooks;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private void charTyped(long l, CharacterEvent characterEvent){}

    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;grab(Ljava/io/File;Lcom/mojang/blaze3d/pipeline/RenderTarget;Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    private void hdr_mod$onVanillaF2Screenshot(long l, int i, KeyEvent keyEvent, CallbackInfo ci){
        HDRModInjectHooks.setVanillaF2Screenshot();
    }
}
