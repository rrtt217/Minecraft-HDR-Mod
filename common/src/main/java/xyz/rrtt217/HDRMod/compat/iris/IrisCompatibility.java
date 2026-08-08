package xyz.rrtt217.HDRMod.compat.iris;

import me.shedaniel.autoconfig.ConfigHolder;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.world.InteractionResult;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.Platform;

@SuppressWarnings("unused")
public class IrisCompatibility {
    public static final boolean IRIS_INSTALLED = Platform.isModLoaded("iris") || Platform.isModLoaded("oculus");
    public static Boolean previousEnableHDR;
    public static InteractionResult onConfigSave(ConfigHolder<HDRModConfig> configHolder, HDRModConfig config) {
        if(previousEnableHDR != null && previousEnableHDR != config.enableHDR && IRIS_INSTALLED) {
            if(IrisApi.getInstance().isShaderPackInUse()) {
                try {
                    Iris.reload();
                } catch (Exception ignored) {
                }
            }
        }
        previousEnableHDR = config.enableHDR;
        // Otherwise other listener won't be triggered.
        return InteractionResult.PASS;
    }
    public static boolean isShaderPackInUse(){
        if(!IRIS_INSTALLED) return false;
        return IrisApi.getInstance().isShaderPackInUse();
    }
}
