package xyz.rrtt217.HDRMod.api;

import org.jetbrains.annotations.Nullable;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.api.color.ColorManagementInfo;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Public API of the Minecraft HDR Mod.
 *
 * <p>This is the single entry point for other mods / shaderpacks that want to
 * integrate with or query the state of the HDR pipeline. Retrieve an instance
 * via {@link #getInstance()}. The returned instance is non-null once the mod
 * has finished initialising on the client.</p>
 *
 * <p>All methods are safe to call from any thread unless documented otherwise.</p>
 */
public interface HDRModApi {
    /**
     * Returns the current {@link HDRModApi} instance, or {@code null} if the mod
     * is not yet initialised (e.g. during early pre-init).
     *
     * @return the active API instance, or {@code null} if unavailable
     */
    static @Nullable HDRModApi getInstance() {
        return HDRMod.apiImpl;
    }

    /**
     * Returns the version string of the installed HDR mod.
     *
     * @return the mod version, e.g. "1.0.0"
     */
    String getModVersion();

    /**
     * Checks whether HDR rendering is currently enabled in the mod's config.
     *
     * <p>Note that this reflects the config setting, not necessarily that the
     * display hardware is actually running in HDR mode at this moment.</p>
     *
     * @return {@code true} if HDR is enabled in the config
     */
    boolean isHDREnabled();

    /**
     * Registers a listener that is notified whenever the HDR on/off state changes.
     *
     * <p>The listener is invoked with {@code true} when HDR becomes enabled and
     * {@code false} when it becomes disabled. Listeners are stored for the
     * lifetime of the client and are never removed automatically.</p>
     *
     * @param consumer the listener to invoke on HDR state changes; must not be {@code null}
     */
    void addHDRStateChangeListener(Consumer<Boolean> consumer);

    /**
     * Returns the provider for querying the display's color management
     * information (luminance, primaries, transfer function, etc.).
     *
     * @return the current {@link ColorManagementInfo} provider
     */
    ColorManagementInfo getColorManagementInfo();

    /**
     * Registers a supplier that reports whether an HDR-compatible shaderpack is
     * currently in use.
     *
     * <p>This is used by the mod to decide whether UI color correction and other
     * HDR-specific behaviour should be applied. Multiple suppliers may be
     * registered; the mod considers a compatible shaderpack to be in use if any
     * of the registered suppliers returns {@code true}.</p>
     *
     * @param supplier a supplier returning {@code true} when an HDR-compatible
     *                 shaderpack is active; must not be {@code null}
     */
    void addHDRCompatibleShaderpackStateSupplier(Supplier<Boolean> supplier);
}
