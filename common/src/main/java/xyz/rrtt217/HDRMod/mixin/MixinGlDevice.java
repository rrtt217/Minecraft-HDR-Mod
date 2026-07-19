package xyz.rrtt217.HDRMod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.rrtt217.HDRMod.core.DXGIStateManager;
import xyz.rrtt217.HDRMod.util.DX11InteropShim;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

import static xyz.rrtt217.HDRMod.core.DXGIStateManager.actuallyUseInteropSDL;
import static xyz.rrtt217.HDRMod.core.DXGIStateManager.interopShimContext;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice")
public class MixinGlDevice {
    @ModifyArgs(method = "createTexture(Ljava/lang/String;ILcom/mojang/blaze3d/textures/TextureFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texImage2D(IIIIIIIILjava/nio/ByteBuffer;)V", ordinal = 1))
    private void hdr_mod$upgradeColorBufferFormat$0(Args args)
    {
        if((int) args.get(2) != TextureUpgradeUtils.srcTextureFormat) return;
        if(TextureUpgradeUtils.getTargetTextureFormat() > 0){
            args.set(2, TextureUpgradeUtils.getTargetTextureFormat());
            TextureUpgradeUtils.resetTargetTextureFormat();
        }
        if(TextureUpgradeUtils.getTargetReadPixelFormat() > 0){
            args.set(7, TextureUpgradeUtils.getTargetReadPixelFormat());
            TextureUpgradeUtils.resetTargetReadPixelFormat();
        }
    }
    @Inject(method = "presentFrame", at = @At("HEAD"), cancellable = true)
    private void present(CallbackInfo ci) {
        if(!actuallyUseInteropSDL) return;
        DXGIStateManager.presentDxSwapChain();
        ci.cancel();
    }

    @Inject(method = "setVsync", at = @At("HEAD"), cancellable = true)
    private void configure(boolean enabled, CallbackInfo ci) {
        if (interopShimContext != 0 && actuallyUseInteropSDL) {
            DX11InteropShim.nSetSwapInterval(interopShimContext, enabled ? 1 : 0);
            ci.cancel();
        }
    }
    @Inject(method = "close", at = @At("HEAD"))
    private void close(CallbackInfo ci) {
        DXGIStateManager.destroyDxDevice();
    }
}
