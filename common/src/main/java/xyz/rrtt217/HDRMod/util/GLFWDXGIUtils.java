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
                GetWin32SwapchainImageHandle = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWin32SwapchainImageHandle"),
                GetWindowSwapchainImageTexture = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowSwapchainImageTexture");

    }

    protected GLFWDXGIUtils() {
        throw new UnsupportedOperationException();
    }
    // --- [ glfwGetWin32SwapchainImageHandle ] ---

    /** {@code long glfwGetWin32SwapchainImageHandle(GLFWwindow *window)} */
    @NativeType("long")
    public static long glfwGetWin32SwapchainImageHandle(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = GLFWDXGIUtils.Functions.GetWin32SwapchainImageHandle;
        if (CHECKS) {
            check(window);
        }
        return invokePJ(window, __functionAddress);
    }
    // --- [ glfwGetWindowSwapchainImageTexture ] ---

    /** {@code int glfwGetWin32SwapchainImageHandle(GLFWwindow *window)} */
    @NativeType("int")
    public static int glfwGetWindowSwapchainImageTexture(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = GLFWDXGIUtils.Functions.GetWindowSwapchainImageTexture;
        if (CHECKS) {
            check(window);
        }
        return invokePI(window, __functionAddress);
    }
}
