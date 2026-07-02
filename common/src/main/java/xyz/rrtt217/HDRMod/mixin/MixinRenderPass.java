package xyz.rrtt217.HDRMod.mixin;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.RenderPassBackend;
import xyz.rrtt217.HDRMod.core.RenderPipelineFormatModifier;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassDescriptor;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(RenderPass.class)
public class MixinRenderPass {
    @Mutable
    @Shadow
    @Final
    private final List<RenderPassDescriptor.@Nullable Attachment<Optional<Vector4fc>>> colorAttachments;
    @Mutable
    @Shadow
    @Final
    private final RenderPassBackend backend;

    public MixinRenderPass(List<RenderPassDescriptor.@Nullable Attachment<Optional<Vector4fc>>> colorAttachments, RenderPassBackend backend) {
        this.colorAttachments = colorAttachments;
        this.backend = backend;
    }

    @Inject(method = "setPipeline", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void hdr_mod$modifyRenderPipelineIfFormatNotMatch(RenderPipeline pipeline, CallbackInfo ci){
        GpuFormat[] formats = new GpuFormat[this.colorAttachments.size()];
        for(int i = 0; i < this.colorAttachments.size(); i++){
            RenderPassDescriptor.Attachment<Optional<Vector4fc>> attachment = this.colorAttachments.get(i);
            if (attachment != null) {
                formats[i] = attachment.textureView().texture().getFormat();
            }
        }
        RenderPipeline modifiedPipeline = RenderPipelineFormatModifier.modifyRenderPipelineFormat(pipeline, formats);
        this.backend.setPipeline(modifiedPipeline);
        ci.cancel();
    }
}
