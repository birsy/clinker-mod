package birsy.clinker.client.book;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.neoforged.api.distmarker.Dist;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;


public class PageAtlasRenderer {
    private final int PAGE_RESOLUTION = 384;
    final int sizeX, sizeY;
    final RenderTarget pageAtlas;
    public final List<Page> pages;

    public PageAtlasRenderer(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.pageAtlas = new TextureTarget(PAGE_RESOLUTION * this.sizeX, PAGE_RESOLUTION * this.sizeY, false, Minecraft.ON_OSX);
        this.pageAtlas.enableStencil();

        this.pages = new ArrayList<>();
    }

    public void draw(MultiBufferSource bufferSource) {
        pageAtlas.clear(Minecraft.ON_OSX);
        pageAtlas.bindWrite(true);

        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(
                0.0F, (float)PAGE_RESOLUTION * this.sizeX,
                0.0F, (float)PAGE_RESOLUTION * this.sizeY,
                0.1F, 1000F
        ), VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.applyModelViewMatrix();
        Matrix4f currentMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
        //RenderSystem.getModelViewStack().setIdentity();
        RenderSystem.applyModelViewMatrix();

        PoseStack posestack = new PoseStack();
        posestack.setIdentity();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f pMatrix = posestack.last().pose();
        float x0 = 0, x1 = PAGE_RESOLUTION, y0 = 0, y1 = PAGE_RESOLUTION;
        bufferbuilder.vertex(pMatrix, x0, y1, -500F).uv(0, 1).endVertex();
        bufferbuilder.vertex(pMatrix, x1, y1, -500F).uv(1, 1).endVertex();
        bufferbuilder.vertex(pMatrix, x1, y0, -500F).uv(1, 0).endVertex();
        bufferbuilder.vertex(pMatrix, x0, y0, -500F).uv(0, 0).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        pageAtlas.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.restoreProjectionMatrix();
        //RenderSystem.getModelViewStack().mulPoseMatrix(currentMatrix);
    }

    public int getTextureID() {
        return pageAtlas.getColorTextureId();
    }
}
