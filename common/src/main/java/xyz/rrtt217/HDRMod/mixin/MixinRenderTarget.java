package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.HDRMod;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.ColorTransformRenderer;
import xyz.rrtt217.HDRMod.util.GLFWColorManagementUtils;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

import java.util.Arrays;

import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;


@Mixin(RenderTarget.class)
public class MixinRenderTarget {
    @Mutable
    @Final
    @Shadow
    protected final GpuFormat format;

    public MixinRenderTarget(GpuFormat format) {
        this.format = format;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hdr_mod$scanRenderTarget(CallbackInfo ci) {
        if(format == GpuFormat.RGBA8_UNORM) {
            LOGGER.info("RGBA8 RenderTarget StackTrace", new Throwable());
        }
    }
}
