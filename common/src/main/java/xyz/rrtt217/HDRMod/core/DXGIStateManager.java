package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

public class DXGIStateManager {
    public static int fbo = 0;
    private static int currentTexture = 0;
    private static int currentWidth = 0;
    private static int currentHeight = 0;
    private static boolean currentIsMinimized = false;
    public static int replaceFbo(int originalFbo) {
    if (originalFbo != 0 || GLFW.glfwGetPlatform() != GLFW.GLFW_PLATFORM_WIN32)
        return originalFbo;

    Window window = Minecraft.getInstance().getWindow();
    int newTexture = GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(window.handle());
    int width = window.getWidth(), height = window.getHeight();
    boolean isMinimized = window.isMinimized();

    // Check if we need to update the FBO
    if (fbo == 0 || newTexture != currentTexture || width != currentWidth || height != currentHeight || isMinimized != currentIsMinimized) {
        if (newTexture == 0) return originalFbo;

        if (fbo == 0) fbo = GlStateManager.glGenFramebuffers();

        // Rebind Color Texture To FBO
        bindFrameBufferTextures(fbo, newTexture, 0, 0, GL30.GL_FRAMEBUFFER, false);

        // Validate FBO
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            HDRMod.LOGGER.error("FBO incomplete after resize: {}", status);
        }

        currentTexture = newTexture;
        currentWidth = width;
        currentHeight = height;
        currentIsMinimized = isMinimized;
    }
    return fbo;
    }
    public static int replaceFboGLOnly(int newTexture, int width, int height,
                                       boolean isMinimized, int originalFbo) {
        if (newTexture == 0) return originalFbo;

        if (fbo == 0 || newTexture != currentTexture || width != currentWidth
                || height != currentHeight || isMinimized != currentIsMinimized) {
            if (fbo == 0) fbo = GlStateManager.glGenFramebuffers();

            bindFrameBufferTextures(fbo, newTexture, 0, 0, GL30.GL_FRAMEBUFFER, false);

            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                HDRMod.LOGGER.error("FBO incomplete after resize: {}", status);
            }

            currentTexture = newTexture;
            currentWidth = width;
            currentHeight = height;
            currentIsMinimized = isMinimized;
        }
        return fbo;
    }
    private static void bindFrameBufferTextures(int k, int l, int m, int n, int o, boolean useStencil) {
        int i = o == 0 ? GL30.GL_DRAW_FRAMEBUFFER : o;
        int j = GlStateManager.getFrameBuffer(i);
        GlStateManager._glBindFramebuffer(i, k);
        GlStateManager._glFramebufferTexture2D(i, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, l, n);
        GlStateManager._glFramebufferTexture2D(i, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, m, n);
        if (useStencil) {
            GlStateManager._glFramebufferTexture2D(i, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_TEXTURE_2D, m, n);
        } else {
            GlStateManager._glFramebufferTexture2D(i, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_TEXTURE_2D, 0, 0);
        }
        if (o == 0) {
            GlStateManager._glBindFramebuffer(i, j);
        }
    }
}
