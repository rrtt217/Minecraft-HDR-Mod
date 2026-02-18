package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class BeforeBlitRenderer {

    // Things about BEFORE_BLIT pass.
    public static RenderPipeline.Builder renderPipelineBuilder = RenderPipeline.builder(new RenderPipeline.Snippet[0]).withLocation("pipeline/before_blit").withFragmentShader(ResourceLocation.fromNamespaceAndPath("hdr_mod","before_blit")).withVertexShader("core/screenquad").withSampler("InSampler").withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES);
    public static RenderPipeline BEFORE_BLIT = renderPipelineBuilder.build();
    public static GpuTexture beforeBlitTexture;
    public static GpuTextureView beforeBlitTextureView;
    public static boolean isBeforeBlitReady = false;
    // DynamicFPS compat.
    public static boolean isGameRenderingCanceled = false;
}
