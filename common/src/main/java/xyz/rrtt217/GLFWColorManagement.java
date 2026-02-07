package xyz.rrtt217;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;

public class GLFWColorManagement {
    /** Contains the function pointers loaded from {@code GLFW.getLibrary()}. */
    public static final class Functions {

        private Functions() {}

        /** Function address. */
        public static final long
                GetWindowSdrWhiteLevel = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowSdrWhiteLevel"),
                GetWindowMinLuminance = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowMinLuminance"),
                GetWindowMaxLuminance = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowMaxLuminance"),
                GetWindowPrimaries = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowPrimaries"),
                GetWindowTransfer = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetWindowTransfer");

    }

    protected GLFWColorManagement() {
        throw new UnsupportedOperationException();
    }
    // --- [ glfwGetWindowSdrWhiteLevel ] ---

    /** {@code float glfwGetWindowSdrWhiteLevel(GLFWwindow *window)} */
    @NativeType("float")
    public static float glfwGetWindowSdrWhiteLevel(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetWindowSdrWhiteLevel;
        if (CHECKS) {
            check(window);
        }
        return invokePF(window, __functionAddress);
    }

    // --- [ glfwGetWindowMinLuminance ] ---

    /** {@code float glfwGetWindowMinLuminance(GLFWwindow *window)} */
    @NativeType("float")
    public static float glfwGetWindowMinLuminance(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetWindowMinLuminance;
        if (CHECKS) {
            check(window);
        }
        return invokePF(window, __functionAddress);
    }

    // --- [ glfwGetWindowMaxLuminance ] ---

    /** {@code float glfwGetWindowMaxLuminance(GLFWwindow *window)} */
    @NativeType("float")
    public static float glfwGetWindowMaxLuminance(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetWindowMaxLuminance;
        if (CHECKS) {
            check(window);
        }
        return invokePF(window, __functionAddress);
    }

    // --- [ glfwGetWindowPrimaries ] ---

    /** {@code uint32_t glfwGetWindowPrimaries(GLFWwindow *window)} */
    @NativeType("uint32_t")
    public static int glfwGetWindowPrimaries(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetWindowPrimaries;
        if (CHECKS) {
            check(window);
        }
        return invokePI(window, __functionAddress);
    }

    // --- [ glfwGetWindowTransfer ] ---

    /** {@code uint32_t glfwGetWindowTransfer(GLFWwindow *window)} */
    @NativeType("uint32_t")
    public static int glfwGetWindowTransfer(@NativeType("GLFWwindow *") long window) {
        long __functionAddress = Functions.GetWindowTransfer;
        if (CHECKS) {
            check(window);
        }
        return invokePI(window, __functionAddress);
    }
}
