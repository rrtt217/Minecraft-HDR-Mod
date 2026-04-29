package xyz.rrtt217.HDRMod.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;

import static xyz.rrtt217.HDRMod.HDRMod.*;
import static xyz.rrtt217.HDRMod.HDRMod.minecraft;

@EventBusSubscriber(modid = HDRMod.MOD_ID)
public class KeyBindingListener {
    @SubscribeEvent // on the game event bus only on the physical client
    public static void onClientTick(ClientTickEvent.Post event) {
        while (CUSTOM_KEYMAPPING.consumeClick()) {
            Minecraft.getInstance().setScreen(AutoConfigClient.getConfigScreen(HDRModConfig.class, Minecraft.getInstance().screen).get());
        }
        while (CUSTOM_KEYMAPPING_2.consumeClick()) {
            PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.getMainRenderTarget(), (arg) -> minecraft.execute(() -> {
                minecraft.gui.getChat().addClientSystemMessage(arg);
                minecraft.getNarrator().saySystemChatQueued(arg);
            }));
        }
    }
}
