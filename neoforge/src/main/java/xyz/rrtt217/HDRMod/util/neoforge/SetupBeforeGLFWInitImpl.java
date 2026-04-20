package xyz.rrtt217.HDRMod.util.neoforge;

import net.neoforged.fml.loading.FMLConfig;

public class SetupBeforeGLFWInitImpl {
    public static void setup(){
        // Update the config just before crash point.
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)){
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false);
            throw new IllegalStateException("HDR Mod is currently incompatible with (Neo)Forge Early Window Control!\n We auto disabled Early Window Control before crashing so the next launch will be successful.");
        }
    }
}
