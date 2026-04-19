package xyz.rrtt217.HDRMod.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.NativeType;

import static org.lwjgl.system.APIUtil.apiGetFunctionAddress;
import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.JNI.*;

public class GLFWDXGIUtils {
    /** Contains the function pointers loaded from {@code GLFW.getLibrary()}. */
    public static final class Functions {

        private Functions() {}

        /** Function address. */
        public static final long
                GetWin32SwapchainImageHandle   = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWin32SwapchainImageHandle"),
                GetWindowSwapchainImageTexture = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowSwapchainImageTexture"),
                CompletePendingDXGIResize      = apiGetFunctionAddress(GLFW.getLibrary(), "glfwCompletePendingDXGIResize"),
                GetPendingDXGIResize           = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetPendingDXGIResize"),
                ReleaseCurrentContext          = apiGetFunctionAddress(GLFW.getLibrary(), "glfwReleaseCurrentContext"),
                ReacquireCurrentContext        = apiGetFunctionAddress(GLFW.getLibrary(), "glfwReacquireCurrentContext"),
                GetPendingDXGIResizeHeight     = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetPendingDXGIResizeHeight");
    }

    protected GLFWDXGIUtils() { throw new UnsupportedOperationException(); }

    /** {@code long glfwGetWin32SwapchainImageHandle(GLFWwindow *window)} */
    @NativeType("long")
    public static long glfwGetWin32SwapchainImageHandle(@NativeType("GLFWwindow *") long window) {
        if (CHECKS) check(window);
        return invokePJ(window, Functions.GetWin32SwapchainImageHandle);
    }
    // --- [ glfwGetWindowSwapchainImageTexture ] ---

    /** {@code int glfwGetWin32SwapchainImageHandle(GLFWwindow *window)} */
    @NativeType("int")
    public static int glfwGetWindowSwapchainImageTexture(@NativeType("GLFWwindow *") long window) {
        if (CHECKS) check(window);
        return invokePI(window, Functions.GetWindowSwapchainImageTexture);
    }

    public static void glfwCompletePendingDXGIResize(@NativeType("GLFWwindow *") long window) {
        if (CHECKS) check(window);
        invokePV(window, Functions.CompletePendingDXGIResize);
    }

    public static int glfwGetPendingDXGIResize(@NativeType("GLFWwindow *") long window) {
        if (CHECKS) check(window);
        return invokePI(window, Functions.GetPendingDXGIResize);
    }

    /** Calls wglMakeCurrent(NULL, NULL) on the calling thread. */
    public static void glfwReleaseCurrentContext() {
        invokeV(Functions.ReleaseCurrentContext);
    }

    public static int glfwGetPendingDXGIResizeHeight(@NativeType("GLFWwindow *") long window) {
        if (CHECKS) check(window);
        return invokePI(window, Functions.GetPendingDXGIResizeHeight);
    }

    /** Restores Minecraft's WGL context on the calling thread. */
    public static void glfwReacquireCurrentContext(@NativeType("GLFWwindow *") long window) {
        if (CHECKS) check(window);
        invokePV(window, Functions.ReacquireCurrentContext);
    }
}
