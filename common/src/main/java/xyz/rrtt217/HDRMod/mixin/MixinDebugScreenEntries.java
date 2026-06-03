package xyz.rrtt217.HDRMod.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.debug.HDRModDebugEntry;

@Mixin(DebugScreenEntries.class)
public class MixinDebugScreenEntries {
    @Shadow
    public static ResourceLocation register(ResourceLocation resourceLocation, DebugScreenEntry debugScreenEntry) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At(value = "RETURN"))
    private static void onInit(CallbackInfo ci) {
        register(ResourceLocation.fromNamespaceAndPath("hdr_mod", "debug"), new HDRModDebugEntry());
    }
}
