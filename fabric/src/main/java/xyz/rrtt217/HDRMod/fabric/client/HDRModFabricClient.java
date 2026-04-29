package xyz.rrtt217.HDRMod.fabric.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;

import static xyz.rrtt217.HDRMod.HDRMod.CUSTOM_KEYMAPPING;
import static xyz.rrtt217.HDRMod.HDRMod.CUSTOM_KEYMAPPING_2;

public final class HDRModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register key mappings.
        KeyMappingHelper.registerKeyMapping(CUSTOM_KEYMAPPING);
        KeyMappingHelper.registerKeyMapping(CUSTOM_KEYMAPPING_2);
        // Register listeners.
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (CUSTOM_KEYMAPPING.consumeClick()) {
                minecraft.setScreen(AutoConfigClient.getConfigScreen(HDRModConfig.class, minecraft.screen).get());
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (CUSTOM_KEYMAPPING_2.consumeClick()) {
                PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.getMainRenderTarget(), (arg) -> minecraft.execute(() -> {
                    minecraft.gui.getChat().addClientSystemMessage(arg);
                    minecraft.getNarrator().saySystemChatQueued(arg);
                }));
            }
        });
    }
}
