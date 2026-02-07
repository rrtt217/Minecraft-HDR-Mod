package xyz.rrtt217.fabric;

import net.fabricmc.api.ModInitializer;

import xyz.rrtt217.HDRMod;

public final class HDRModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HDRMod.init();
    }
}
