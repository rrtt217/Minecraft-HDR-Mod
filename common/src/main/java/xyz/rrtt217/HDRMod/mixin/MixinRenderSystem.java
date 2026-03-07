package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.TimeSource;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(value = RenderSystem.class, priority = 1010)
public class MixinRenderSystem {
    // Enables Wayland color management. No effect on non-wayland platform.
    @Inject(method = "initBackendSystem", at = @At("HEAD"))
    private static void hdr_mod$colorManagementHint(CallbackInfoReturnable<TimeSource.NanoTimeSource> cir) {
        if(enableHDR && HDRModMixinPlugin.hasGlfwLib) GLFW.glfwInitHint(0x00026002,GLFW.GLFW_TRUE);
    }
}
