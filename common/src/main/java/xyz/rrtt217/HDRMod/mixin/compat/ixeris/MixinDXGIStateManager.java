package xyz.rrtt217.HDRMod.mixin.compat.ixeris;

import com.mojang.blaze3d.opengl.GlStateManager;
import me.decce.ixeris.api.IxerisApi;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.core.interop.GLFWGLInteropResourceManager;
import xyz.rrtt217.HDRMod.core.interop.DXGISwapchainCache;
import xyz.rrtt217.HDRMod.util.glfw.GLFWDXGIUtils;

@Mixin(value = GLFWGLInteropResourceManager.class, remap = false)
public class MixinDXGIStateManager {
    @Shadow
    public static int currentGlFbo;
    @Shadow
    static int currentGlTexture;
    @Shadow
    static int currentGlTextureWidth;
    @Shadow
    static int currentGlTextureHeight;
    @Shadow
    static boolean currentIsMinimized;
    @Shadow
    static void bindFrameBufferTextures(int currentGlFbo, int newTexture, int i, int i1, int glFramebuffer, boolean b) {
    }

    /**
     * @author rrtt217. CommandGenius
     * @reason Ixeris compat
     */
    @Overwrite
    private static void replaceFbo(int originalFbo,
                                                       CallbackInfoReturnable<Integer> cir) {
        IxerisApi api = IxerisApi.getInstance();
        if (!api.isEnabled() || api.isOnMainThreadOrInit()) return;

        if (originalFbo != 0 || GLFW.glfwGetPlatform() != GLFW.GLFW_PLATFORM_WIN32) {
            cir.setReturnValue(originalFbo);
            return;
        }

        com.mojang.blaze3d.platform.Window window =
                net.minecraft.client.Minecraft.getInstance().getWindow();
        long handle = window.handle();

        if (GLFWDXGIUtils.glfwGetPendingDXGIResize(handle) > 0) {
            GLFWDXGIUtils.glfwReleaseCurrentContext();
            api.query(() -> {
                GLFWDXGIUtils.glfwCompletePendingDXGIResize(handle);
                return null;
            });
            GLFWDXGIUtils.glfwReacquireCurrentContext(handle);
            DXGISwapchainCache.texture    = 0;
            DXGISwapchainCache.lastWidth  = 0;
            DXGISwapchainCache.lastHeight = 0;
        }

        int tex = GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(handle);
        if (tex != DXGISwapchainCache.texture) {
            DXGISwapchainCache.texture    = tex;
            DXGISwapchainCache.lastWidth  = 0;
            DXGISwapchainCache.lastHeight = 0;
        }

        cir.setReturnValue(hdr_mod$replaceFboGLOnly(
                DXGISwapchainCache.texture,
                window.getWidth(), window.getHeight(),
                window.isMinimized(), originalFbo));
    }

    @Unique
    private static int hdr_mod$replaceFboGLOnly(int newTexture, int width, int height,
                                                boolean isMinimized, int originalFbo) {
        if (newTexture == 0) return originalFbo;

        if (currentGlFbo == 0 || newTexture != currentGlTexture || width != currentGlTextureWidth
                || height != currentGlTextureHeight || isMinimized != currentIsMinimized) {
            if (currentGlFbo == 0) currentGlFbo = GlStateManager.glGenFramebuffers();

            bindFrameBufferTextures(currentGlFbo, newTexture, 0, 0, GL30.GL_FRAMEBUFFER, false);

            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                HDRMod.LOGGER.error("FBO incomplete after resize: {}", status);
            }

            currentGlTexture = newTexture;
            currentGlTextureWidth = width;
            currentGlTextureHeight = height;
            currentIsMinimized = isMinimized;
        }
        return currentGlFbo;
    }
}
