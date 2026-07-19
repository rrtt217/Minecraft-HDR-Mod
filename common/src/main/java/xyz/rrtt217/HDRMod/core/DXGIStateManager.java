package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import org.lwjgl.opengl.WGLNVDXInterop;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.DX11InteropShim;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.WGLNVDXInterop.*;
import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasBlazeSdl;
import static xyz.rrtt217.HDRMod.util.DX11InteropShim.DXGI_FORMAT_R16G16B16A16_FLOAT;

public class DXGIStateManager {
    // These members are shared, used both in GLFW and SDL path.
    public static int fbo = 0;
    private static int currentTexture = 0;
    private static int currentWidth = 0;
    private static int currentHeight = 0;
    private static boolean currentIsMinimized = false;

    // These members are only used in SDL path.
    public static long interopShimContext = 0;
    private static long dxDevice = 0;
    private static long interopDevice = 0;
    private static long interopSharedHandle = 0;
    private static long interopObject = 0;
    private static long interopTexturePtr = 0;
    private static int glTexture = 0;

    // These functions are shared.
    public static int replaceFbo(int originalFbo) {
    if (originalFbo != 0 || GLFW.glfwGetPlatform() != GLFW.GLFW_PLATFORM_WIN32)
        return originalFbo;

    Window window = Minecraft.getInstance().getWindow();
    int newTexture;
    if(hasBlazeSdl){
        newTexture = glTexture;
    }
    else newTexture = GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(window.handle());
    int width = window.getWidth(), height = window.getHeight();
    boolean isMinimized = window.isMinimized();

    // Check if we need to update the FBO
    if (fbo == 0 || newTexture != currentTexture || width != currentWidth || height != currentHeight || isMinimized != currentIsMinimized) {
        if (newTexture == 0) return originalFbo;

        if (fbo == 0) fbo = GlStateManager.glGenFramebuffers();

        resizeDxSwapChain(width, height);

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

    // These functions are only used in SDL path.
    public static void createDxDevice(long hwnd, int width, int height) {
        interopShimContext = DX11InteropShim.nCreate(hwnd, width, height, DXGI_FORMAT_R16G16B16A16_FLOAT, true);
        if (interopShimContext == 0) {
            HDRMod.LOGGER.error("Failed to create DX11 device context: {}",
                    DX11InteropShim.nGetLastError());
            return;
        }

        dxDevice = DX11InteropShim.nGetDevice(interopShimContext);
        interopDevice = wglDXOpenDeviceNV(dxDevice);
        interopSharedHandle = DX11InteropShim.nGetSharedTextureHandle(interopShimContext);
        interopTexturePtr = DX11InteropShim.nGetInteropTexture(interopShimContext);

        if (interopDevice == 0) {
            HDRMod.LOGGER.error("Failed to open WGL DX interop device");
            return;
        }

        glTexture = GL30.glGenTextures();
        GL30.glBindTexture(GL_TEXTURE_2D, glTexture);

        interopObject = wglDXRegisterObjectNV(interopDevice, interopTexturePtr,
                glTexture, GL_TEXTURE_2D, WGL_ACCESS_WRITE_DISCARD_NV);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer objects = stack.mallocPointer(1);
            objects.put(0, interopObject);
            wglDXLockObjectsNV(interopDevice, objects);
        }

        HDRMod.LOGGER.info("DX11 interop device created: glTexture={}", glTexture);
    }

    public static void destroyDxDevice(long hwnd) {
        if (interopObject != 0 && interopDevice != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer objects = stack.mallocPointer(1);
                objects.put(0, interopObject);
                wglDXUnlockObjectsNV(interopDevice, objects);
            }
            wglDXUnregisterObjectNV(interopDevice, interopObject);
            interopObject = 0;
        }

        if (glTexture != 0) {
            GL30.glDeleteTextures(glTexture);
            glTexture = 0;
        }

        if (interopDevice != 0) {
            wglDXCloseDeviceNV(interopDevice);
            interopDevice = 0;
        }

        if (interopShimContext != 0) {
            DX11InteropShim.nDestroy(interopShimContext);
            interopShimContext = 0;
        }

        interopTexturePtr = 0;
        interopSharedHandle = 0;
        dxDevice = 0;

        currentTexture = 0;
        currentWidth = 0;
        currentHeight = 0;
    }

    /**
     * Presents the DXGI swapchain. The caller must ensure the GL context
     * is current on the calling thread and that the interop object was
     * locked (registered and in GL-access mode) before the call.
     *
     * @param interval 0 for unsynced/tearing, 1 for vsync
     */
    public static void presentDxSwapChain(int interval) {
        if (interopShimContext == 0) return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer objects = stack.mallocPointer(1);
            objects.put(0, interopObject);
            wglDXUnlockObjectsNV(interopDevice, objects);
        }

        DX11InteropShim.nPresent(interopShimContext, interval);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer objects = stack.mallocPointer(1);
            objects.put(0, interopObject);
            wglDXLockObjectsNV(interopDevice, objects);
        }
    }

    /**
     * Resizes the DXGI swapchain and re-establishes the WGL interop binding.
     * Must be called after the window size changes. The caller must ensure
     * the GL context is current on the calling thread.
     *
     * @param width  new width (must be >= 1)
     * @param height new height (must be >= 1)
     */
    public static void resizeDxSwapChain(int width, int height) {
        if (interopShimContext == 0) return;

        if (interopObject != 0 && interopDevice != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer objects = stack.mallocPointer(1);
                objects.put(0, interopObject);
                wglDXUnlockObjectsNV(interopDevice, objects);
            }
            wglDXUnregisterObjectNV(interopDevice, interopObject);
            interopObject = 0;
        }

        if (glTexture != 0) {
            GL30.glDeleteTextures(glTexture);
            glTexture = 0;
        }

        if (!DX11InteropShim.nResize(interopShimContext, width, height)) {
            HDRMod.LOGGER.error("Failed to resize DX11 swapchain to {}x{}", width, height);
            return;
        }

        interopSharedHandle = DX11InteropShim.nGetSharedTextureHandle(interopShimContext);
        interopTexturePtr = DX11InteropShim.nGetInteropTexture(interopShimContext);

        glTexture = GL30.glGenTextures();
        GL30.glBindTexture(GL_TEXTURE_2D, glTexture);

        interopObject = wglDXRegisterObjectNV(interopDevice, interopTexturePtr,
                glTexture, GL_TEXTURE_2D, WGL_ACCESS_WRITE_DISCARD_NV);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer objects = stack.mallocPointer(1);
            objects.put(0, interopObject);
            wglDXLockObjectsNV(interopDevice, objects);
        }

        currentTexture = 0;
        currentWidth = 0;
        currentHeight = 0;
    }
}