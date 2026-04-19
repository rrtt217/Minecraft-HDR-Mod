package xyz.rrtt217.HDRMod.mixin.compat.imblocker;

import io.github.reserveword.imblocker.common.gui.Point;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

import static xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils.glfwSetPreeditCursorRectangle;

@Mixin(targets = "io.github.reserveword.imblocker.common.IMManagerLinux")
public class MixinIMManagerLinux {
    @Shadow
    private static boolean state;
    // This only works on our modified GLFW, not the original LWJGL 3.4.1 version!
    @Inject(method = "setState", at = @At("HEAD"), cancellable = true, remap = false)
    private void setState(boolean on, CallbackInfo ci) {
        if (state != on) {
            GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(),0x00033007, on ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            state = on;
        }
        ci.cancel();
    }
    // This works on the original LWJGL 3.4.1 GLFW.
    @Unique
    public void updateCompositionWindowPos(Point pos) {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        FloatBuffer xscale = BufferUtils.createFloatBuffer(1);
        FloatBuffer yscale = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetWindowContentScale(handle, xscale, yscale);
        glfwSetPreeditCursorRectangle(handle, (int)(pos.x() / xscale.get()), (int)(pos.y() / yscale.get()), 0, 0);
    }
}
