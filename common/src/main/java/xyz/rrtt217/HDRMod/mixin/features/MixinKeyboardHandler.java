package xyz.rrtt217.HDRMod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.HDRModInjectHooks;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private void charTyped(long l, int i, int j){}

    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;grab(Ljava/io/File;Lcom/mojang/blaze3d/pipeline/RenderTarget;Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    private void hdr_mod$onVanillaF2Screenshot(long l, int i, int j, int k, int m, CallbackInfo ci){
        HDRModInjectHooks.setVanillaF2Screenshot();
    }

    // Fix IME on Rebased LWJGL 3.4.1.
    // Still need IMBlocker to properly show preedit.
    @Inject(method = "setup", at = @At(value = "TAIL"))
    private void hdr_mod$afterSetUp(long l, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(!config.enableCharCallbackReplacement) return;
        GLFW.glfwSetCharModsCallback(l, null);
        GLFW.glfwSetCharCallback(l, (lx, i) -> {
            this.minecraft.execute(() -> this.charTyped(lx, i, 0));
        });
    }
}
