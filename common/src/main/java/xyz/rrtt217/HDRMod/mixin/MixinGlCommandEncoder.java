package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.GLFWDXGIUtils;
import xyz.rrtt217.HDRMod.util.HDRModInjectHooks;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

@Mixin(GlCommandEncoder.class)
public class MixinGlCommandEncoder {
    @ModifyArg(method = "copyTextureToBuffer(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/buffers/GpuBuffer;JLjava/lang/Runnable;IIIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_readPixels(IIIIIIJ)V"), index = 5)
    private int hdr_mod$modifyReadPixelFormat(int i){
        if(TextureUpgradeUtils.getTargetReadPixelFormat() < 0) return i;
        int format = TextureUpgradeUtils.getTargetReadPixelFormat();
        TextureUpgradeUtils.resetTargetReadPixelFormat();
        return format;
    }
    @ModifyArg(method = "presentTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/DirectStateAccess;blitFrameBuffers(IIIIIIIIIIII)V"), index = 6)
    private int hdr_mod$modifyPresentTexture(int i){
        if(i == 0 && GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WIN32) {
            //HDRMod.LOGGER.info("DXGI PBO {}", Minecraft.getInstance().getWindow().handle());
            return GLFWDXGIUtils.glfwGetWindowSwapchainImageTexture(Minecraft.getInstance().getWindow().handle());
        }
        return i;
    }
}
