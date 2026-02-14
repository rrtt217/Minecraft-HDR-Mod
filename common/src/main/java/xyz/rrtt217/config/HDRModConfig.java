package xyz.rrtt217.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import xyz.rrtt217.util.Enums.*;

@Config(name = "hdr_mod")
public class HDRModConfig implements ConfigData {
    public boolean enableHDR = true;

    public boolean autoSetUIBrightness = false;
    public float uiBrightness = 203.0f;

    public boolean autoSetGamePaperWhiteBrightness = true;
    public float customGamePaperWhiteBrightness = 203.0f;
    public boolean autoSetGamePeakBrightness = true;
    public float customGamePeakBrightness = 1000.0f;
    public boolean autoSetGameMinimumBrightness = true;
    public float customGameMinimumBrightness = 0.0f;

    public boolean autoSetPrimaries = true;
    public Primaries customPrimaries = Primaries.SRGB;
    public boolean autoSetTransferFunction = true;
    public TransferFunction customTransferFunction = TransferFunction.SRGB;

    public boolean forceDisableGlfwWorkound = false;
}
