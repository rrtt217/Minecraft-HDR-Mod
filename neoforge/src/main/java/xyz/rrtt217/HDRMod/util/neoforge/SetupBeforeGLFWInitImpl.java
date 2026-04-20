package xyz.rrtt217.HDRMod.util.neoforge;

import net.neoforged.fml.loading.FMLConfig;

import javax.swing.*;

public class SetupBeforeGLFWInitImpl {
    public static void setup(){
        // Update the config just before crash point.
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)){
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false);
            JOptionPane.showMessageDialog(null, "HDR Mod is currently incompatible with (Neo)Forge Early Window Control!\n The game will crash and auto disable Early Window Control so the next launch will be successful.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
