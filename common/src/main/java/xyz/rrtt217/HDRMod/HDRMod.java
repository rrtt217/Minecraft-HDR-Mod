package xyz.rrtt217.HDRMod;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.slf4j.LoggerFactory;

import xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.core.PngjHDRScreenshot;
import xyz.rrtt217.HDRMod.util.Enums.*;
import org.slf4j.Logger;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.ColorManagementInfoProvider;

import static xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility.previousEnableHDR;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Global Renderers.
    public static ColorTransformRenderer PresentationColorTransformRenderer; // Unused now.
    public static ColorTransformRenderer ScreenshotColorTransformRenderer;
    public static ColorTransformRenderer ReplayColorTransformRenderer;

    // Key Mapping.;
    public static final KeyMapping CUSTOM_KEYMAPPING = new KeyMapping(
            "key.hdr_mod.open_config_menu", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F9, // The default keycode
            "key.category.hdr_mod.main" // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping CUSTOM_KEYMAPPING_2 = new KeyMapping(
            "key.hdr_mod.take_hdr_screenshot", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F10, // The default keycode
            "key.category.hdr_mod.main" // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping CUSTOM_KEYMAPPING_3 = new KeyMapping(
            "key.hdr_mod.toggle_hdr", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            "key.category.hdr_mod.main" // The category translation key used to categorize in the Controls screen
    );

    public static boolean isReplayRendering = false;

    public static ConfigHolder<HDRModConfig> configHolder;

    public static ColorManagementInfoProvider colorManagementInfoProvider;

    public HDRMod() {
    }

    public static void init() {
        if(configHolder == null) {
            configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
            configHolder.registerSaveListener(IrisCompatibility::onConfigSave);
            HDRModConfig config = configHolder.getConfig();
            colorManagementInfoProvider = new ColorManagementInfoProvider(config);
            previousEnableHDR = config.enableHDR;
        }
        // Register Key Mapping.
        KeyMappingRegistry.register(CUSTOM_KEYMAPPING);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (CUSTOM_KEYMAPPING.consumeClick()) {
                Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(HDRModConfig.class, Minecraft.getInstance().screen).get());
            }
        });
        KeyMappingRegistry.register(CUSTOM_KEYMAPPING_2);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (CUSTOM_KEYMAPPING_2.consumeClick()) {
                PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.getMainRenderTarget(), (arg) -> minecraft.execute(() -> {
                    minecraft.gui.getChat().addMessage(arg);
                    minecraft.getNarrator().say(arg);
                }));
            }
        });
        KeyMappingRegistry.register(CUSTOM_KEYMAPPING_3);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (CUSTOM_KEYMAPPING_3.consumeClick()) {
                HDRModConfig config = configHolder.getConfig();
                config.enableHDR = !config.enableHDR;
                configHolder.setConfig(config);
                configHolder.save();
            }
        });

        LOGGER.debug("HDRMod Initialized!");
    }
}