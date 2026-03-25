package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod.util.Enums;
import xyz.rrtt217.HDRMod.util.TextureUpgradeUtils;

import java.io.IOException;
import java.nio.IntBuffer;

public class ColorTransformRenderer implements AutoCloseable {
    public ShaderInstance ColorTransformShader;
    private RenderTarget srcTarget;
    private int dstTextureId = -1;
    private int dstTextureFramebufferId = -1;
    private int dstTextureWidth = -1;
    private int dstTextureHeight = -1;
    private int dstTextureFormat;
    private int dstReadPixelFormat;

    public float UIBrightness = -1.0f;
    public float EotfEmulate =  -1.0f;
    public int Primaries = -1;
    public int TransferFunction = -1;

    public ColorTransformRenderer(RenderTarget srcTarget, String string) throws IOException {
        this.srcTarget = srcTarget;
        this.ColorTransformShader = new ShaderInstance(Minecraft.getInstance().getVanillaPackResources().asProvider(), "color_transform", DefaultVertexFormat.BLIT_SCREEN);
        // Set a group of default UBO values. You may call updateColorTransformUBO manually to update later.
        updateColorTransformUniforms(203.0F, 0.0F, Enums.Primaries.SRGB, Enums.TransferFunction.SRGB);
        TextureUpgradeUtils.setTargetTextureFormat(GL30.GL_RGBA16F);
        TextureUpgradeUtils.setTargetReadPixelFormat(GL30.GL_HALF_FLOAT);
        this.dstReadPixelFormat = GL30.GL_HALF_FLOAT;
        this.dstTextureFormat = GL30.GL_RGBA16F;
        this.createTextures(srcTarget.width, srcTarget.height);
    }

    private void createTextures(int width, int height) {
        this.dstTextureFramebufferId = GlStateManager.glGenFramebuffers();
        this.dstTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(this.dstTextureId);
        GlStateManager._texParameter(3553, 10241, srcTarget.filterMode);
        GlStateManager._texParameter(3553, 10240, srcTarget.filterMode);
        GlStateManager._texParameter(3553, 10242, 33071);
        GlStateManager._texParameter(3553, 10243, 33071);
        GlStateManager._texImage2D(3553, 0, this.dstTextureFormat, width, height, 0, 6408, this.dstReadPixelFormat, (IntBuffer)null);
        GlStateManager._glBindFramebuffer(36160, this.dstTextureFramebufferId);
        GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.dstTextureId, 0);
        GlStateManager._bindTexture(0);
        this.dstTextureWidth = this.srcTarget.width;
        this.dstTextureHeight = this.srcTarget.height;
    }

    private void destroyTextures() {
        if (this.dstTextureId > -1) {
            TextureUtil.releaseTextureId(this.dstTextureId);
            this.dstTextureId = -1;
        }
        if (this.dstTextureFramebufferId > -1) {
            GlStateManager._glBindFramebuffer(36160, 0);
            GlStateManager._glDeleteFramebuffers(this.dstTextureFramebufferId);
            this.dstTextureFramebufferId = -1;
        }
    }

    public void updateColorTransformUniforms(float UIBrightness, float EotfEmulate, Enums.Primaries Primaries, Enums.TransferFunction TransferFunction){
        updateColorTransformUniforms(UIBrightness, EotfEmulate, Primaries.getId(), TransferFunction.getId());
    }
    public void updateColorTransformUniforms(float UIBrightness, float EotfEmulate, int Primaries, int TransferFunction) {
        if(TransferFunction == Enums.TransferFunction.ST2084_PQ.getId() && this.TransferFunction != Enums.TransferFunction.ST2084_PQ.getId()) {
            this.dstReadPixelFormat = GL30.GL_UNSIGNED_SHORT;
            this.dstTextureFormat = GL30.GL_RGBA16;
            this.recreateTexture();
        }
        this.UIBrightness = UIBrightness;
        this.EotfEmulate = EotfEmulate;
        this.Primaries = Primaries;
        this.TransferFunction = TransferFunction;
    }

    private void setColorTransformUniforms() {
        this.ColorTransformShader.safeGetUniform("UiBrightness").set(UIBrightness);
        this.ColorTransformShader.safeGetUniform("EotfEmulate").set(EotfEmulate);
        this.ColorTransformShader.safeGetUniform("CurrentTransferFunction").set(TransferFunction);
        this.ColorTransformShader.safeGetUniform("CurrentPrimaries").set(Primaries);
    }

    public void resize(){
        if(this.dstTextureHeight != this.srcTarget.height || this.dstTextureWidth != this.srcTarget.width){
            this.recreateTexture();
        }
    }
    public void recreateTexture(){
        this.destroyTextures();
        this.createTextures(this.srcTarget.width, this.srcTarget.height);
    }
    public void render(){
        this.resize();
        // The actual renderer.

        if (srcTarget.frameBufferId != -1) {
            srcTarget.bindRead();
            GlStateManager._glBindFramebuffer(36160, this.dstTextureFramebufferId);

            RenderSystem.colorMask(true, true, true, false);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.viewport(0, 0, this.dstTextureWidth, this.dstTextureHeight);
            //RenderSystem.enableBlend();

            RenderSystem.setShader(() -> ColorTransformShader);

            setColorTransformUniforms();
            ColorTransformShader.setSampler("DiffuseSampler", srcTarget.getColorTextureId());
            BufferBuilder bufferbuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
            bufferbuilder.addVertex(0.0F, 0.0F, 0.0F);
            bufferbuilder.addVertex(1.0F, 0.0F, 0.0F);
            bufferbuilder.addVertex(1.0F, 1.0F, 0.0F);
            bufferbuilder.addVertex(0.0F, 1.0F, 0.0F);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.depthMask(true);
            RenderSystem.colorMask(true, true, true, true);

            srcTarget.unbindRead();
            GlStateManager._glBindFramebuffer(36160, 0);
        } else {
            throw new IllegalStateException("FrameBuffer is -1");
        }
    }
    public RenderTarget getSrcTarget(){
        return this.srcTarget;
    }
    public void setSrcTarget(RenderTarget srcTarget){
        this.srcTarget = srcTarget;
        this.recreateTexture();
    }
    public int getDstTextureId(){
        return this.dstTextureId;
    }
    public int getDstTextureFramebufferId(){
        return this.dstTextureFramebufferId;
    }
    public void close(){
        srcTarget = null;
        this.destroyTextures();
    }
}
