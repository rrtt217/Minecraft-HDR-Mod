package xyz.rrtt217.HDRMod.mixin.compat.ixeris;

import me.decce.ixeris.api.IxerisApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.util.ime.*;
import xyz.rrtt217.HDRMod.compat.ixeris.*;

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
    @Inject(method = "glfwSetIMEStatusCallback", at = @At("HEAD"))
    private static void ixeris$glfwSetIMEStatusCallback(long window, GLFWIMEStatusCallbackI cbfun, CallbackInfoReturnable<GLFWIMEStatusCallbackI> cir) {
        var dispatcher = IMEStatusCallbackDispatcher.get(window);
        if (dispatcher.suppressChecks) {
            return;
        }
        dispatcher.update(cbfun);
    }

    @Inject(method = "nglfwSetIMEStatusCallback", at = @At("RETURN"), cancellable = true)
    private static void ixeris$nglfwSetIMEStatusCallback(long window, long cbfun, CallbackInfoReturnable<Long> cir) {
        var dispatcher = IMEStatusCallbackDispatcher.get(window);
        if (dispatcher.suppressChecks) {
            return;
        }
        if (cbfun != CommonCallbacks_334.iMEStatusCallback.address()) {
            cir.setReturnValue(dispatcher.update(cbfun));
        }
    }

    @Inject(method = "glfwSetPreeditCallback", at = @At("HEAD"))
    private static void ixeris$glfwSetPreeditCallback(long window, GLFWPreeditCallbackI cbfun, CallbackInfoReturnable<GLFWPreeditCallbackI> cir) {
        var dispatcher = PreeditCallbackDispatcher.get(window);
        if (dispatcher.suppressChecks) {
            return;
        }
        dispatcher.update(cbfun);
    }

    @Inject(method = "nglfwSetPreeditCallback", at = @At("RETURN"), cancellable = true)
    private static void ixeris$nglfwSetPreeditCallback(long window, long cbfun, CallbackInfoReturnable<Long> cir) {
        var dispatcher = PreeditCallbackDispatcher.get(window);
        if (dispatcher.suppressChecks) {
            return;
        }
        if (cbfun != CommonCallbacks_334.preeditCallback.address()) {
            cir.setReturnValue(dispatcher.update(cbfun));
        }
    }

    @Inject(method = "glfwSetPreeditCandidateCallback", at = @At("HEAD"))
    private static void ixeris$glfwSetPreeditCandidateCallback(long window, GLFWPreeditCandidateCallbackI cbfun, CallbackInfoReturnable<GLFWPreeditCandidateCallbackI> cir) {
        var dispatcher = PreeditCandidateCallbackDispatcher.get(window);
        if (dispatcher.suppressChecks) {
            return;
        }
        dispatcher.update(cbfun);
    }

    @Inject(method = "nglfwSetPreeditCandidateCallback", at = @At("RETURN"), cancellable = true)
    private static void ixeris$nglfwSetPreeditCandidateCallback(long window, long cbfun, CallbackInfoReturnable<Long> cir) {
        var dispatcher = PreeditCandidateCallbackDispatcher.get(window);
        if (dispatcher.suppressChecks) {
            return;
        }
        if (cbfun != CommonCallbacks_334.preeditCandidateCallback.address()) {
            cir.setReturnValue(dispatcher.update(cbfun));
        }
    }

}
