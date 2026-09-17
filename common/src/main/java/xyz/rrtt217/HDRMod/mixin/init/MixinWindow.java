package xyz.rrtt217.HDRMod.mixin.init;

import com.mojang.blaze3d.platform.*;
import com.mojang.renderpearl.api.device.GpuBackend;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.api.color.Enums;
import xyz.rrtt217.HDRMod.HDRMod;


import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;


@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    public abstract long handle();

    @Shadow
    @Final
    private long handle;
    @Inject(method = "<init>(Lcom/mojang/blaze3d/platform/WindowEventHandler;Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;ZLjava/lang/String;Lcom/mojang/blaze3d/platform/MonitorManager;Lcom/mojang/renderpearl/api/device/GpuBackend;I)V", at = @At("RETURN"))
    private void hdr_mod$setupWindowData(WindowEventHandler eventHandler, DisplayData displayData, String fullscreenVideoModeString, boolean exclusiveFullscreen, String title, MonitorManager monitorManager, GpuBackend backend, int maximumSize, CallbackInfo ci) {
        int bpc = HDRMod.colorManagementInfoProvider.getBitsPerChannel(this.handle);
        float SDRWhiteLevel = HDRMod.colorManagementInfoProvider.getWindowSdrWhiteLevel(handle);
        float maxLuminance = HDRMod.colorManagementInfoProvider.getWindowMaxLuminance(handle);
        float minLuminance = HDRMod.colorManagementInfoProvider.getWindowMinLuminance(handle);
        Enums.Primaries primaries = HDRMod.colorManagementInfoProvider.getCurrentPrimaries(handle);
        Enums.TransferFunction tf = HDRMod.colorManagementInfoProvider.getWindowTransferFunction(handle);
        LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ", bpc, SDRWhiteLevel, maxLuminance, minLuminance, primaries, tf);
    }
}
