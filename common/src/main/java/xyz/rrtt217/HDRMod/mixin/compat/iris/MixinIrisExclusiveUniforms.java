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
    private static float GlwfCached_Min = 0;
    private static float GlwfCached_Peak = 0;
    private static float GlwfCached_Paper = 0;

    @Inject(method = "addIrisExclusiveUniforms", at = @At("RETURN"))
    private static void addHDRModExclusiveUniforms(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, CallbackInfo ci) {
        //GLFW
        {
            //TODO: This only runs on shaderpack compile. This will miss multimonitor switching. (but this means other GLWF must be fixed first)
            //get
            var handle = Minecraft.getInstance().getWindow().handle();
            GlwfCached_Min = GLFWColorManagement.glfwGetWindowMinLuminance(handle);
            GlwfCached_Peak = GLFWColorManagement.glfwGetWindowMaxLuminance(handle);
            GlwfCached_Paper = GLFWColorManagement.glfwGetWindowSdrWhiteLevel(handle);

            //log
            HDRMod.LOGGER.info("GLFW Reported Min: " + GlwfCached_Min);
            HDRMod.LOGGER.info("GLFW Reported Peak: " + GlwfCached_Peak);
            HDRMod.LOGGER.info("GLFW Reported Paper: " + GlwfCached_Paper);
        }

        //add uniforms
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        //TODO: even slower than UniformUpdateFrequency.PER_TICK
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrGameMinimumBrightness",() -> config.customGameMinimumBrightness < 0 ? GlwfCached_Min : config.customGameMinimumBrightness );
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrGamePeakBrightness",() -> config.customGamePeakBrightness < 0 ? GlwfCached_Peak : config.customGamePeakBrightness );
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrGamePaperWhiteBrightness", () -> config.customGamePaperWhiteBrightness < 0 ? GlwfCached_Paper : config.customGamePaperWhiteBrightness);
        uniforms.uniform1f(UniformUpdateFrequency.PER_TICK,"HdrUIBrightness", () -> config.uiBrightness < 0 ? GlwfCached_Paper : config.uiBrightness);
    }
}
