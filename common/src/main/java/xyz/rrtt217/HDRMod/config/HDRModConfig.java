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
    @ConfigEntry.Category("screenshot")
    public BehaviorOnVanillaScreenshotCalled behaviorOnVanillaScreenshotCalled = BehaviorOnVanillaScreenshotCalled.ONLY_VANILLA;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("screenshot")
    public BehaviorOnVanillaScreenshotCalled behaviorOnVanillaF2 = BehaviorOnVanillaScreenshotCalled.ONLY_VANILLA;

    @ConfigEntry.Category("replay")
    public boolean enableReplayHDRVideoExport = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("replay")
    public String replayRecommendedCommandline = "-color_primaries bt2020 -color_trc smpte2084 -color_range pc -y -f rawvideo -pix_fmt rgba64 -s %WIDTH%x%HEIGHT% -r %FPS% -i - %FILTERS%-an -colorspace bt2020nc -color_primaries bt2020 -color_trc smpte2084 -color_range pc -c:v libx265 -b:v %BITRATE% -pix_fmt yuv420p10le \\\"%FILENAME%\\\"";
    @ConfigEntry.Category("replay")
    public float replayUIBrightness = 203.0f;
    @ConfigEntry.Category("replay")
    public float replayGamePaperWhiteBrightness = 203.0f;
    @ConfigEntry.Category("replay")
    public float replayGamePeakBrightness = 10000.0f;
    @ConfigEntry.Category("replay")
    public float replayGameMinimumBrightness = 0.0f;
    // Due to export pixel format, the primaries is always Rec.2020 and tf always PQ.

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("ime")
    public boolean enableCharCallbackReplacement = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("ime")
    public boolean enableIMBlockerSetStateIntegration = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("ime")
    public boolean enableIMBlockerSetEnglishStateIntegration = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("ime")
    public boolean enableIMBlockerSetPreeditOverlayPositionIntegration = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("ime")
    public boolean enableIMBlockerSetPreeditCallbackIntegration = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("ime")
    public boolean PreeditOverlayPositionFollowMonitorScale = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("advanced")
    public boolean useUNORMWindowPixelFormat = System.getProperty("os.name").toLowerCase().contains("linux");
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("advanced")
    public boolean forceActivateGlDxInterop = false;

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
    public boolean forceDisableBeforeBlitPipeline = false;
}
