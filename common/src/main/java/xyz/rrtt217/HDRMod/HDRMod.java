package xyz.rrtt217.HDRMod;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import org.slf4j.Logger;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.color.ColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.util.color.SDLColorManagementInfoProvider;

import static xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility.previousEnableHDR;
import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasBlazeSdl;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Global Renderers.
    public static ColorTransformRenderer PresentationColorTransformRenderer;
    public static ColorTransformRenderer ScreenshotColorTransformRenderer;
    public static ColorTransformRenderer ReplayColorTransformRenderer;

    // Key Mapping.
    public static final KeyMapping.Category HDRModCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hdr_mod","main"));
    public static final KeyMapping CUSTOM_KEYMAPPING = new KeyMapping(
            "key.hdr_mod.open_config_menu", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F9, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping CUSTOM_KEYMAPPING_2 = new KeyMapping(
            "key.hdr_mod.take_hdr_screenshot", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F10, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping CUSTOM_KEYMAPPING_3 = new KeyMapping(
            "key.hdr_mod.toggle_hdr", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );

    public static Minecraft minecraft;

    public static boolean isReplayRendering = false;

    public static ConfigHolder<HDRModConfig> configHolder;

    public static ColorManagementInfoProvider colorManagementInfoProvider;

    public HDRMod() {
    }

    public static void init() {
        // Register config.
        configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        configHolder.registerSaveListener(IrisCompatibility::onConfigSave);
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        previousEnableHDR = config.enableHDR;
        if(hasBlazeSdl) colorManagementInfoProvider = new SDLColorManagementInfoProvider();
        else colorManagementInfoProvider = new ColorManagementInfoProvider();
        LOGGER.debug("HDRMod Initialized!");
    }
}