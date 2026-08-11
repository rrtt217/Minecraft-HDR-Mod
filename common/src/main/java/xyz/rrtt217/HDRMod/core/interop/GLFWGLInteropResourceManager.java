package xyz.rrtt217.HDRMod.core.interop;

import com.mojang.blaze3d.opengl.GlStateManager;

import com.sun.jna.Platform;
import org.lwjgl.opengl.GL30;

import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

public class GLFWGLInteropResourceManager extends GLInteropResourceManager {
    // This function is only used in GLFW.
    public int replaceFboGLOnly(int newTexture, int width, int height,
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

    @Override
    boolean shouldReplaceFbo(int originalFbo) {
        return originalFbo == 0 && Platform.isWindows();
    }

    @Override
    int getNewTexture(long handle, int currentTexture) {
        return GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(handle);
    }
}