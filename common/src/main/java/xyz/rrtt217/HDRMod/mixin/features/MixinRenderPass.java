package xyz.rrtt217.HDRMod.mixin.features;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.api.RenderPassBackend;
import com.mojang.renderpearl.frontend.FrontendRenderPass;
import com.mojang.renderpearl.frontend.FrontendRenderPipeline;
import xyz.rrtt217.HDRMod.core.RenderPipelineFormatModifier;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Mixin(FrontendRenderPass.class)
public class MixinRenderPass {
    @Mutable
    @Shadow
    @Final
    private final List<RenderPassDescriptor.@Nullable Attachment<Optional<Vector4fc>>> colorAttachments;
    @Mutable
    @Shadow
    @Final
    private final RenderPassBackend backend;

    @Shadow
    private @Nullable FrontendRenderPipeline boundPipeline;

    @Mutable
    @Shadow
    @Final
    protected final HashMap<String, Object> uniforms = new HashMap();

    @Shadow
    private boolean constantsPushed = false;

    @Shadow
    private void setUniform(final String name, final @Nullable Object value){}

    public MixinRenderPass(List<RenderPassDescriptor.@Nullable Attachment<Optional<Vector4fc>>> colorAttachments, RenderPassBackend backend) {
        this.colorAttachments = colorAttachments;
        this.backend = backend;
    }

    @Inject(method = "setPipeline", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    private void hdr_mod$modifyRenderPipelineIfFormatNotMatch(CompiledRenderPipeline pipeline, CallbackInfo ci){
        GpuFormat[] formats = new GpuFormat[this.colorAttachments.size()];
        for(int i = 0; i < this.colorAttachments.size(); i++){
            RenderPassDescriptor.Attachment<Optional<Vector4fc>> attachment = this.colorAttachments.get(i);
            if (attachment != null) {
                formats[i] = attachment.textureView().texture().getFormat();
            }
        }
        FrontendRenderPipeline modifiedPipeline = (FrontendRenderPipeline) RenderPipelineFormatModifier.modifyRenderPipelineFormat(pipeline, formats);
        this.boundPipeline = modifiedPipeline;
        this.backend.setPipeline(modifiedPipeline.backendRenderPipeline());
        this.uniforms.forEach(this::setUniform);
        this.constantsPushed = false;
        ci.cancel();
    }
}
