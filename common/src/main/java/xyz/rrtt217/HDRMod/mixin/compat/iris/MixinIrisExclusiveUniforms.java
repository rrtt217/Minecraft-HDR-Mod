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
import xyz.rrtt217.HDRMod.util.GLFWColorManagement;
import xyz.rrtt217.HDRMod.config.HDRModConfig;


@Mixin(IrisExclusiveUniforms.class)
public class MixinIrisExclusiveUniforms {
    @Inject(method = "addIrisExclusiveUniforms", at = @At("RETURN"))
    private static void addHDRModExclusiveUniforms(UniformHolder uniforms, CallbackInfo ci) {
        var handle = Minecraft.getInstance().getWindow().getWindow();
        HDRMod.LOGGER.info("GLFW Reported Min: {}", GLFWColorManagement.glfwGetWindowMinLuminance(handle));
        HDRMod.LOGGER.info("GLFW Reported Peak: {}", GLFWColorManagement.glfwGetWindowMaxLuminance(handle));
        HDRMod.LOGGER.info("GLFW Reported Paper: {}", GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle));
        // Add uniforms. Almost no performance lost at least on Linux for calling GLFW functions every tick.
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrGameMinimumBrightness",() -> config.customGameMinimumBrightness < 0 ? GLFWColorManagement.glfwGetWindowMinLuminance(handle) : config.customGameMinimumBrightness );
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrGamePeakBrightness",() -> config.customGamePeakBrightness < 0 ? GLFWColorManagement.glfwGetWindowMaxLuminance(handle) : config.customGamePeakBrightness );
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrGamePaperWhiteBrightness", () -> config.customGamePaperWhiteBrightness < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle) : config.customGamePaperWhiteBrightness);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrUIBrightness", () -> config.uiBrightness < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle) : config.uiBrightness);
    }
}
