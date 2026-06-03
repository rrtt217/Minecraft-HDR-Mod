package xyz.rrtt217.HDRMod.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DebugScreenEntryList.class)
public class MixinDebugScreenEntryList {
    @Shadow
    private Map<Identifier, DebugScreenEntryStatus> allStatuses;

    @Inject(method = "rebuildCurrentList", at = @At("HEAD"))
    private void hdr_mod$addHDRModDebugEntries(CallbackInfo ci) {
        if (!this.allStatuses.containsKey(Identifier.fromNamespaceAndPath("hdr_mod", "debug"))) {
            this.allStatuses.put(Identifier.fromNamespaceAndPath("hdr_mod", "debug"), DebugScreenEntryStatus.IN_OVERLAY);
        }
    }
}
