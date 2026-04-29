package xyz.rrtt217.HDRMod.neoforge;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;
import xyz.rrtt217.HDRMod.neoforge.client.KeyBindingListener;

import static xyz.rrtt217.HDRMod.HDRMod.*;

@Mod(HDRMod.MOD_ID)
public final class HDRModNeoForge {
    public HDRModNeoForge(IEventBus modBus) {
        // Run our common setup.
        HDRMod.init();
        if (FMLEnvironment.getDist().isClient()) {
            HDRModForgeConfigHelper.registerConfig();
            modBus.addListener(this::registerBindings);
            NeoForge.EVENT_BUS.register(KeyBindingListener.class);
        }
    }
    @SubscribeEvent // on the mod event bus only on the physical client
    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(HDRModCategory);
        event.register(CUSTOM_KEYMAPPING);
        event.register(CUSTOM_KEYMAPPING_2);
    }
}
