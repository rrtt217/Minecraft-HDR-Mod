package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.GpuFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;
import xyz.rrtt217.HDRMod.util.Enums;

import java.util.Optional;

public class ColorTransformRenderer implements AutoCloseable {
    private static RenderPipeline.Builder builder;
    static{
        BindGroupLayout COLOR_TRANSFORM_LAYOUT = BindGroupLayout.builder().withSampler("Sampler0").withUniform("ColorTransform", UniformType.UNIFORM_BUFFER).build();
        builder = RenderPipeline.builder(RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.GLOBALS).buildSnippet()).withLocation("pipeline/color_transform").withFragmentShader(Identifier.fromNamespaceAndPath("hdr_mod","color_transform")).withVertexShader("core/screenquad").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withBindGroupLayout(COLOR_TRANSFORM_LAYOUT)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLES);
        for(Enums.Primaries p : Enums.Primaries.values()) {
            builder = builder.withShaderDefine("PRIMARIES_"+p.toString(), p.getId());
        }
        for(Enums.TransferFunction tf : Enums.TransferFunction.values()) {
            builder = builder.withShaderDefine("TRANSFER_FUNCTION_"+tf.toString(), tf.getId());
        }
        COLOR_TRANSFORM = builder.build();
        COLOR_TRANSFORM_PQ = builder.withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA16_UNORM, 15)).build();
    }
    public static RenderPipeline COLOR_TRANSFORM;
    public static RenderPipeline COLOR_TRANSFORM_PQ;
    private GpuTextureView srcTextureView;
    private GpuTexture dstTexture;
    private GpuTextureView dstTextureView;
    private GpuFormat dstTextureFormat;
    private ColorTransformUBO colorTransformUbo;
    private GpuBuffer colorTransformBuffer;

    public ColorTransformRenderer(GpuTextureView srcTextureView, String string) {
        this.srcTextureView = srcTextureView;
        this.colorTransformUbo = new ColorTransformUBO(string);
        // Set a group of default UBO values. You may call updateColorTransformUniforms manually to update later.
        updateColorTransformUniforms(203.0F, 0.0F, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
        this.dstTextureFormat = GpuFormat.RGBA16_FLOAT;
        this.dstTexture = RenderSystem.getDevice().createTexture(() -> "Color Transform Destination Texture",GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, dstTextureFormat, srcTextureView.getWidth(0), srcTextureView.getHeight(0), 1, 1);
        this.dstTextureView = RenderSystem.getDevice().createTextureView(this.dstTexture);
    }

    public ColorTransformRenderer(RenderTarget srcTarget, String string) {
        this(srcTarget.getColorTextureView(), string);
    }
    public void updateColorTransformUniforms(float UIBrightness, float EotfEmulate, Enums.Primaries Primaries, Enums.TransferFunction TransferFunction){
        updateColorTransformUniforms(UIBrightness, EotfEmulate, Primaries.getId(), TransferFunction.getId());
    }
    public void updateColorTransformUniforms(float UIBrightness, float EotfEmulate, int Primaries, int TransferFunction) {
        if(this.colorTransformUbo == null) {
            throw new IllegalStateException("Cannot update color transform UBO when UBO is null");
        }
        this.colorTransformBuffer = colorTransformUbo.update(UIBrightness, EotfEmulate, Primaries, TransferFunction);
        if(TransferFunction == Enums.TransferFunction.ST2084_PQ.getId() && this.colorTransformUbo.lastTransferFunction != Enums.TransferFunction.ST2084_PQ.getId()) {
            this.dstTextureFormat = GpuFormat.RGBA16_UNORM;
            this.recreateTexture();
        }
        else if(TransferFunction != Enums.TransferFunction.ST2084_PQ.getId() && this.colorTransformUbo.lastTransferFunction == Enums.TransferFunction.ST2084_PQ.getId()) {
            this.dstTextureFormat = GpuFormat.RGBA16_FLOAT;
            this.recreateTexture();
        }
    }
    public void resize(){
        if(this.dstTexture.getHeight(0) != this.srcTextureView.getHeight(0) || this.dstTexture.getWidth(0) != this.srcTextureView.getWidth(0)) {
            this.recreateTexture();
        }
    }
    public void recreateTexture(){
        this.dstTextureView.close();
        this.dstTexture.close();
        this.dstTexture = RenderSystem.getDevice().createTexture(() -> "Color Transform Destination Texture",GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, this.dstTextureFormat, srcTextureView.getWidth(0), srcTextureView.getHeight(0), 1, 1);
        this.dstTextureView = RenderSystem.getDevice().createTextureView(this.dstTexture);
    }
    public void render(){
        this.resize();
        // The actual renderer.
        if (srcTextureView != null) {
            try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Color Transform", this.dstTextureView, Optional.empty())) {
                RenderSystem.bindDefaultUniforms(renderPass);
                if(this.dstTextureFormat == GpuFormat.RGBA16_UNORM) renderPass.setPipeline(COLOR_TRANSFORM_PQ);
                else renderPass.setPipeline(COLOR_TRANSFORM);
                if (this.colorTransformUbo != null) renderPass.setUniform("ColorTransform", this.colorTransformBuffer);
                renderPass.bindTexture("InSampler", srcTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                renderPass.draw(3, 1, 0, 0);
            }
        } else {
            throw new IllegalStateException("colorTexture is null");
        }
    }

    public GpuTextureView getSrcTextureView(){
        return this.srcTextureView;
    }
    public void setSrcSrcTextureView(GpuTextureView SrcTextureView){
        this.srcTextureView = SrcTextureView;
        this.recreateTexture();
    }
    public GpuTexture getDstTexture(){
        return this.dstTexture;
    }
    public GpuTextureView getDstTextureView(){
        return this.dstTextureView;
    }
    public void close(){
        colorTransformBuffer = null;
        srcTextureView = null;
        colorTransformUbo.close();
        dstTextureView.close();
        dstTexture.close();
    }
}
