package xyz.rrtt217.HDRMod.neoforge.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.TimeSource;
import net.neoforged.fml.earlydisplay.DisplayWindow;
import net.neoforged.fml.loading.EarlyLoadingScreenController;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(RenderSystem.class)
public class NeoForgeMixinRenderSystem {
    @Inject(method = "initBackendSystem", at = @At("HEAD"))
    private static void hdr_mod$reinitGlfw(CallbackInfoReturnable<TimeSource.NanoTimeSource> cir) {
        EarlyLoadingScreenController earlyLoadingScreen = EarlyLoadingScreenController.current();
        if (earlyLoadingScreen != null) {
            long handle = earlyLoadingScreen.takeOverGlfwWindow();
            GLFW.glfwMakeContextCurrent(handle);
            if(EarlyLoadingScreenController.current() instanceof DisplayWindow){
                ((DisplayWindow) EarlyLoadingScreenController.current()).close();
            }
        }
        GLFW.glfwTerminate();
        if(enableHDR && HDRModMixinPlugin.hasGlfwLib) GLFW.glfwInitHint(0x00026002,GLFW.GLFW_TRUE);
    }
}
