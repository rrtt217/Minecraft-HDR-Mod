package xyz.rrtt217.HDRMod.util;

import me.shedaniel.autoconfig.AutoConfig;
import org.lwjgl.glfw.GLFW;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

public class ColorManagementInfoProvider {
    HDRModConfig config;
    int bitsPerChannel = 0;
    public ColorManagementInfoProvider(HDRModConfig config) {
        this.config = config;
    }
    public ColorManagementInfoProvider() {
        this.config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
    }

    public void updateConfig() {
        this.config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
    }

    public int getBitsPerChannel(long handle) {
        return bitsPerChannel > 0 ? bitsPerChannel : GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_RED_BITS);
    }

    public void setBitsPerChannel(int bpc) {
        this.bitsPerChannel = bpc;
    }

    public float getWindowSdrWhiteLevel(long handle) {
        return GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(handle);
    }
    public float getWindowMinLuminance(long handle) {
        return GLFWColorManagementUtils.glfwGetWindowMinLuminance(handle);
    }
    public float getWindowMaxLuminance(long handle) {
        return GLFWColorManagementUtils.glfwGetWindowMaxLuminance(handle);
    }
    public Enums.Primaries getWindowPrimaries(long handle) {
        return Enums.Primaries.fromId(GLFWColorManagementUtils.glfwGetWindowPrimaries(handle));
    }
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        return Enums.TransferFunction.fromId(GLFWColorManagementUtils.glfwGetWindowTransfer(handle));
    }
    public float getCurrentGamePaperWhiteBrightness(long handle) {
        if(HDRMod.isReplayRendering) return config.replayGamePaperWhiteBrightness;
        float customValue = config.customGamePaperWhiteBrightness;
        float queryValue = getWindowSdrWhiteLevel(handle);
        if(queryValue <= 0) queryValue = 203.0F; // Default paper white.
        return customValue < 0 ? queryValue : customValue;
    }
    public float getCurrentUIBrightness(long handle) {
        if(HDRMod.isReplayRendering) return config.replayUIBrightness;
        float customValue = config.uiBrightness;
        float queryValue = getWindowSdrWhiteLevel(handle);
        if(queryValue <= 0) queryValue = 203.0F; // Default paper white.
        return customValue < 0 ? queryValue : customValue;
    }
    public float getCurrentGameMinimumBrightness(long handle) {
        if(HDRMod.isReplayRendering) return config.replayGameMinimumBrightness;
        float customValue = config.customGameMinimumBrightness;
        float queryValue = getWindowMinLuminance(handle);
        if(queryValue <= 0) queryValue = 0.0F; // Default minimum.
        return customValue < 0 ? queryValue : customValue;
    }
    public float getCurrentGamePeakBrightness(long handle) {
        if(HDRMod.isReplayRendering) return config.replayGamePeakBrightness;
        float customValue = config.customGamePeakBrightness;
        float queryValue = getWindowMaxLuminance(handle);
        if(queryValue <= 0) queryValue = 1000.0F; // Default maximum.
        return customValue < 0 ? queryValue : customValue;
    }

    public float getCurrentEotfEmulate(long handle) {
        if(HDRMod.isReplayRendering) return 0.0F;
        float customValue = config.customEotfEmulate;
        float queryValue = getWindowSdrWhiteLevel(handle);
        if(queryValue <= 0) queryValue = 0.0F; // Default maximum.
        return customValue < 0 ? queryValue : customValue;
    }
    public Enums.Primaries getCurrentPrimaries(long handle) {
        return config.autoSetPrimaries ? getWindowPrimaries(handle) : config.customPrimaries;
    }
    public Enums.TransferFunction getCurrentTransferFunction(long handle) {
        return config.autoSetTransferFunction ? getWindowTransferFunction(handle) : config.customTransferFunction;
    }
}
