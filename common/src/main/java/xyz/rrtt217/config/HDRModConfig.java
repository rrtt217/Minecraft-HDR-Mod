package xyz.rrtt217.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import xyz.rrtt217.Enums.*;

@Config(name = "hdr_mod")
public class HDRModConfig implements ConfigData {
    public boolean enableHDR = true;

    public boolean overrideSDRWhiteLevel = false;
    public float SDRWhiteLevel = 80.0f;
    public boolean overrideMinLuminance = false;
    public float MinLuminance = 1.0f;
    public boolean overrideMaxLuminance = false;
    public float MaxLuminance = 203.0f;
    public boolean overridePrimaries = false;
    public Primaries primaries = Primaries.SRGB;
    public boolean overrideTransferFunction = false;
    public TransferFunction transferFunction = TransferFunction.SRGB;
}
