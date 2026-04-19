package xyz.rrtt217.HDRMod.util.ime;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.NativeType;

import java.nio.IntBuffer;

import static org.lwjgl.system.APIUtil.apiGetFunctionAddress;
import static org.lwjgl.system.APIUtil.apiGetFunctionAddressOptional;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryUtil.memAddressSafe;

public class GLFWIMEUtils {
    /** Contains the function pointers loaded from {@code GLFW.getLibrary()}. */
    public static final class Functions {

        private Functions() {}

        /** Function address. */
        public static final long
                SetPreeditCallback            = apiGetFunctionAddressOptional(GLFW.getLibrary(), "glfwSetPreeditCallback"),
                SetIMEStatusCallback          = apiGetFunctionAddressOptional(GLFW.getLibrary(), "glfwSetIMEStatusCallback"),
                SetPreeditCandidateCallback   = apiGetFunctionAddressOptional(GLFW.getLibrary(), "glfwSetPreeditCandidateCallback"),
                GetPreeditCursorRectangle     = apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetPreeditCursorRectangle"),
                SetPreeditCursorRectangle     = apiGetFunctionAddress(GLFW.getLibrary(), "glfwSetPreeditCursorRectangle");

    }

    protected GLFWIMEUtils() {
        throw new UnsupportedOperationException();
    }

    // --- [ glfwGetPreeditCursorRectangle ] ---

    /** Unsafe version of: {@link #glfwGetPreeditCursorRectangle GetPreeditCursorRectangle} */
    public static void nglfwGetPreeditCursorRectangle(long window, long x, long y, long w, long h) {
        long __functionAddress = Functions.GetPreeditCursorRectangle;
        if (CHECKS) {
            check(window);
        }
        invokePPPPPV(window, x, y, w, h, __functionAddress);
    }
    /**
     * Retrieves the area of the preedit text cursor.
     *
     * <p>This area is used to decide the position of the candidate window. The cursor position is relative to the window.</p>
     *
     * <p>This function may only be called from the main thread.</p>
     *
     * @param window the window to set the preedit text cursor for
     * @param x      the preedit text cursor x position (relative position from window coordinates)
     * @param y      the preedit text cursor y position (relative position from window coordinates)
     * @param w      the preedit text cursor width
     * @param h      the preedit text cursor height
     *
     * @since version 3.X
     */
    public static void glfwGetPreeditCursorRectangle(@NativeType("GLFWwindow *") long window, @NativeType("int *") @Nullable IntBuffer x, @NativeType("int *") @Nullable IntBuffer y, @NativeType("int *") @Nullable IntBuffer w, @NativeType("int *") @Nullable IntBuffer h) {
        if (CHECKS) {
            checkSafe(x, 1);
            checkSafe(y, 1);
            checkSafe(w, 1);
            checkSafe(h, 1);
        }
        nglfwGetPreeditCursorRectangle(window, memAddressSafe(x), memAddressSafe(y), memAddressSafe(w), memAddressSafe(h));
    }

    // --- [ glfwSetPreeditCursorRectangle ] ---

    /**
     * Sets the area of the preedit text cursor.
     *
     * <p>This area is used to decide the position of the candidate window. The cursor position is relative to the window.</p>
     *
     * <p>This function may only be called from the main thread.</p>
     *
     * @param window the window to set the text cursor for
     * @param x      the preedit text cursor x position (relative position from window coordinates)
     * @param y      the preedit text cursor y position (relative position from window coordinates)
     * @param w      the preedit text cursor width
     * @param h      the preedit text cursor height
     *
     * @since version 3.X
     */
    public static void glfwSetPreeditCursorRectangle(@NativeType("GLFWwindow *") long window, int x, int y, int w, int h) {
        long __functionAddress = Functions.SetPreeditCursorRectangle;
        if (CHECKS) {
            check(window);
        }
        invokePV(window, x, y, w, h, __functionAddress);
    }

    // --- [ glfwSetPreeditCallback ] ---

    /** {@code GLFWpreeditfun glfwSetPreeditCallback(GLFWwindow * window, GLFWpreeditfun cbfun)} */
    public static long nglfwSetPreeditCallback(long window, long cbfun) {
        long __functionAddress = Functions.SetPreeditCallback;
        if (CHECKS) {
            check(__functionAddress);
            check(window);
        }
        return invokePPP(window, cbfun, __functionAddress);
    }

    /** {@code GLFWpreeditfun glfwSetPreeditCallback(GLFWwindow * window, GLFWpreeditfun cbfun)} */
    @NativeType("GLFWpreeditfun")
    public static @Nullable GLFWPreeditCallback glfwSetPreeditCallback(@NativeType("GLFWwindow *") long window, @NativeType("GLFWpreeditfun") @Nullable GLFWPreeditCallbackI cbfun) {
        return GLFWPreeditCallback.createSafe(nglfwSetPreeditCallback(window, memAddressSafe(cbfun)));
    }

    // --- [ glfwSetIMEStatusCallback ] ---

    /** {@code GLFWimestatusfun glfwSetIMEStatusCallback(GLFWwindow * window, GLFWimestatusfun cbfun)} */
    public static long nglfwSetIMEStatusCallback(long window, long cbfun) {
        long __functionAddress = Functions.SetIMEStatusCallback;
        if (CHECKS) {
            check(__functionAddress);
            check(window);
        }
        return invokePPP(window, cbfun, __functionAddress);
    }
    /** {@code GLFWimestatusfun glfwSetIMEStatusCallback(GLFWwindow * window, GLFWimestatusfun cbfun)} */
    @NativeType("GLFWimestatusfun")
    public static @Nullable GLFWIMEStatusCallback glfwSetIMEStatusCallback(@NativeType("GLFWwindow *") long window, @NativeType("GLFWimestatusfun") @Nullable GLFWIMEStatusCallbackI cbfun) {
        return GLFWIMEStatusCallback.createSafe(nglfwSetIMEStatusCallback(window, memAddressSafe(cbfun)));
    }

    // --- [ glfwSetPreeditCandidateCallback ] ---

    /** {@code GLFWpreeditcandidatefun glfwSetPreeditCandidateCallback(GLFWwindow * window, GLFWpreeditcandidatefun cbfun)} */
    public static long nglfwSetPreeditCandidateCallback(long window, long cbfun) {
        long __functionAddress = Functions.SetPreeditCandidateCallback;
        if (CHECKS) {
            check(__functionAddress);
            check(window);
        }
        return invokePPP(window, cbfun, __functionAddress);
    }

    /** {@code GLFWpreeditcandidatefun glfwSetPreeditCandidateCallback(GLFWwindow * window, GLFWpreeditcandidatefun cbfun)} */
    @NativeType("GLFWpreeditcandidatefun")
    public static @Nullable GLFWPreeditCandidateCallback glfwSetPreeditCandidateCallback(@NativeType("GLFWwindow *") long window, @NativeType("GLFWpreeditcandidatefun") @Nullable GLFWPreeditCandidateCallbackI cbfun) {
        return GLFWPreeditCandidateCallback.createSafe(nglfwSetPreeditCandidateCallback(window, memAddressSafe(cbfun)));
    }
}
