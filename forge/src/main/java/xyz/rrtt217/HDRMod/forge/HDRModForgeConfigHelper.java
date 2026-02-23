package xyz.rrtt217.HDRMod.forge;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.client.ConfigScreenHandler;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class HDRModForgeConfigHelper {

    public static void registerConfig() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->  new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> AutoConfig.getConfigScreen(HDRModConfig.class, parent).get()));
        LOGGER.debug("Registered Config Screen");
    }
}
