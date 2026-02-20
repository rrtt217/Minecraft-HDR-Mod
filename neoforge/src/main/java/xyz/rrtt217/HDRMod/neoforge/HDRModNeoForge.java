package xyz.rrtt217.HDRMod.neoforge;

import net.neoforged.fml.common.Mod;

import net.neoforged.fml.loading.FMLEnvironment;
import xyz.rrtt217.HDRMod.HDRMod;

@Mod(HDRMod.MOD_ID)
public final class HDRModNeoForge {
    public HDRModNeoForge() {
        // Run our common setup.
        HDRMod.init();
        if (FMLEnvironment.dist.isClient()) {
            HDRModForgeConfigHelper.registerConfig();
        }
    }
}
