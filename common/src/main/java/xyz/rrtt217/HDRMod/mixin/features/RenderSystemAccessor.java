package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.class)
public interface RenderSystemAccessor {
    @Accessor
    static PipelineCache getCurrentPipelineCache(){
        throw new AssertionError();
    };
}
