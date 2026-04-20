package xyz.rrtt217.HDRMod.mixin;

import org.lwjgl.system.Configuration;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import xyz.rrtt217.HDRMod.util.LibraryExtractor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class HDRModMixinPlugin implements IMixinConfigPlugin {
    private final String MIXIN_CLASS_START = "xyz.rrtt217.HDRMod.mixin.";
    private final String FABRIC_MIXIN_CLASS_START = "xyz.rrtt217.HDRMod.fabric.mixin.";
    private final String NEOFORGE_MIXIN_CLASS_START = "xyz.rrtt217.HDRMod.neoforge.mixin.";
    private final String IXERIS_COMPAT_MIXIN_CLASS_START = "xyz.rrtt217.HDRMod.mixin.compat.ixeris.";
    private final String IMBLOCKER_COMPAT_MIXIN_CLASS_START = "xyz.rrtt217.HDRMod.mixin.compat.imblocker.";
    private final String LIBRARY_VERSION = "3.5.4";
    public static final Logger LOGGER = LoggerFactory.getLogger("hdr_mod_mixin_plugin");
    public static boolean hasGlfwLib = false;
    public static boolean hasIxeris = false;
    public static boolean hasIMblocker = false;

    @Override
    public void onLoad(String s) {
        // Switch glfw lib on MixinPlugin Load.
        HashMap<String, String> glfwLibNames = new HashMap<>();
        glfwLibNames.put("win", "glfw3");
        glfwLibNames.put("mac", "libglfw");
        glfwLibNames.put("linux", "libglfw");
        String glfwLibPath = "";
        boolean loaded = false;
        try {
            glfwLibPath = LibraryExtractor.extractLibraries(glfwLibNames,"glfw", LIBRARY_VERSION).toString();
            loaded = true;
        }
        catch (Exception e) {
            LOGGER.warn("Unable to load libraries from glfw:{}",e.getMessage());
        }
        if(loaded) {
            Configuration.GLFW_LIBRARY_NAME.set(glfwLibPath);
            hasGlfwLib = true;
        }

        try {
            Class.forName("me.decce.ixeris.api.IxerisApi");
            hasIxeris = true;
            LOGGER.info("Ixeris detected, enabling Ixeris compatibility mixins.");
        } catch (ClassNotFoundException ignored) {
            LOGGER.debug("Ixeris not found, Ixeris compatibility mixins will be skipped.");
        }
        try{
            // HACK
            Class.forName("io.github.reserveword.imblocker.common.DebugUtil");
            hasIMblocker = true;
            LOGGER.info("IMblocker detected, enabling IMBlocker compatibility mixins.");
        } catch (ClassNotFoundException ignored) {
            LOGGER.debug("IMBlocker is not found, IMBlocker compatibility mixins will be skipped.");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String tClass, String mClassPath) {
        if (mClassPath.startsWith(IXERIS_COMPAT_MIXIN_CLASS_START)) {
            return hasIxeris;
        }
        if (mClassPath.startsWith(IMBLOCKER_COMPAT_MIXIN_CLASS_START)) {
            return hasIMblocker;
        }
        return mClassPath.startsWith(MIXIN_CLASS_START) || mClassPath.startsWith(FABRIC_MIXIN_CLASS_START) ||  mClassPath.startsWith(NEOFORGE_MIXIN_CLASS_START);
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
