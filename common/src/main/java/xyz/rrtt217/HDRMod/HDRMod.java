package xyz.rrtt217.HDRMod;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.system.Configuration;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.HDRMod.util.Enums.*;
import org.slf4j.Logger;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.LibraryExtractor;

import java.util.HashMap;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Default Internal values for HDR. We should register a hook to change them before shaderpack preload, and after window init.
    public static Primaries WindowPrimaries = Primaries.SRGB;
    public static TransferFunction WindowTransferFunction = TransferFunction.SRGB;

    // Whether we have the glfw lib for the platform.
    public static boolean hasglfwLib = false;


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

    public static boolean enableHDR;

    public HDRMod() {
    }

    public static void init() {
        // Register config.
        AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);

        // Load glfw.
        HashMap<String, String> glfwLibNames = new HashMap<>();
        glfwLibNames.put("win", "glfw3");
        glfwLibNames.put("mac", "LibGLFW");
        glfwLibNames.put("linux", "libglfw");
        String glfwLibPath = "";
        boolean loaded = false;
        try {
            glfwLibPath = LibraryExtractor.extractLibraries(glfwLibNames,"glfw").toString();
            loaded = true;
        }
        catch (Exception e) {
            LOGGER.warn("Unable to load libraries from glfw:{}",e.getMessage());
        }
        if(loaded) {
            Configuration.GLFW_LIBRARY_NAME.set(glfwLibPath);
            hasglfwLib = true;
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
                // Removed screenshot as of now.
            }
        });

        // Set enableHDR once and for all.
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        enableHDR = config.enableHDR;

        LOGGER.debug("HDRMod Initialized!");
    }
}