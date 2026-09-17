package xyz.rrtt217.HDRMod.mixin.gl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.renderpearl.backend.opengl.GlBackend;
import com.sun.jna.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.interop.NewGLInteropResourceManager;

import java.util.List;
import java.util.Set;

import static org.lwjgl.sdl.SDLVideo.*;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

@Mixin(GlBackend.class)
public class MixinGlBackend {

    @WrapOperation(method = "createWindow", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDLVideo;SDL_GL_SetAttribute(II)Z"))
    private boolean hdr_mod$skipsRGB(int attr, int value, Operation<Boolean> original){
        if(attr == SDL_GL_FRAMEBUFFER_SRGB_CAPABLE) return true;
        return original.call(attr, value);
    }

    @Inject(method = "createWindow", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDLVideo;SDL_GL_SetAttribute(II)Z", ordinal = 4))
    private void hdr_mod$setWindowHints(String title, int width, int height, long flags, CallbackInfoReturnable<Long> cir){

        // Get config.
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();

        // Get GPU.
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
        boolean hasNvidiaCard = false;
        boolean hasIntelCard = false;
        boolean hasOnlyIntelCard = true;
        for (GraphicsCard card : graphicsCards) {
            if (card.getVendor().toLowerCase().contains("nvidia") && !hasNvidiaCard) {
                hasNvidiaCard = true;
            }
            if (card.getVendor().toLowerCase().contains("intel") && !hasIntelCard) {
                hasIntelCard = true;
            }
            if (!card.getVendor().toLowerCase().contains("intel") && !hdr_mod$isVirtualGraphicsCard(card) && hasOnlyIntelCard) {
                hasOnlyIntelCard = false;
            }
        }
        hasOnlyIntelCard = hasOnlyIntelCard && hasIntelCard;
        //boolean applyLinuxWorkaround = (platform == GLFW.GLFW_PLATFORM_X11 || (hasNvidiaCard && platform == GLFW.GLFW_PLATFORM_WAYLAND)) && !config.forceDisableGlfwWorkaround;
        boolean applyWindowsWorkaround = (hasOnlyIntelCard && Platform.isWindows()) && !config.forceDisableGlfwWorkaround;

        if(!applyWindowsWorkaround && !config.forceActivateGlDxInterop) {
            SDL_GL_SetAttribute(SDL_GL_RED_SIZE, 16);
            SDL_GL_SetAttribute(SDL_GL_FLOATBUFFERS, 1);
        }
        else{
            if(Platform.isWindows()) {
                if (applyWindowsWorkaround) {
                    LOGGER.warn("A workaround (WindowsIntelRequireGlDxInterop) has been applied for your platform and hardware. HDR Mod may or may not work.");
                }
                HDRMod.glInteropResourceManager = new NewGLInteropResourceManager();
            }
        }
    }

    @Unique
    private static final Set<String> VIRTUAL_KEYWORDS = Set.of(
            "vmware", "virtualbox", "qxl", "virtio", "virtio-gpu",
            "hyper-v video", "microsoft basic display", "citrix",
            "parallels display", "rdpud", "idd", "mirage", "virtual"
    );
    @Unique
    private boolean hdr_mod$isVirtualGraphicsCard(GraphicsCard card) {
        String name = card.getName().toLowerCase();
        String vendor = card.getVendor().toLowerCase();

        for (String keyword : VIRTUAL_KEYWORDS) {
            if (name.contains(keyword) || vendor.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}
