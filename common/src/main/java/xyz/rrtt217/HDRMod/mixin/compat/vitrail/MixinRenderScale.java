package xyz.rrtt217.HDRMod.mixin.compat.vitrail;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.GpuFormat;
import dev.vitrail.render.RenderScale;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderScale.class)
public class MixinRenderScale {
    @WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/GpuFormat;RGBA8_UNORM:Lcom/mojang/blaze3d/GpuFormat;", opcode = Opcodes.GETSTATIC))
    private static GpuFormat hdr_mod$modifyRenderScaleFormat(Operation<GpuFormat> original) {
        return GpuFormat.RGBA16_FLOAT;
    }
}
