package xyz.rrtt217.HDRMod.compat.flashback;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;

import java.lang.reflect.Proxy;

/**
 * Registers this mod as the colour transform behind Flashback's HDR export hook.
 *
 * <p>Flashback looks up the hook by name, so this stays a plain reflection call: with an unpatched
 * Flashback (or without Flashback at all) nothing happens and the export window keeps showing the
 * normal SDR options.
 *
 * <p>When the hook is present, Flashback asks for the exported frame rendered through this mod's
 * colour transform ({@link Enums.Primaries#BT2020} + {@link Enums.TransferFunction#ST2084_PQ}) and reads the
 * resulting texture back as 16-bit normalised RGBA. The rows are flipped and the buffer is fed to
 * the encoder as RGBA64 by Flashback itself.
 */
public class FlashbackHdrBridge {

    private static final String BRIDGE_CLASS = "com.moulberry.flashback.exporting.HdrExportBridge";
    private static final String TRANSFORM_CLASS = BRIDGE_CLASS + "$ColorTransform";

    private static ColorTransformRenderer renderer;
    private static RenderTarget rendererSource;
    private static boolean registered;

    public static void tryRegister() {
        if (!Platform.isModLoaded("flashback")) {
            return;
        }

        try {
            Class<?> bridge = Class.forName(BRIDGE_CLASS);
            Class<?> colorTransform = Class.forName(TRANSFORM_CLASS);
            Object proxy = Proxy.newProxyInstance(FlashbackHdrBridge.class.getClassLoader(), new Class<?>[]{colorTransform},
                    (instance, method, args) -> method.getName().equals("transform")
                            ? transform((RenderTarget) args[0], (Integer) args[1], (Integer) args[2])
                            : null);
            bridge.getMethod("register", colorTransform).invoke(null, proxy);
            registered = true;
            HDRMod.LOGGER.info("HDR export bridge registered with Flashback");
        } catch (Throwable t) {
            HDRMod.LOGGER.info("Flashback offers no HDR export bridge ({}), HDR export stays unavailable", t.toString());
        }
    }

    public static boolean isRegistered() {
        return registered;
    }

    private static GpuTexture transform(RenderTarget source, int width, int height) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if (renderer == null || rendererSource != source) {
            if (renderer != null) {
                renderer.close();
            }
            renderer = new ColorTransformRenderer(source, "Flashback");
            rendererSource = source;
        }
        // Exposure follows the replay brightness setting, the same value the replay UI is drawn with.
        renderer.updateColorTransformUniforms(config.replayUIBrightness, 0, Enums.Primaries.BT2020, Enums.TransferFunction.ST2084_PQ);
        renderer.render();
        return renderer.getDstTexture();
    }
}
