package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import xyz.rrtt217.HDRMod.util.Enums;

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
        this.dstReadPixelFormat = GL30.GL_HALF_FLOAT;
        this.dstTextureFormat = GL30.GL_RGBA16F;
        this.createTextures(srcTarget.width, srcTarget.height);
    }

    private void createTextures(int width, int height) {
        this.dstTextureFramebufferId = GlStateManager.glGenFramebuffers();
        this.dstTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(this.dstTextureId);
        GlStateManager._texParameter(3553, 10241, 9728);
        GlStateManager._texParameter(3553, 10240, 9728);
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
            // You need to disable blend to make presentation looks correct.
            RenderSystem.disableBlend();

            //

            ColorTransformShader.setSampler("DiffuseSampler", srcTarget.getColorTextureId());
            setColorTransformUniforms();
            RenderSystem.setShader(() -> ColorTransformShader);

            Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)this.dstTextureWidth, (float)this.dstTextureHeight, 0.0F, 1000.0F, 3000.0F);
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
            RenderSystem.backupProjectionMatrix();
            PoseStack poseStack = RenderSystem.getModelViewStack();
            poseStack.pushPose();
            poseStack.setIdentity();
            poseStack.translate(0.0F, 0.0F, -2000.0F);
            RenderSystem.applyModelViewMatrix();

            /*
            if (ColorTransformShader.MODEL_VIEW_MATRIX != null) {
                ColorTransformShader.MODEL_VIEW_MATRIX.set((new Matrix4f()).translation(0.0F, 0.0F, -2000.0F));
            }

            if (ColorTransformShader.PROJECTION_MATRIX != null) {
                ColorTransformShader.PROJECTION_MATRIX.set(matrix4f);
            }
            */

            //ColorTransformShader.apply();

            float f = (float)dstTextureWidth;
            float g = (float)dstTextureHeight;
            float h = (float)srcTarget.viewWidth / (float)srcTarget.width;
            float k = (float)srcTarget.viewHeight / (float)srcTarget.height;

            Tesselator tesselator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex((double)0.0F, (double)g, (double)0.0F).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex((double)f, (double)g, (double)0.0F).uv(h, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex((double)f, (double)0.0F, (double)0.0F).uv(h, k).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex((double)0.0F, (double)0.0F, (double)0.0F).uv(0.0F, k).color(255, 255, 255, 255).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());

            //ColorTransformShader.clear();
            RenderSystem.depthMask(true);
            RenderSystem.colorMask(true, true, true, true);

            RenderSystem.restoreProjectionMatrix();
            poseStack.popPose();
            RenderSystem.applyModelViewMatrix();

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
    public int getDstTextureFormat() { return this.dstTextureFormat; }
    public int getDstReadPixelFormat() { return this.dstReadPixelFormat; }
    public void close(){
        srcTarget = null;
        this.destroyTextures();
    }
}
