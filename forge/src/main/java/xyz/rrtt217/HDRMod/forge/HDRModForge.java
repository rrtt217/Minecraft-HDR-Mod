package xyz.rrtt217.HDRMod.forge;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.loading.FMLEnvironment;
import xyz.rrtt217.HDRMod.HDRMod;

@Mod(HDRMod.MOD_ID)
public final class HDRModForge {
    public HDRModForge() {
        // Run our common setup.
        HDRMod.init();
        if (FMLEnvironment.dist.isClient()) {
            HDRModForgeConfigHelper.registerConfig();
        }
    }
}
