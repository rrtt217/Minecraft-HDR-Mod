package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

import java.util.OptionalInt;

public class ColorTransformRenderer implements AutoCloseable {
    static{
        RenderPipeline.Builder builder = RenderPipeline.builder(new RenderPipeline.Snippet[0]).withLocation("pipeline/color_transform").withFragmentShader(ResourceLocation.fromNamespaceAndPath("hdr_mod","color_transform")).withVertexShader("core/screenquad").withSampler("InSampler").withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES).withUniform("ColorTransform", UniformType.UNIFORM_BUFFER);
        for(Enums.Primaries p : Enums.Primaries.values()) {
            builder = builder.withShaderDefine("PRIMARIES_"+p.toString(), p.getId());
        }
        for(Enums.TransferFunction tf : Enums.TransferFunction.values()) {
            builder = builder.withShaderDefine("TRANSFER_FUNCTION_"+tf.toString(), tf.getId());
        }
        COLOR_TRANSFORM = builder.build();
    }
    public static final RenderPipeline COLOR_TRANSFORM;
    private RenderTarget srcTarget;
    private GpuTexture dstTexture;
    private GpuTextureView dstTextureView;
    private int dstTextureFormat;
    private int dstReadPixelFormat;
    private ColorTransformUBO colorTransformUbo;
    private GpuBuffer colorTransformBuffer;
    public ColorTransformRenderer(RenderTarget srcTarget, String string) {
        this.srcTarget = srcTarget;
        this.colorTransformUbo = new ColorTransformUBO(string);
        // Set a group of default UBO values. You may call updateColorTransformUBO manually to update later.
        updateColorTransformUniforms(203.0F, 0.0F, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
        TextureUpgradeUtils.setTargetTextureFormat(GL30.GL_RGBA16F);
        TextureUpgradeUtils.setTargetReadPixelFormat(GL30.GL_HALF_FLOAT);
        this.dstReadPixelFormat = GL30.GL_HALF_FLOAT;
        this.dstTextureFormat = GL30.GL_RGBA16F;
        this.dstTexture = RenderSystem.getDevice().createTexture(()->"Color Transform Destination Texture",15, TextureFormat.RGBA8, srcTarget.width, srcTarget.height, 1, 1);
        this.dstTextureView = RenderSystem.getDevice().createTextureView(this.dstTexture);
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
            this.dstReadPixelFormat = GL30.GL_UNSIGNED_SHORT;
            this.dstTextureFormat = GL30.GL_RGBA16;
            this.recreateTexture();
        }
    }
    public void resize(){
        if(this.dstTexture.getHeight(0) != this.srcTarget.height || this.dstTexture.getWidth(0) != this.srcTarget.width){
            this.recreateTexture();
        }
    }
    public void recreateTexture(){
        this.dstTextureView.close();
        this.dstTexture.close();
        TextureUpgradeUtils.setTargetTextureFormat(this.dstTextureFormat);
        TextureUpgradeUtils.setTargetReadPixelFormat(this.dstReadPixelFormat);
        this.dstTexture = RenderSystem.getDevice().createTexture(()->"Color Transform Destination Texture",15, TextureFormat.RGBA8, srcTarget.width, srcTarget.height, 1, 1);
        this.dstTextureView = RenderSystem.getDevice().createTextureView(this.dstTexture);
    }
    public void render(){
        this.resize();
        // The actual renderer.
        if (srcTarget.getColorTextureView() != null) {
            try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Color Transform", this.dstTextureView, OptionalInt.empty())) {
                renderPass.setPipeline(COLOR_TRANSFORM);
                RenderSystem.bindDefaultUniforms(renderPass);
                if (this.colorTransformUbo != null) renderPass.setUniform("ColorTransform", this.colorTransformBuffer);
                renderPass.bindSampler("InSampler", srcTarget.getColorTextureView());
                renderPass.draw(0, 3);
            }
        } else {
            throw new IllegalStateException("colorTexture is null");
        }
    }
    public RenderTarget getSrcTarget(){
        return this.srcTarget;
    }
    public void setSrcTarget(RenderTarget srcTarget){
        this.srcTarget = srcTarget;
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
        srcTarget = null;
        colorTransformUbo.close();
        dstTextureView.close();
        dstTexture.close();
    }
}
