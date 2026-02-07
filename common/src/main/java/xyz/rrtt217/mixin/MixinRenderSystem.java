package xyz.rrtt217.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.TimeSource;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RenderSystem.class, priority = 1010)
public class MixinRenderSystem {
    @Inject(method = "initBackendSystem", at = @At("HEAD"))
    private static void hdr_mod$colorManagementHint(CallbackInfoReturnable<TimeSource.NanoTimeSource> cir) {
        GLFW.glfwInitHint(0x00026002,GLFW.GLFW_TRUE);
    }
}
