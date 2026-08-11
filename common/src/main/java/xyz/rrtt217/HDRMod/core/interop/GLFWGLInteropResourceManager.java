package xyz.rrtt217.HDRMod.core.interop;

import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

public class GLFWGLInteropResourceManager extends GLInteropResourceManager {
    @Override
    int getNewTexture(long handle, int currentTexture) {
        return GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(handle);
    }
}