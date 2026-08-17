package xyz.rrtt217.HDRMod.core.interop;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import me.decce.ixeris.api.IxerisApi;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.glfw.GLFWDXGIUtils;

import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasIxeris;

public class GLFWGLInteropResourceManager extends GLInteropResourceManager {
    @Override
    int getNewTexture(long handle, int currentTexture) {
        return GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(handle);
    }

    @Override
    public void resize(int width, int height) {
        boolean isMinimized = Minecraft.getInstance().getWindow().isMinimized();
        int newTexture = getNewTexture(Minecraft.getInstance().getWindow().handle(), currentGlTexture);
        if(currentIsMinimized != isMinimized || currentGlTextureWidth != width || currentGlTextureHeight != height) {
            // Rebind Color Texture To FBO
            bindFrameBufferTextures(currentGlFbo, newTexture, 0, 0, GL30.GL_FRAMEBUFFER, false);

            // Validate FBO
            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                HDRMod.LOGGER.error("FBO incomplete after resize: {}", status);
            }
            currentIsMinimized = isMinimized;
            currentGlTextureWidth = width;
            currentGlTextureHeight = height;
            currentGlTexture = newTexture;
        }
    }

    @Override
    public int replaceFbo(int originalFbo) {
        if(!hasIxeris) return super.replaceFbo(originalFbo);

        IxerisApi api = IxerisApi.getInstance();
        if (!api.isEnabled() || api.isOnMainThreadOrInit()) return super.replaceFbo(originalFbo);

        if (!shouldReplaceFbo(originalFbo)) {
            return originalFbo;
        }

        Window window = Minecraft.getInstance().getWindow();
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

        return replaceFboGLOnly(
                DXGISwapchainCache.texture,
                window.getWidth(), window.getHeight(),
                window.isMinimized(), originalFbo);
    }

    private int replaceFboGLOnly(int newTexture, int width, int height,
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