package xyz.rrtt217.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Window.class, priority = 1010)
    public class MixinWindow {
        @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = At.Shift.AFTER))
        private void hdr_mod$enableHDR(WindowEventHandler arg, ScreenManager arg2, DisplayData arg3, String string, String string2, CallbackInfo ci) {
            GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS,16);
            GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS,16);
            GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS,16);
            GLFW.glfwWindowHint(0x00021011,GLFW.GLFW_TRUE);
        }
    }
