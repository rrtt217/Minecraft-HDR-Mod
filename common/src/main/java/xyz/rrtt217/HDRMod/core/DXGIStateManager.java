package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

public class DXGIStateManager {
    public static int fbo = 0;
    public static int texture = 0;
    public static int update(int originalFbo){
        if(originalFbo == 0 && GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WIN32) {
            if(fbo == 0) fbo = GlStateManager.glGenFramebuffers();
            if(texture != GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(Minecraft.getInstance().getWindow().handle())){
                texture = GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(Minecraft.getInstance().getWindow().handle());
                if(texture == 0) return originalFbo;
                bindFrameBufferTextures(fbo, texture, 0, 0, 0,false);
            }
            return fbo;
        }
        return originalFbo;
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
