package xyz.rrtt217.HDRMod;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.homo.superresolution.common.presentation.vulkan.VulkanPresentationFeature;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility;
import xyz.rrtt217.HDRMod.compat.sr.SRVulkanPresentationColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.core.color.BrightnessValueControl;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.core.screenshot.PngjHDRScreenshot;
import org.slf4j.Logger;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.mixin.features.KeyMappingAccessor;
import xyz.rrtt217.HDRMod.util.color.ColorManagementInfoProvider;

import static xyz.rrtt217.HDRMod.compat.iris.IrisCompatibility.previousEnableHDR;
import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasSr;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Global Renderers.
    public static ColorTransformRenderer PresentationColorTransformRenderer;
    public static ColorTransformRenderer ScreenshotColorTransformRenderer;
    public static ColorTransformRenderer ReplayColorTransformRenderer;

    // Key Mapping.
    public static final KeyMapping.Category HDRModCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hdr_mod","main"));
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.hdr_mod.open_config_menu", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F9, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping HDR_SCREENSHOT = new KeyMapping(
            "key.hdr_mod.take_hdr_screenshot", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F10, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping TOGGLE_HDR = new KeyMapping(
            "key.hdr_mod.toggle_hdr", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping VALUE_UP = new KeyMapping(
            "key.hdr_mod.value_up", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping VALUE_DOWN = new KeyMapping(
            "key.hdr_mod.value_down", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping TOGGLE_VALUE_ADJUSTED = new KeyMapping(
            "key.hdr_mod.toggle_value_adjusted", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping TOGGLE_VALUE_ADJUSTED_BACKWARDS = new KeyMapping(
            "key.hdr_mod.toggle_value_adjusted_backwards", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );

    public static boolean isReplayRendering = false;

    public static ConfigHolder<HDRModConfig> configHolder;

    public static ColorManagementInfoProvider colorManagementInfoProvider;

    public HDRMod() {
    }

    public static void init() {
        // Register config.
        configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        configHolder.registerSaveListener(IrisCompatibility::onConfigSave);

        // Register Key Mapping.
        KeyMappingRegistry.register(OPEN_CONFIG);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (OPEN_CONFIG.consumeClick()) {
                Minecraft.getInstance().setScreen(AutoConfigClient.getConfigScreen(HDRModConfig.class, Minecraft.getInstance().screen).get());
            }
        });
        KeyMappingRegistry.register(HDR_SCREENSHOT);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (HDR_SCREENSHOT.consumeClick()) {
                PngjHDRScreenshot.grab(minecraft.gameDirectory, minecraft.getMainRenderTarget(), (arg) -> minecraft.execute(() -> {
                    minecraft.gui.getChat().addMessage(arg);
                    minecraft.getNarrator().saySystemChatQueued(arg);
                }));
            }
        });
        KeyMappingRegistry.register(TOGGLE_HDR);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (TOGGLE_HDR.consumeClick()) {
                HDRModConfig config = configHolder.getConfig();
                config.enableHDR = !config.enableHDR;
                configHolder.setConfig(config);
                configHolder.save();
            }
        });
        KeyMappingRegistry.register(VALUE_UP);
        KeyMappingRegistry.register(VALUE_DOWN);
        KeyMappingRegistry.register(TOGGLE_VALUE_ADJUSTED);
        KeyMappingRegistry.register(TOGGLE_VALUE_ADJUSTED_BACKWARDS);
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (TOGGLE_VALUE_ADJUSTED.consumeClick()) {
                BrightnessValueControl.valueSwitchAdjusted();
            }
            while (TOGGLE_VALUE_ADJUSTED_BACKWARDS.consumeClick()) {
                BrightnessValueControl.valueSwitchAdjustedBackwards();
            }
            if(!VALUE_DOWN.isDown()) BrightnessValueControl.clearDownTick(); else BrightnessValueControl.stepDownTick();
            if(!VALUE_UP.isDown()) BrightnessValueControl.clearUpTick(); else BrightnessValueControl.stepUpTick();
            while (VALUE_UP.consumeClick()) {
                BrightnessValueControl.valueAdjust( BrightnessValueControl.currentValueUpTick / 5 + 5);
            }
            while (VALUE_DOWN.consumeClick()) {
                BrightnessValueControl.valueAdjust(-BrightnessValueControl.currentValueDownTick / 5 - 5);
            }
        });

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            BrightnessValueControl.consumeClick();
        });

        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(hasSr && VulkanPresentationFeature.isRequested()) {
            colorManagementInfoProvider = new SRVulkanPresentationColorManagementInfoProvider();
        }
        else colorManagementInfoProvider = new ColorManagementInfoProvider(config);
        previousEnableHDR = config.enableHDR;
        LOGGER.debug("HDRMod Initialized!");
    }
}