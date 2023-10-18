package birsy.clinker.client.book.formatting;

import birsy.clinker.client.gui.GuiHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class PageElement {
    float x, y;

    public abstract void render(PoseStack stack, float partialTicks);
    public void tick() {}

    public static void drawRect(PoseStack pPoseStack, float x, float y, float width, float height, int packedOverlay, int packedLight, ResourceLocation texture, float uOffset, float vOffset, float textureX, float textureY) {
        drawRect(pPoseStack, x, y, width, height, 0, 1.0F, 1.0F, 1.0F, packedOverlay, packedLight, texture, uOffset, vOffset, textureX, textureY);
    }
    public static void drawRect(PoseStack pPoseStack, float x, float y, float width, float height, float zOffset, int packedOverlay, int packedLight, ResourceLocation texture, float uOffset, float vOffset, float textureX, float textureY) {
        drawRect(pPoseStack, x, y, width, height, zOffset, 1.0F, 1.0F, 1.0F, packedOverlay, packedLight, texture, uOffset, vOffset, textureX, textureY);
    }

    public static void drawRect(PoseStack pPoseStack, float x, float y, float width, float height, float offset, float r, float g, float b, int packedOverlay, int packedLight, ResourceLocation texture, float uOffset, float vOffset, float textureX, float textureY) {
        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Matrix4f pMatrix = pPoseStack.last().pose();
        Matrix3f pMatrix3 = pPoseStack.last().normal();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        
        float u1 = (uOffset) / textureX;
        float u2 = (uOffset + width) / textureX;
        float v1 = (vOffset) / textureY;
        float v2 = (vOffset + height) / textureY;

        bufferbuilder.vertex(pMatrix, x, y + height, offset)
                .color(r, g, b, 1.0F)
                .uv(u1, v2)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pMatrix3, 0, 0, 1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x + width, y + height, offset)
                .color(r, g, b, 1.0F)
                .uv(u2, v2)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pMatrix3, 0, 0, 1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x + width, y, offset)
                .color(r, g, b, 1.0F)
                .uv(u2, v1)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pMatrix3, 0, 0, 1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x, y, offset)
                .color(r, g, b, 1.0F)
                .uv(u1, v1)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pMatrix3, 0, 0, 1)
                .endVertex();
        
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
