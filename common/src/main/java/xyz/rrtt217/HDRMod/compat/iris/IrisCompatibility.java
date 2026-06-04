package xyz.rrtt217.HDRMod.compat.iris;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.InteractionResult;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

@SuppressWarnings("unused")
public class IrisCompatibility {
    public static Boolean previousEnableHDR;
    public static InteractionResult onConfigSave(ConfigHolder<HDRModConfig> configHolder, HDRModConfig config) {
        previousEnableHDR = config.enableHDR;
        // Otherwise other listener won't be triggered.
        return InteractionResult.PASS;
    }
}
