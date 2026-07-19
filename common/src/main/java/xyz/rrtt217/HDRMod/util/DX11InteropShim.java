package xyz.rrtt217.HDRMod.util;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * JNI binding for the dx11-interop-shim native library.
 * <p>
 * This class provides basic DirectX 11 device creation and DXGI swapchain
 * management. All WGL interop calls (WGL_NV_DX_interop2) must be performed
 * on the Java side using LWJGL or direct function-pointer invocation.
 *
 * <h3>Typical usage flow</h3>
 * <pre>{@code
 * // 1. Create the DX11 device + swapchain + shared interop texture
 * long ctx = DX11InteropShim.nCreate(hwnd, width, height, format, true);
 *
 * // 2. Open a WGL DX interop device using the D3D11 device pointer
 * long dxDevice = DX11InteropShim.nGetDevice(ctx);
 * long interopDevice = wglDXOpenDeviceNV(dxDevice);
 *
 * // 3. Register the shared texture with a GL texture
 * long sharedHandle = DX11InteropShim.nGetSharedTextureHandle(ctx);
 * int glTexture = glGenTextures();
 * glBindTexture(GL_TEXTURE_2D, glTexture);
 * long interopObject = wglDXRegisterObjectNV(interopDevice, sharedHandle,
 *                                             glTexture, GL_TEXTURE_2D,
 *                                             WGL_ACCESS_WRITE_DISCARD_NV);
 * wglDXLockObjectsNV(interopDevice, 1, interopObject);
 *
 * // 4. Render to glTexture (which writes to the shared DX11 texture)
 *
 * // 5. Present: unlock -> nPresent -> lock
 * wglDXUnlockObjectsNV(interopDevice, 1, interopObject);
 * DX11InteropShim.nPresent(ctx, swapInterval);
 * wglDXLockObjectsNV(interopDevice, 1, interopObject);
 *
 * // 6. On resize: unregister -> nResize -> re-register
 * wglDXUnlockObjectsNV(interopDevice, 1, interopObject);
 * wglDXUnregisterObjectNV(interopDevice, interopObject);
 * glDeleteTextures(glTexture);
 * DX11InteropShim.nResize(ctx, newWidth, newHeight);
 * sharedHandle = DX11InteropShim.nGetSharedTextureHandle(ctx);
 * glTexture = glGenTextures();
 * glBindTexture(GL_TEXTURE_2D, glTexture);
 * interopObject = wglDXRegisterObjectNV(interopDevice, sharedHandle,
 *                                        glTexture, GL_TEXTURE_2D,
 *                                        WGL_ACCESS_WRITE_DISCARD_NV);
 * wglDXLockObjectsNV(interopDevice, 1, interopObject);
 *
 * // 7. Destroy
 * wglDXUnlockObjectsNV(interopDevice, 1, interopObject);
 * wglDXUnregisterObjectNV(interopDevice, interopObject);
 * glDeleteTextures(glTexture);
 * wglDXCloseDeviceNV(interopDevice);
 * DX11InteropShim.nDestroy(ctx);
 * }</pre>
 */
public class DX11InteropShim {

    private static final Logger LOGGER = LoggerFactory.getLogger("dx11_interop_shim");
    private static final String LIB_VERSION = "1.0.0";

    /**
     * {@code true} if the native dx11-interop-shim library was successfully
     * loaded. Always {@code false} on non-Windows platforms.
     */
    public static final boolean LOADED;

    static {
        boolean loaded = false;
        if (Platform.isWindows()) {
            try {
                Map<String, String> libNames = new HashMap<>();
                libNames.put("windows", "dx11-interop-shim");
                String libPath = LibraryExtractor.extractLibraries(
                        libNames, "dx11-interop-shim", LIB_VERSION).toString();
                System.load(libPath);
                loaded = true;
                LOGGER.info("dx11-interop-shim native library loaded from {}", libPath);
            } catch (Throwable t) {
                LOGGER.warn("Failed to load dx11-interop-shim native library: {}", t.getMessage());
            }
        } else {
            LOGGER.debug("dx11-interop-shim is Windows-only, skipping load on this platform");
        }
        LOADED = loaded;
    }

    /* DXGI_FORMAT values accepted by nCreate */
    public static final int DXGI_FORMAT_R8G8B8A8_UNORM       = 28;
    public static final int DXGI_FORMAT_R10G10B10A2_UNORM    = 24;
    public static final int DXGI_FORMAT_R16G16B16A16_FLOAT   = 10;

