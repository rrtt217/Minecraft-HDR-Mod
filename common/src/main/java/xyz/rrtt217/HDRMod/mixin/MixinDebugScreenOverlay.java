package xyz.rrtt217.HDRMod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.util.Platform;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class MixinDebugScreenOverlay {
    @Inject(method = "getSystemInformation", at = @At("RETURN"))
    private void hdr_mod$appendHDRModDebugInformation(CallbackInfoReturnable<List<String>> cir) {
        List<String> messages = cir.getReturnValue();
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        long handle = Minecraft.getInstance().getWindow().getWindow();
        int bpc = HDRMod.colorManagementInfoProvider.getBitsPerChannel(handle);
        float paperWhiteBrightness = HDRMod.colorManagementInfoProvider.getCurrentGamePaperWhiteBrightness(handle);
        float maxBrightness = HDRMod.colorManagementInfoProvider.getCurrentGamePeakBrightness(handle);
        float minBrightness = HDRMod.colorManagementInfoProvider.getCurrentGameMinimumBrightness(handle);
        float eotfEmulate = HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle);
        Enums.Primaries primaries = HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle);
        Enums.TransferFunction tf = HDRMod.colorManagementInfoProvider.getCurrentTransferFunction(handle);
        messages.add(String.format("HDR Mod Version %s+%s", Platform.getVersion(), Platform.isFabricLike() ? "fabric" : (Platform.isNeoForge() ? "neoforge" : "forge")));
        messages.add(String.format("Enabled: %b", config.enableHDR));
        messages.add(String.format("Bits Per Channel: %d, Primaries: %s, Transfer Function: %s", bpc, primaries, tf));
        messages.add(String.format("Max Brightness: %.1f, Min Brightness: %.1f", maxBrightness, minBrightness));
        messages.add(String.format("Paperwhite Brightness: %.1f, EOTF Correction Brightness: %.1f", paperWhiteBrightness, eotfEmulate));
    }
}
