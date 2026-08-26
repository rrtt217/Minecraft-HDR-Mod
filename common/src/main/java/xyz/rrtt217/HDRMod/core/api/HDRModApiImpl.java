package xyz.rrtt217.HDRMod.core.api;

import me.shedaniel.autoconfig.ConfigHolder;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.world.InteractionResult;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.api.HDRModApi;
import xyz.rrtt217.HDRMod.api.color.ColorManagementInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.platform.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HDRModApiImpl implements HDRModApi {
    private final List<Consumer<Boolean>> hdrStateListeners = new ArrayList<>();
    private final List<Supplier<Boolean>> hdrCompatibleShaderpackStateSuppliers = new ArrayList<>();
    public Boolean previousEnableHDR;
    @Override
    public String getModVersion() {
        String version = Platform.getVersion();
        if(version.contains("-")) version = version.substring(0, version.indexOf("-"));
        return version;
    }

    @Override
    public boolean isHDREnabled() {
        return HDRMod.configHolder.getConfig().enableHDR;
    }

    @Override
    public void addHDRStateChangeListener(Consumer<Boolean> consumer) {
        hdrStateListeners.add(consumer);
    }

    @Override
    public ColorManagementInfo getColorManagementInfo() {
        return HDRMod.colorManagementInfoProvider;
    }

    @Override
    public void addHDRCompatibleShaderpackStateSupplier(Supplier<Boolean> supplier) {
        hdrCompatibleShaderpackStateSuppliers.add(supplier);
    }

    public boolean isHDRCompatibleShaderpackInUse() {
        if (hdrCompatibleShaderpackStateSuppliers.isEmpty()) {return false;}
        for (Supplier<Boolean> supplier : hdrCompatibleShaderpackStateSuppliers) {
            if (supplier.get()) {return true;}
        }
        return false;
    }

    public void callHDRStateChangeListeners(boolean hdrEnabled) {
        for (Consumer<Boolean> consumer : hdrStateListeners) {
            consumer.accept(hdrEnabled);
        }
    }

    public InteractionResult onConfigSave(ConfigHolder<HDRModConfig> configHolder, HDRModConfig config) {
        if(previousEnableHDR != null && previousEnableHDR != config.enableHDR) {
            callHDRStateChangeListeners(config.enableHDR);
        }
        previousEnableHDR = config.enableHDR;
        // Otherwise other listener won't be triggered.
        return InteractionResult.PASS;
    }
}