    /* Color primaries (Wayland) */
    public static final int COLOR_PRIMARIES_BT709  = 1;
    public static final int COLOR_PRIMARIES_BT2020 = 6;

    /* Transfer functions (Wayland) */
    public static final int TRANSFER_LINEAR = 5;
    public static final int TRANSFER_SRGB   = 10;
    public static final int TRANSFER_PQ     = 11;

    private DX11InteropShim() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a D3D11 device, DXGI swapchain, shared interop texture, and the
     * internal flip-Y shader pipeline.
     *
     * @param hwnd         native Win32 window handle (HWND)
     * @param width        initial swapchain width (must be >= 1)
     * @param height       initial swapchain height (must be >= 1)
     * @param dxgiFormat   one of the {@code DXGI_FORMAT_*} constants
     * @param allowTearing whether to request DXGI_PRESENT_ALLOW_TEARING support
     * @return opaque native context handle, or {@code 0} on failure
     */
    public static native long nCreate(long hwnd, int width, int height,
                                       int dxgiFormat, boolean allowTearing);

    /**
     * Returns the raw {@code ID3D11Device*} pointer stored in the context.
     * Pass this to {@code wglDXOpenDeviceNV} on the Java side.
     *
     * @param context native context handle from {@link #nCreate}
     * @return {@code ID3D11Device*} pointer, or {@code 0}
     */
    public static native long nGetDevice(long context);

    /**
     * Returns the shared HANDLE of the interop texture.
     * Pass this to {@code wglDXRegisterObjectNV} on the Java side.
     *
     * @param context native context handle from {@link #nCreate}
     * @return shared texture HANDLE, or {@code 0}
     */
    public static native long nGetSharedTextureHandle(long context);

    /**
     * Returns the raw {@code ID3D11Texture2D*} pointer of the interop texture.
     * Pass this to {@code wglDXRegisterObjectNV} as the {@code dxResource}
     * argument.
     *
     * @param context native context handle from {@link #nCreate}
     * @return {@code ID3D11Texture2D*} pointer, or {@code 0}
     */
    public static native long nGetInteropTexture(long context);

    /**
     * Resizes the swapchain buffers and recreates the shared interop texture.
     * <p>
     * The caller must unregister / delete the old GL interop object and GL
     * texture <em>before</em> calling this method, then re-register with the
     * new shared handle returned by {@link #nGetSharedTextureHandle}.
     *
     * @param context native context handle
     * @param width   new width (must be >= 1)
     * @param height  new height (must be >= 1)
     * @return {@code true} on success
     */
    public static native boolean nResize(long context, int width, int height);

    /**
     * Renders the interop texture to the back buffer (Y-flip) and presents the
     * swapchain.
     * <p>
     * The caller must {@code wglDXUnlockObjectsNV} <em>before</em> calling
     * this method and {@code wglDXLockObjectsNV} <em>after</em>.
     *
     * @param context native context handle
     * @param interval sync interval (0 = tearing/unsynced, 1 = vsync)
     * @return {@code true} on success
     */
    public static native boolean nPresent(long context, int interval);

    /**
     * Configures the swapchain color space from Wayland-style primaries and transfer
     * function values.
     *
     * @param context        native context handle
     * @param colorPrimaries e.g. {@link #COLOR_PRIMARIES_BT709} or {@link #COLOR_PRIMARIES_BT2020}
     * @param colorTransfer  e.g. {@link #TRANSFER_LINEAR}, {@link #TRANSFER_SRGB}, or {@link #TRANSFER_PQ}
     * @return {@code true} on success
     */
    public static native boolean nSetColorSpace(long context, int colorPrimaries,
                                                 int colorTransfer);

    /**
     * Destroys the context and releases all native D3D11 / DXGI resources.
     * The caller must close the WGL interop device and release GL resources
     * <em>before</em> calling this method.
     *
     * @param context native context handle
     */
    public static native void nDestroy(long context);

    /**
     * Returns the last error message recorded by the native library.
     * After a successful {@link #nCreate} call, this returns an empty string.
     * After a failed call, it returns a human-readable description including
     * the HRESULT where applicable.
     *
     * @return the last error string, or empty string if no error occurred
     */
    public static native String nGetLastError();
}
