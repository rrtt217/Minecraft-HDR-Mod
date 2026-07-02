package xyz.rrtt217.HDRMod.mixin.compat.iris;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.unimi.dsi.fastutil.Function;
import me.shedaniel.autoconfig.AutoConfig;
import net.irisshaders.iris.pipeline.IrisPipelines;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.RenderPipelineFormatModifier;

@Mixin(IrisPipelines.class)
public class MixinIrisPipelines {
    @Shadow
    public static void assignPipeline(RenderPipeline pipeline, ShaderKey programId) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static void assignToMain(RenderPipeline pipeline, Function<IrisRenderingPipeline, ShaderKey> o) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static void assignToShadow(RenderPipeline pipeline, Function<IrisRenderingPipeline, ShaderKey> o) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Unique
    private static ThreadLocal<Boolean> HAS_MODIFIED_MAIN;
    @Unique
    private static ThreadLocal<Boolean> HAS_MODIFIED_SHADOW;
    @Unique
    private static ThreadLocal<Boolean> HAS_MODIFIED_PIPELINE;

    @Inject(method = "assignToMain", at = @At("HEAD"))
    private static void hdr_mod$assignModifiedIrisPipelinesMain(RenderPipeline pipeline, Function<IrisRenderingPipeline, ShaderKey> o, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(HAS_MODIFIED_MAIN == null) HAS_MODIFIED_MAIN = ThreadLocal.withInitial(() -> false);
        if (HAS_MODIFIED_MAIN.get() || config.modifyDefaultColorTargetState) return;
        HAS_MODIFIED_MAIN.set(true);
        try {
            assignToMain(RenderPipelineFormatModifier.modifyRenderPipelineFormat(pipeline, new GpuFormat[]{GpuFormat.RGBA16_FLOAT}), o);
        }
        catch (IllegalStateException _) {}
        finally {HAS_MODIFIED_MAIN.set(false);}
    }
    @Inject(method = "assignToShadow", at = @At("HEAD"))
    private static void hdr_mod$assignModifiedIrisPipelinesShadow(RenderPipeline pipeline, Function<IrisRenderingPipeline, ShaderKey> o, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(HAS_MODIFIED_SHADOW == null) HAS_MODIFIED_SHADOW = ThreadLocal.withInitial(() -> false);
        if (HAS_MODIFIED_SHADOW.get() || config.modifyDefaultColorTargetState) return;
        HAS_MODIFIED_SHADOW.set(true);
        try {
            assignToShadow(RenderPipelineFormatModifier.modifyRenderPipelineFormat(pipeline, new GpuFormat[]{GpuFormat.RGBA16_FLOAT}), o);
        }
        catch (IllegalStateException _) {}
        finally {HAS_MODIFIED_SHADOW.set(false);}
    }
    @Inject(method = "assignPipeline", at = @At("HEAD"))
    private static void hdr_mod$assignModifiedIrisPipelines(RenderPipeline pipeline, ShaderKey programId, CallbackInfo ci) {
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(HAS_MODIFIED_PIPELINE == null) HAS_MODIFIED_PIPELINE = ThreadLocal.withInitial(() -> false);
        if (HAS_MODIFIED_PIPELINE.get() || config.modifyDefaultColorTargetState) return;
        HAS_MODIFIED_PIPELINE.set(true);
        try {
            assignPipeline(RenderPipelineFormatModifier.modifyRenderPipelineFormat(pipeline, new GpuFormat[]{GpuFormat.RGBA16_FLOAT}), programId);
        }
        catch (IllegalStateException _) {}
        finally {HAS_MODIFIED_PIPELINE.set(false);}
    }
}
