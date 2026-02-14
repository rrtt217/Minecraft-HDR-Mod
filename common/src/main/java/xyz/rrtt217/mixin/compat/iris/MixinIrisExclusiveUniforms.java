package xyz.rrtt217.mixin.compat.iris;

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
import xyz.rrtt217.util.GLFWColorManagement;
import xyz.rrtt217.config.HDRModConfig;

@Mixin(IrisExclusiveUniforms.class)
public class MixinIrisExclusiveUniforms {
    @Inject(method = "addIrisExclusiveUniforms", at = @At("RETURN"))
    private static void addHDRModExclusiveUniforms(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME,"HdrGameMinimumBrightness",() -> config.autoSetGameMinimumBrightness ? GLFWColorManagement.glfwGetWindowMinLuminance(Minecraft.getInstance().getWindow().handle()) : config.customGameMinimumBrightness );
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME,"HdrGamePeakBrightness",() -> config.autoSetGamePeakBrightness ? GLFWColorManagement.glfwGetWindowMaxLuminance(Minecraft.getInstance().getWindow().handle()) : config.customGamePeakBrightness );
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME,"HdrGamePaperWhiteBrightness", () -> config.autoSetGamePaperWhiteBrightness ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.customGamePaperWhiteBrightness);
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME,"HdrUIBrightness", () -> config.autoSetUIBrightness ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.uiBrightness);
    }
}
