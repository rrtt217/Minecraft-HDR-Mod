package xyz.rrtt217.HDRMod.fabric.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;

import static xyz.rrtt217.HDRMod.HDRMod.*;

public final class HDRModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register key mappings.
        KeyMappingHelper.registerKeyMapping(CUSTOM_KEYMAPPING);
        KeyMappingHelper.registerKeyMapping(CUSTOM_KEYMAPPING_2);
        KeyMappingHelper.registerKeyMapping(CUSTOM_KEYMAPPING_3);
        // Register listeners.
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (CUSTOM_KEYMAPPING.consumeClick()) {
                minecraft.gui.setScreen(AutoConfigClient.getConfigScreen(HDRModConfig.class, minecraft.gui.screen()).get());
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (CUSTOM_KEYMAPPING_2.consumeClick()) {
                PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.gameRenderer.mainRenderTarget(), (arg) -> minecraft.execute(() -> {
                    minecraft.gui.hud.getChat().addClientSystemMessage(arg);
                    minecraft.getNarrator().saySystemChatQueued(arg);
                }));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (CUSTOM_KEYMAPPING_3.consumeClick()) {
                HDRModConfig config = configHolder.getConfig();
                config.enableHDR = !config.enableHDR;
                configHolder.setConfig(config);
                configHolder.save();
            }
        });
    }
}
