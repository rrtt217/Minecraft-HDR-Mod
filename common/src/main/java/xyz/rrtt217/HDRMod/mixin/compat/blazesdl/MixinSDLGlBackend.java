package xyz.rrtt217.HDRMod.mixin.compat.blazesdl;

import com.sun.jna.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import top.fifthlight.blazesdl.SDLGlBackend;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

import java.util.List;
import java.util.Set;

import static org.lwjgl.sdl.SDLVideo.*;

@Mixin(SDLGlBackend.class)
public class MixinSDLGlBackend {
    @Inject(method = "setWindowHints", at = @At("TAIL"))
    private void hdr_mod$setWindowHints(CallbackInfo ci){
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
            SDL_GL_SetAttribute(SDL_GL_GREEN_SIZE, 16);
            SDL_GL_SetAttribute(SDL_GL_BLUE_SIZE, 16);
            SDL_GL_SetAttribute(SDL_GL_FLOATBUFFERS, 1);
        }
        else if(applyWindowsWorkaround) {
            HDRMod.LOGGER.warn("A workaround (WindowsIntelRequireGlDxInterop) has been applied for your platform and hardware. HDR Mod may or may not work.");
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
