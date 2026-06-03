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
        int bpc = GLFW.glfwGetWindowAttrib(handle,GLFW.GLFW_RED_BITS);
        float paperWhiteBrightness = config.customGamePaperWhiteBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(handle) : config.customGamePaperWhiteBrightness;
        float maxBrightness = config.customGamePeakBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowMaxLuminance(handle) : config.customGamePeakBrightness;
        float minBrightness = config.customGameMinimumBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowMinLuminance(handle) : config.customGameMinimumBrightness;
        float eotfEmulate = config.customEotfEmulate < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(handle) : config.customEotfEmulate;
        Enums.Primaries primaries = Enums.Primaries.fromId(config.autoSetPrimaries ? GLFWColorManagementUtils.glfwGetWindowPrimaries(handle) : config.customPrimaries.getId());
        Enums.TransferFunction tf = Enums.TransferFunction.fromId(config.autoSetTransferFunction ? GLFWColorManagementUtils.glfwGetWindowTransfer(handle) : config.customTransferFunction.getId());
        messages.add(String.format("HDR Mod Version %s+%s", Platform.getVersion(), Platform.isFabricLike() ? "fabric" : (Platform.isNeoForge() ? "neoforge" : "forge")));
        messages.add(String.format("Enabled: %b", config.enableHDR));
        messages.add(String.format("Bits Per Channel: %d, Primaries: %s, Transfer Function: %s", bpc, primaries, tf));
        messages.add(String.format("Max Brightness: %.1f, Min Brightness: %.1f", maxBrightness, minBrightness));
        messages.add(String.format("Paperwhite Brightness: %.1f, EOTF Correction Brightness: %.1f", paperWhiteBrightness, eotfEmulate));
    }
}
