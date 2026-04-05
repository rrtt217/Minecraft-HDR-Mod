package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

public class DXGIStateManager {
    public static int fbo = 0;
    private static int currentDXTexture = 0;
    private static int currentDXWidth = 0;
    private static int currentDXHeight = 0;
    private static boolean currentDXIsMinimized = false;
    private static boolean currentIsMinimized = false;
    public static int replaceFbo(int originalFbo) {
        if (originalFbo != 0 || GLFW.glfwGetPlatform() != GLFW.GLFW_PLATFORM_WIN32)
            return originalFbo;

        Window window = Minecraft.getInstance().getWindow();
        int newTexture = GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(window.getWindow());
        int width = window.getWidth(), height = window.getHeight();

        // Check if we need to update the FBO
        if (fbo == 0 || newTexture != currentDXTexture || width != currentDXWidth || height != currentDXHeight || currentIsMinimized != currentDXIsMinimized) {
            if (newTexture == 0) return originalFbo;

            if (fbo == 0) fbo = GlStateManager.glGenFramebuffers();

            // Rebind Color Texture To FBO
            bindFrameBufferTextures(fbo, newTexture, 0, 0, GL30.GL_FRAMEBUFFER, false);

            // Validate FBO
            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                HDRMod.LOGGER.error("FBO incomplete after resize: {}", status);
            }

            currentDXTexture = newTexture;
            currentDXWidth = width;
            currentDXHeight = height;
            currentDXIsMinimized = currentIsMinimized;
        }
        return fbo;
    }
    public static void setMinimized(boolean isMinimized) {
        currentIsMinimized = isMinimized;
    }
    private static void bindFrameBufferTextures(int k, int l, int m, int n, int o, boolean useStencil) {
        int i = o == 0 ? GL30.GL_DRAW_FRAMEBUFFER : o;
        int j = GlStateManager._getInteger(i);
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
