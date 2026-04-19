package xyz.rrtt217.HDRMod.forge;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xyz.rrtt217.HDRMod.HDRMod;

import javax.swing.*;

@Mod(HDRMod.MOD_ID)
public final class HDRModForge {
    public HDRModForge() {
        // Run our common setup.
        HDRMod.init();
        if (FMLEnvironment.dist.isClient()) {
            HDRModForgeConfigHelper.registerConfig();
        }
        if (FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)){
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false);
            JOptionPane.showMessageDialog(null, "HDR Mod is currently incompatible with (Neo)Forge Early Window Control!\n The game will crash and auto disable Early Window Control so the next launch will be successful.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
