package xyz.rrtt217.HDRMod.mixin.compat.ixeris;

import me.decce.ixeris.api.IxerisApi;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;
import xyz.rrtt217.HDRMod.core.DXGISwapchainCache;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

@Mixin(value = DXGIStateManager.class, remap = false)
public class MixinDXGIStateManager {

    @Inject(method = "replaceFbo", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hdr_mod$replaceFboIxerisCompat(int originalFbo,
                                                       CallbackInfoReturnable<Integer> cir) {
        IxerisApi api = IxerisApi.getInstance();
        if (!api.isEnabled() || api.isOnMainThreadOrInit()) return;

        if (originalFbo != 0 || GLFW.glfwGetPlatform() != GLFW.GLFW_PLATFORM_WIN32) {
            cir.setReturnValue(originalFbo);
            return;
        }

        com.mojang.blaze3d.platform.Window window =
                net.minecraft.client.Minecraft.getInstance().getWindow();
        long handle = window.handle();

        if (GLFWDXGIUtils.glfwGetPendingDXGIResize(handle) > 0) {
            GLFWDXGIUtils.glfwReleaseCurrentContext();
            api.query(() -> {
                GLFWDXGIUtils.glfwCompletePendingDXGIResize(handle);
                return null;
            });
            GLFWDXGIUtils.glfwReacquireCurrentContext(handle);
            DXGISwapchainCache.texture    = 0;
            DXGISwapchainCache.lastWidth  = 0;
            DXGISwapchainCache.lastHeight = 0;
        }

        int tex = GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(handle);
        if (tex != DXGISwapchainCache.texture) {
            DXGISwapchainCache.texture    = tex;
            DXGISwapchainCache.lastWidth  = 0;
            DXGISwapchainCache.lastHeight = 0;
        }

        cir.setReturnValue(DXGIStateManager.replaceFboGLOnly(
                DXGISwapchainCache.texture,
                window.getWidth(), window.getHeight(),
                window.isMinimized(), originalFbo));
    }
}
