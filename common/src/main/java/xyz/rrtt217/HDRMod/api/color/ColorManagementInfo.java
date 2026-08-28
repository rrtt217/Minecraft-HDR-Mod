package xyz.rrtt217.HDRMod.api.color;

/**
 * Provides access to the color management information of the display.
 *
 * <p>Brightness values are reported in nits (candela per square metre). The
 * {@code getWindow*...} methods return the raw values of the underlying display
 * / window, while the {@code getCurrent*...} methods return the effective value
 * actually used by the mod, taking the user's config overrides into account.</p>
 *
 * <p>Every method takes the GLFW window handle of the active display window.</p>
 */
public interface ColorManagementInfo {
    /**
     * Returns the number of bits per color channel of the framebuffer.
     *
     * @param handle the GLFW window handle of the current display window
     * @return the bits per channel, e.g. 8, 10 or 16
     */
    int getBitsPerChannel(long handle);

    /**
     * Returns the SDR white level of the window as reported by the display.
     *
     * @param handle the GLFW window handle
     * @return the SDR white level in nits
     */
    float getWindowSdrWhiteLevel(long handle);

    /**
     * Returns the minimum luminance the window/display is capable of.
     *
     * @param handle the GLFW window handle
     * @return the minimum luminance in nits
     */
    float getWindowMinLuminance(long handle);

    /**
     * Returns the maximum (peak) luminance the window/display is capable of.
     *
     * @param handle the GLFW window handle
     * @return the maximum luminance in nits
     */
    float getWindowMaxLuminance(long handle);

    /**
     * Returns the color primaries currently in use by the window.
     *
     * @param handle the GLFW window handle
     * @return the {@link Enums.Primaries} of the window
     */
    Enums.Primaries getWindowPrimaries(long handle);

    /**
     * Returns the transfer function currently in use by the window.
     *
     * @param handle the GLFW window handle
     * @return the {@link Enums.TransferFunction} of the window
     */
    Enums.TransferFunction getWindowTransferFunction(long handle);

    /**
     * Returns the effective game "paper white" brightness.
     *
     * <p>This is the scene-referred white point for the game content. If the user
     * set a custom value it is returned, otherwise the window's SDR white level
     * ({@link #getWindowSdrWhiteLevel(long)}) is used, falling back to a default
     * of 203 nits when unavailable.</p>
     *
     * @param handle the GLFW window handle
     * @return the paper white brightness in nits
     */
    float getCurrentGamePaperWhiteBrightness(long handle);

    /**
     * Returns the effective UI brightness currently in use.
     *
     * <p>Delegates to {@link #getCurrentHudUIBrightness(long)} when an
     * HDR-compatible shaderpack is active and an in-game GUI is open, otherwise
     * to {@link #getCurrentNonHudUIBrightness(long)}.</p>
     *
     * @param handle the GLFW window handle
     * @return the UI brightness in nits
     */
    float getCurrentUIBrightness(long handle);

    /**
     * Returns the effective UI brightness used outside of HUD rendering.
     *
     * @param handle the GLFW window handle
     * @return the non-HUD UI brightness in nits
     */
    float getCurrentNonHudUIBrightness(long handle);

    /**
     * Returns the effective brightness used for the HUD while a shaderpack is active.
     *
     * @param handle the GLFW window handle
     * @return the HUD UI brightness in nits
     */
    float getCurrentHudUIBrightness(long handle);

    /**
     * Returns the effective minimum scene luminance for the game content.
     *
     * @param handle the GLFW window handle
     * @return the minimum scene brightness in nits
     */
    float getCurrentGameMinimumBrightness(long handle);

    /**
     * Returns the effective peak scene luminance for the game content.
     *
     * @param handle the GLFW window handle
     * @return the peak scene brightness in nits
     */
    float getCurrentGamePeakBrightness(long handle);

    /**
     * Returns the effective EOTF (electro-optical transfer function) emulation value.
     *
     * @param handle the GLFW window handle
     * @return the EOTF emulation value
     */
    float getCurrentEotfEmulate(long handle);

    /**
     * Returns the effective color primaries used for rendering.
     *
     * <p>Returns the window's primaries when auto-set is enabled, otherwise the
     * user-configured custom primaries.</p>
     *
     * @param handle the GLFW window handle
     * @return the current {@link Enums.Primaries}
     */
    Enums.Primaries getCurrentPrimaries(long handle);

    /**
     * Returns the effective transfer function used for rendering.
     *
     * <p>Returns the window's transfer function when auto-set is enabled,
     * otherwise the user-configured custom transfer function.</p>
     *
     * @param handle the GLFW window handle
     * @return the current {@link Enums.TransferFunction}
     */
    Enums.TransferFunction getCurrentTransferFunction(long handle);
}
