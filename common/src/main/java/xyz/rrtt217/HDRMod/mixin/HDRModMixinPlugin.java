package xyz.rrtt217.HDRMod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import org.lwjgl.system.Configuration;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.LibraryExtractor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class HDRModMixinPlugin implements IMixinConfigPlugin {
    private final String MIXIN_CLASS_START = "xyz.rrtt217.HDRMod.mixin.";
    public static boolean hasGlfwLib = false;
    public static boolean enableHDR = true;

    @Override
    public void onLoad(String s) {
        // Register config.
        AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);

        // Set enableHDR once and for all.
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        enableHDR = config.enableHDR;

        // Switch glfw lib on MixinPlugin Load.
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
            hasGlfwLib = true;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String tClass, String mClassPath) {
        return mClassPath.startsWith(MIXIN_CLASS_START);
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
