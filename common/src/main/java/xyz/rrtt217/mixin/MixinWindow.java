package xyz.rrtt217.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.shaders.UniformType;
import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.helpers.StringPair;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.core.CommonFloatUBO;
import xyz.rrtt217.util.Enums;
import xyz.rrtt217.util.GLFWColorManagement;
import xyz.rrtt217.HDRMod;
import xyz.rrtt217.config.HDRModConfig;

import static xyz.rrtt217.HDRMod.UiLuminanceUBO;

@Mixin(value = Window.class, priority = 1010)
    public abstract class MixinWindow {
    @Shadow
    public abstract long handle();

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private long handle;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = At.Shift.AFTER))
        private void hdr_mod$16BitWindowHint(WindowEventHandler arg, ScreenManager arg2, DisplayData arg3, String string, String string2, CallbackInfo ci) {
            int platform = GLFW.glfwGetPlatform();
            HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
            if(platform != GLFW.GLFW_PLATFORM_X11 && config.enableHDR && HDRMod.hasglfwLib) {
                // For 16 bits per channal.
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 16);
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 16);
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 16);
                // For float buffer. Note: Because Intel on Windows do not support float buffer (WGL_TYPE_RGBA_FLOAT_ARB), Intel users can't use this mod natively.
                GLFW.glfwWindowHint(0x00021011,GLFW.GLFW_TRUE);
            }
        }
        @Inject(method = "<init>", at = @At("RETURN"))
        private void hdr_mod$setupWindowData(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci)
        {
            HDRMod.WindowPrimaries = Enums.Primaries.fromId(GLFWColorManagement.glfwGetWindowPrimaries(this.handle()));
            HDRMod.WindowTransferFunction = Enums.TransferFunction.fromId(GLFWColorManagement.glfwGetWindowTransfer(this.handle()));
            HDRMod.LOGGER.info("Get {} bit buffer window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ",
                GLFW.glfwGetWindowAttrib(this.handle(),GLFW.GLFW_RED_BITS), GLFWColorManagement.glfwGetWindowSdrWhiteLevel(this.handle()), GLFWColorManagement.glfwGetWindowMaxLuminance(this.handle()) ,GLFWColorManagement.glfwGetWindowMinLuminance(this.handle()),HDRMod.WindowPrimaries,HDRMod.WindowTransferFunction
            );
            HDRMod.LOGGER.info("SDR white level and luminances logged here may not be accurate at this time for Linux users.");

            // Update BeforeBlit. Also add UIBrightness UBO here.
            RenderPipeline.Builder builder = HDRMod.renderPipelineBuilder.withShaderDefine("CURRENT_PRIMARIES",HDRMod.WindowPrimaries.getId()).withShaderDefine("CURRRENT_TRANSFER_FUNCTION",HDRMod.WindowTransferFunction.getId());
            for(Enums.Primaries p : Enums.Primaries.values()) {
                builder = builder.withShaderDefine("PRIMARIES_"+p.toString(), p.getId());
            }
            for(Enums.TransferFunction tf : Enums.TransferFunction.values()) {
                builder = builder.withShaderDefine("TRANSFER_FUNCTION_"+tf.toString(), tf.getId());
            }
            // builder.withUniform("UiLuminance", UniformType.UNIFORM_BUFFER);

            HDRMod.BEFORE_BLIT = builder.build();
        }
    }
