package xyz.rrtt217.HDRMod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.core.MainTargetBlitShader;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    @Final
    private VanillaPackResources vanillaPackResources;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;preloadUiShader(Lnet/minecraft/server/packs/resources/ResourceProvider;)V"))
    private void preloadMainTargetBlitShader(GameConfig gameConfig, CallbackInfo ci) {
        MainTargetBlitShader.preloadMaintargetBlitShader(vanillaPackResources.asProvider());
    }
}
