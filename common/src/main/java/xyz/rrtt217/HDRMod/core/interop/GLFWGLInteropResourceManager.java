package xyz.rrtt217.HDRMod.core.interop;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.glfw.GLFWDXGIUtils;

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
}