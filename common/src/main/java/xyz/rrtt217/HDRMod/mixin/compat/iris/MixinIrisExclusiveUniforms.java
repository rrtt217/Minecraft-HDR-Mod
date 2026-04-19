package xyz.rrtt217.HDRMod.mixin.compat.iris;

import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.IrisExclusiveUniforms;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.config.HDRModConfig;


@Mixin(IrisExclusiveUniforms.class)
public class MixinIrisExclusiveUniforms {
    @Inject(method = "addIrisExclusiveUniforms", at = @At("RETURN"))
    private static void addHDRModExclusiveUniforms(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, CallbackInfo ci) {
        var handle = Minecraft.getInstance().getWindow().handle();
        HDRMod.LOGGER.info("GLFW Reported Min: {}", GLFWColorManagementUtils.glfwGetWindowMinLuminance(handle));
        HDRMod.LOGGER.info("GLFW Reported Peak: {}", GLFWColorManagementUtils.glfwGetWindowMaxLuminance(handle));
        HDRMod.LOGGER.info("GLFW Reported Paper: {}", GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(handle));
        // Add uniforms. Almost no performance lost at least on Linux for calling GLFW functions every tick.
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        uniforms.uniform1f(
                UniformUpdateFrequency.PER_TICK,"HdrGameMinimumBrightness",
                () -> {
                    if(HDRMod.isReplayRendering && config.enableReplayHDRVideoExport && config.replayGameMinimumBrightness > 0)
                        return config.replayGameMinimumBrightness;
                    return config.customGameMinimumBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowMinLuminance(handle) : config.customGameMinimumBrightness;
                }
        );
        uniforms.uniform1f(
                UniformUpdateFrequency.PER_TICK,"HdrGamePeakBrightness",
                () ->{
                    if(HDRMod.isReplayRendering && config.enableReplayHDRVideoExport && config.replayGamePeakBrightness > 0)
                        return config.replayGamePeakBrightness;
                    return config.customGamePeakBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowMaxLuminance(handle) : config.customGamePeakBrightness;
                }
        );
        uniforms.uniform1f(
                UniformUpdateFrequency.PER_TICK,"HdrGamePaperWhiteBrightness",
                () -> {
                    if(HDRMod.isReplayRendering && config.enableReplayHDRVideoExport && config.replayGamePaperWhiteBrightness > 0)
                        return config.replayGamePaperWhiteBrightness;
                    return config.customGamePaperWhiteBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(handle) : config.customGamePaperWhiteBrightness;
                }
        );
        uniforms.uniform1f(
                UniformUpdateFrequency.PER_TICK,"HdrUIBrightness",
                () -> {
                    if(HDRMod.isReplayRendering && config.enableReplayHDRVideoExport && config.replayUIBrightness > 0)
                        return config.replayUIBrightness;
                    return config.uiBrightness < 0 ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(handle) : config.uiBrightness;
                }
        );
    }
}
