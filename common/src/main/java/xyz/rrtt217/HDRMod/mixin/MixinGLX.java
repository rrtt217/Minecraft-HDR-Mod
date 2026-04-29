package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.SharedConstants;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static xyz.rrtt217.HDRMod.HDRMod.enableHDR;

@Mixin(GLX.class)
public class MixinGLX {
    @Redirect(method = "_initGlfw", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;DEBUG_PREFER_WAYLAND:Z", opcode = Opcodes.GETSTATIC))
    private static boolean hdr_mod$preferWaylandForHDR(){
        if(enableHDR) return true;
        else return SharedConstants.DEBUG_PREFER_WAYLAND;
    }
}
