package xyz.rrtt217.HDRMod.debug;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.util.Platform;

public class HDRModDebugEntry implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer debugScreenDisplayer, @Nullable Level level, @Nullable LevelChunk levelChunk, @Nullable LevelChunk levelChunk2) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        long handle = Minecraft.getInstance().getWindow().handle();
        int bpc = HDRMod.colorManagementInfoProvider.getBitsPerChannel(handle);
        float paperWhiteBrightness = HDRMod.colorManagementInfoProvider.getCurrentGamePaperWhiteBrightness(handle);
        float maxBrightness = HDRMod.colorManagementInfoProvider.getCurrentGamePeakBrightness(handle);
        float minBrightness = HDRMod.colorManagementInfoProvider.getCurrentGameMinimumBrightness(handle);
        float eotfEmulate = HDRMod.colorManagementInfoProvider.getCurrentEotfEmulate(handle);
        Enums.Primaries primaries = HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle);
        Enums.TransferFunction tf = HDRMod.colorManagementInfoProvider.getCurrentTransferFunction(handle);
        debugScreenDisplayer.addToGroup(ResourceLocation.fromNamespaceAndPath("hdr_mod","name"), String.format("HDR Mod Version %s+%s", Platform.getVersion(), Platform.isFabricLike() ? "fabric" : (Platform.isNeoForge() ? "neoforge" : "forge")));
        debugScreenDisplayer.addToGroup(ResourceLocation.fromNamespaceAndPath("hdr_mod","name"), String.format("Enabled: %b", config.enableHDR));
        debugScreenDisplayer.addToGroup(ResourceLocation.fromNamespaceAndPath("hdr_mod","name"), String.format("Bits Per Channel: %d, Primaries: %s, Transfer Function: %s", bpc, primaries, tf));
        debugScreenDisplayer.addToGroup(ResourceLocation.fromNamespaceAndPath("hdr_mod","name"), String.format("Max Brightness: %.1f, Min Brightness: %.1f", maxBrightness, minBrightness));
        debugScreenDisplayer.addToGroup(ResourceLocation.fromNamespaceAndPath("hdr_mod","name"), String.format("Paperwhite Brightness: %.1f, EOTF Correction Brightness: %.1f", paperWhiteBrightness, eotfEmulate));
    }
}
