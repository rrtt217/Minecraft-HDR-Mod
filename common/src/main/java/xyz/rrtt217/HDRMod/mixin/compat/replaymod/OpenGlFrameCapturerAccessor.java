package xyz.rrtt217.HDRMod.mixin.compat.replaymod;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.replaymod.render.capturer.OpenGlFrameCapturer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OpenGlFrameCapturer.class)
public interface OpenGlFrameCapturerAccessor {
    @Invoker
    RenderTarget callFrameBuffer();
}
