package xyz.rrtt217.HDRMod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import xyz.rrtt217.HDRMod.util.Enums.*;

@Config(name = "hdr_mod")
public class HDRModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public boolean enableHDR = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float uiBrightness = System.getProperty("os.name").startsWith("Windows") ? 203.f : -1.0f; //TODO: GLFW auto get fix on Windows
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customGamePaperWhiteBrightness = System.getProperty("os.name").startsWith("Windows") ? 203.f : -1.0f; //TODO: GLFW auto get fix on Windows
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customGamePeakBrightness = 1000.0f;  //TODO: GLFW auto get
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customGameMinimumBrightness = 0.0f;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customEotfEmulate = System.getProperty("os.name").startsWith("Windows") ? 203.f : 0.0f; //TODO: GLFW auto get fix on Windows

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("advanced")
    public boolean onlyUpgradeNecessaryTexture = false;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("advanced")
    public boolean writeBeforeBlitToMainTarget = false;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("advanced")
    public boolean useRGBA16UNORM = System.getProperty("os.name").toLowerCase().contains("linux");

    @ConfigEntry.Category("debug")
    public boolean autoSetPrimaries = true;
    @ConfigEntry.Category("debug")
    public Primaries customPrimaries = Primaries.SRGB;
    @ConfigEntry.Category("debug")
    public boolean autoSetTransferFunction = true;
    @ConfigEntry.Category("debug")
    public TransferFunction customTransferFunction = TransferFunction.SRGB;
    @ConfigEntry.Category("debug")
    public boolean forceDisableGlfwWorkound = false;
    @ConfigEntry.Category("debug")
    public boolean forceDisableBeforeBlitPipeline = false;
}
