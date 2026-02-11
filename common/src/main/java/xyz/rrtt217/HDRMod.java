package xyz.rrtt217;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.GLFWColorManagement;
import xyz.rrtt217.Enums.*;
import org.slf4j.Logger;
import xyz.rrtt217.config.HDRModConfig;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Default Internal values for HDR. We should register a hook to change them before shaderpack preload, and after window init.
    public static Primaries WindowPrimaries = Primaries.SRGB;
    public static TransferFunction WindowTransferFunction = TransferFunction.SRGB;

    public HDRMod() {
    }

    public static void init() {
        // Write common init code here.
        AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        LOGGER.info("HDRMod Initialized!");
    }
}