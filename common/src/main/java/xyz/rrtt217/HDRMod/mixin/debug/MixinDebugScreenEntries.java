package xyz.rrtt217.HDRMod.mixin.debug;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.debug.HDRModDebugEntry;

@Mixin(DebugScreenEntries.class)
public class MixinDebugScreenEntries {
    @Shadow
    private static Identifier register(Identifier Identifier, DebugScreenEntry debugScreenEntry) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At(value = "RETURN"))
    private static void onInit(CallbackInfo ci) {
        register(Identifier.fromNamespaceAndPath("hdr_mod", "debug"), new HDRModDebugEntry());
    }
}
