package xyz.rrtt217.HDRMod.mixin.init;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.jna.Platform;
import io.homo.superresolution.common.presentation.vulkan.VulkanPresentationFeature;
import net.minecraft.util.TimeSource;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin;
import xyz.rrtt217.HDRMod.util.platform.SetupBeforeGLFWInit;

import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.hasSr;


@Mixin(value = RenderSystem.class, priority = 1010)
public class MixinRenderSystem {
    // Enables Wayland color management. No effect on non-wayland platform.
    @Inject(method = "initBackendSystem", at = @At("HEAD"))
    private static void hdr_mod$colorManagementHint(CallbackInfoReturnable<TimeSource.NanoTimeSource> cir) {
        if(HDRModMixinPlugin.hasGlfwLib) {
            SetupBeforeGLFWInit.setup();
            GLFW.glfwInitHint(0x00026002,GLFW.GLFW_TRUE);
            if(Platform.isWindows()) {
                GLFW.glfwInitHint(0x00050005,GLFW.GLFW_FALSE);
            }
        }
    }
}
