package xyz.rrtt217;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.GLFWColorManagement;
import org.slf4j.Logger;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public HDRMod() {
        System.setProperty("org.lwjgl.glfw.libname", "/home/david/what-is-the-mod/hdr_mod-1.21.10-fabric-neoforge-template/libglfw.so.3.5");
        System.out.println("HDRMod Initialized!");
    }

    public static void init() {
        // Write common init code here.
        LOGGER.info("HDRMod Initialized!");
    }
}