package xyz.rrtt217.HDRMod.core.interop;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.Window;

import com.sun.jna.Platform;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;

import xyz.rrtt217.HDRMod.HDRMod;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public abstract class GLInteropResourceManager {
    public static int currentGlFbo = 0;
    static int currentGlTexture = 0;
    static int currentGlTextureWidth = 0;
    static int currentGlTextureHeight = 0;
    static boolean currentIsMinimized = false;

    // These functions are shared.
    public int replaceFbo(int originalFbo) {
        if (!shouldReplaceFbo(originalFbo))
            return originalFbo;

        Window window = Minecraft.getInstance().getWindow();
        int width = window.getWidth(), height = window.getHeight();
        boolean isMinimized = window.isMinimized();

        lazyResourceInit(window);

        int newTexture = getNewTexture(window.handle(), currentGlTexture);

        // Check if we need to update the FBO (aka. resize)
        if (currentGlFbo == 0 || newTexture != currentGlTexture || width != currentGlTextureWidth || height != currentGlTextureHeight || isMinimized != currentIsMinimized) {

            if (newTexture == 0) return originalFbo;

            if (currentGlFbo == 0) currentGlFbo = GlStateManager.glGenFramebuffers();

            resizeDxSwapchain(width, height);

            // Rebind Color Texture To FBO
            bindFrameBufferTextures(currentGlFbo, newTexture, 0, 0, GL30.GL_FRAMEBUFFER, false);

            // Validate FBO
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

    void bindFrameBufferTextures(int k, int l, int m, int n, int o, boolean useStencil) {
        int i = o == 0 ? GL30.GL_DRAW_FRAMEBUFFER : o;
        int j = GlStateManager.getFrameBuffer(i);
        GlStateManager._glBindFramebuffer(i, k);
        GlStateManager._glFramebufferTexture2D(i, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, l, n);
        GlStateManager._glFramebufferTexture2D(i, GL30.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, m, n);
        if (useStencil) {
            GlStateManager._glFramebufferTexture2D(i, GL30.GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, m, n);
        } else {
            GlStateManager._glFramebufferTexture2D(i, GL30.GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, 0, 0);
        }
        if (o == 0) {
            GlStateManager._glBindFramebuffer(i, j);
        }
    }

    boolean shouldReplaceFbo(int originalFbo) {
        return originalFbo == 0 && Platform.isWindows();
    }

    int getNewTexture(long handle, int currentTexture) {
        return currentTexture;
    }

    void resizeDxSwapchain(int width, int height) {
    }

    void lazyResourceInit(Window window) {}

    public boolean presentSwapchain() {
        return false;
    }
    public boolean setSwapInterval(int swapInterval) {
        return false;
    }
}