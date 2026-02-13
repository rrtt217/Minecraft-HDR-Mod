package xyz.rrtt217;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.Identifier;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.renderer.RenderPipelines;
import org.lwjgl.system.Configuration;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.core.CommonFloatUBO;
import xyz.rrtt217.util.Enums.*;
import org.slf4j.Logger;
import xyz.rrtt217.config.HDRModConfig;
import xyz.rrtt217.util.LibraryExtractor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Default Internal values for HDR. We should register a hook to change them before shaderpack preload, and after window init.
    public static Primaries WindowPrimaries = Primaries.SRGB;
    public static TransferFunction WindowTransferFunction = TransferFunction.SRGB;

    // Whether we have the glfw lib for the platform.
    public static boolean hasglfwLib = false;

    // Things about BEFORE_BLIT pass. May be moved to a seperate class in core/.
    public static RenderPipeline.Builder renderPipelineBuilder = RenderPipeline.builder(new RenderPipeline.Snippet[0]).withLocation("pipeline/before_blit").withFragmentShader(Identifier.fromNamespaceAndPath("hdr_mod","before_blit")).withVertexShader("core/screenquad").withSampler("InSampler").withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES);
    public static RenderPipeline BEFORE_BLIT = renderPipelineBuilder.build();
    public static GpuTexture beforeBlitTexture;
    public static GpuTextureView beforeBlitTextureView;
    public static boolean isBeforeBlitReady = false;

    // Global UI brightness UBO.
    public static CommonFloatUBO UiLuminanceUBO;
    public HDRMod() {
    }

    public static void init() {
        // Write common init code here.
        AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
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
        catch (Exception ignored) {
        }
        if(loaded) {
            Configuration.GLFW_LIBRARY_NAME.set(glfwLibPath);
            hasglfwLib = true;
        }
        LOGGER.info("HDRMod Initialized!");
    }
}