package xyz.rrtt217.HDRMod.mixin.compat.ixeris;

import me.decce.ixeris.api.IxerisApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils;

import java.nio.IntBuffer;

@Mixin(value = GLFWIMEUtils.class, remap = false)
public class MixinGLFWIMEUtils {

    @Inject(method = "glfwGetPreeditCursorRectangle", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hdr_mod$getPreeditCursorRectangleOnMainThread(long window, IntBuffer x, IntBuffer y, IntBuffer w, IntBuffer h, CallbackInfo ci){
        IxerisApi api = IxerisApi.getInstance();
        if (api.isEnabled() && !api.isOnMainThreadOrInit()) {
            ci.cancel();
            api.runNowOnMainThread(() -> GLFWIMEUtils.glfwGetPreeditCursorRectangle(window, x, y, w, h));
        }
    }

    @Inject(method = "glfwSetPreeditCursorRectangle", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hdr_mod$setPreeditCursorRectangleOnMainThread(long window, int x, int y, int w, int h, CallbackInfo ci){
        IxerisApi api = IxerisApi.getInstance();
        if (api.isEnabled() && !api.isOnMainThreadOrInit()) {
            ci.cancel();
            api.runNowOnMainThread(() -> GLFWIMEUtils.glfwSetPreeditCursorRectangle(window, x, y, w, h));
        }
    }
}
