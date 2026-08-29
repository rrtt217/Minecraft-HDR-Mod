package xyz.rrtt217.HDRMod.mixin.compat.sr;

import io.homo.superresolution.core.graphics.impl.framebuffer.FrameBufferTextureAdapter;
import io.homo.superresolution.core.graphics.impl.framebuffer.IFrameBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FrameBufferTextureAdapter.class)
public interface FrameBufferTextureAdapterAccessor {
    @Accessor
    IFrameBuffer getFrameBuffer();
}
