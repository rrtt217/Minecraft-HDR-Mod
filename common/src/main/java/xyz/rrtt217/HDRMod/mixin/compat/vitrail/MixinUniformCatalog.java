package xyz.rrtt217.HDRMod.mixin.compat.vitrail;

import com.llamalad7.mixinextras.sugar.Local;
import dev.vitrail.uniform.UniformCatalog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.compat.vitrail.HDRModValues;

@Mixin(UniformCatalog.class)
public class MixinUniformCatalog {
    @Inject(method = "engine", at = @At(value = "INVOKE", target = "Ldev/vitrail/uniform/values/PlayerValues;register(Ldev/vitrail/uniform/UniformCatalog$Builder;)V"))
    private static void hdr_mod$register(CallbackInfoReturnable<UniformCatalog> cir, @Local(name = "builder") UniformCatalog.Builder builder){
        HDRModValues.register(builder);
    }
}
