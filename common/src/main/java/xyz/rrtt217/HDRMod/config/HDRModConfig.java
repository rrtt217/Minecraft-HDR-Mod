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
    public float uiBrightness = -1.0f;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customGamePaperWhiteBrightness = -1.0f;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customGamePeakBrightness = 1000.0f;  // It's broken intentionally for tev glfw, the system always reports the software limit instead of hardware.
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customGameMinimumBrightness = 0.0f; // Probably unused and broken.
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("general")
    public float customEotfEmulate = System.getProperty("os.name").startsWith("Windows") ? -1.0f : 0.0f;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("advanced")
    public boolean UseUNORMBufferOnLinux = System.getProperty("os.name").toLowerCase().contains("linux");

    @ConfigEntry.Category("debug")
    public boolean autoSetPrimaries = true;
    @ConfigEntry.Category("debug")
    public Primaries customPrimaries = Primaries.SRGB;
    @ConfigEntry.Category("debug")
    public boolean autoSetTransferFunction = true;
    @ConfigEntry.Category("debug")
    public TransferFunction customTransferFunction = TransferFunction.SRGB;
    @ConfigEntry.Category("debug")
    public boolean forceDisableGlfwWorkaround = false;
    @ConfigEntry.Category("debug")
    public boolean forceDisableBlitShaderReplacement = false;
}
