package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.platform.GLX;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(GLX.class)
public class MixinGLX {
    @Redirect(method = "_initGlfw", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;DEBUG_PREFER_WAYLAND:Z", opcode = Opcodes.GETSTATIC))
    private static boolean hdr_mod$preferWaylandForHDR(){
        return true;
    }
}
