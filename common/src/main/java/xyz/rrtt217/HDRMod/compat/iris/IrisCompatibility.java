package xyz.rrtt217.HDRMod.compat.iris;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.InteractionResult;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

public class IrisCompatibility {
    private static Boolean previousEnableHDR;
    public static InteractionResult onConfigSave(ConfigHolder<HDRModConfig> configHolder, HDRModConfig config) {
        previousEnableHDR = config.enableHDR;
        return InteractionResult.SUCCESS;
    }
}
