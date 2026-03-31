package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
    @ModifyVariable(method = "_glBindFramebuffer", at = @At("HEAD"), argsOnly = true, index = 1)
    private static int hdr_mod$useDxgiPbo(int value){
        if(value == 0 && GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WIN32) return GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(Minecraft.getInstance().getWindow().handle());
        return value;
    }
}
