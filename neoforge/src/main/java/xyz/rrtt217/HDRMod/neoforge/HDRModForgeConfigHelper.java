package xyz.rrtt217.HDRMod.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class HDRModForgeConfigHelper {

    public static void registerConfig() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, parent) -> AutoConfig.getConfigScreen(HDRModConfig.class, parent).get());
        LOGGER.debug("Registered Config Screen");
    }
}
