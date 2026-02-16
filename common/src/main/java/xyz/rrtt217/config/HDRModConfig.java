package xyz.rrtt217.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import xyz.rrtt217.util.Enums.*;

@Config(name = "hdr_mod")
public class HDRModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableHDR = true;

    @ConfigEntry.Gui.Tooltip
    public float uiBrightness = -1.0f;
    @ConfigEntry.Gui.Tooltip
    public float customGamePaperWhiteBrightness = -1.0f;
    @ConfigEntry.Gui.Tooltip
    public float customGamePeakBrightness = 1000.0f;
    @ConfigEntry.Gui.Tooltip
    public float customGameMinimumBrightness = 0.0f;
    @ConfigEntry.Gui.Tooltip
    public float customEotfEmulate = System.getProperty("os.name").startsWith("Windows") ? -1.0f : 0.0f;

    @ConfigEntry.Gui.Tooltip
    public boolean onlyUpgradeNecessaryTexture = false;
    @ConfigEntry.Gui.Tooltip
    public boolean writeBeforeBlitToMainTarget = false;

    public boolean autoSetPrimaries = true;
    public Primaries customPrimaries = Primaries.SRGB;
    public boolean autoSetTransferFunction = true;
    public TransferFunction customTransferFunction = TransferFunction.SRGB;

    public boolean forceDisableGlfwWorkound = false;
    public boolean forceDisableBeforeBlitPipeline = false;
}
