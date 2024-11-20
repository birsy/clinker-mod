package birsy.clinker.client.book.formatting;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ImageBox extends PageElement {
    private ResourceLocation location = TextureManager.INTENTIONAL_MISSING_TEXTURE;
    boolean mirroredX, mirroredY;
    float r, g, b, a;

    public ImageBox(float x, float y, float sizeX, float sizeY, int z) {
        super(x, y, sizeX, sizeY, z);
        this.r = 1.0F;
        this.g = 1.0F;
        this.b = 1.0F;
        this.a = 1.0F;
        this.rotation = 0.1F;
    }

    @Override
    public void renderElementContents(PoseStack stack, MultiBufferSource source) {
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Matrix4f pMatrix = stack.last().pose();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        float x0 = 0;
        float x1 = this.getXPixelSize();
        float y0 = 0;
        float y1 = this.getYPixelSize();

        float u0 = mirroredX ? 1.0F : 0.0F;
        float u1 = mirroredX ? 0.0F : 1.0F;
        float v0 = mirroredY ? 1.0F : 0.0F;
        float v1 = mirroredY ? 0.0F : 1.0F;

        bufferbuilder.vertex(pMatrix, x0, y1, 0)
                .color(this.r, this.g, this.b, this.a)
                .uv(u0, v1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x1, y1, 0)
                .color(this.r, this.g, this.b, this.a)
                .uv(u1, v1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x1, y0, 0)
                .color(this.r, this.g, this.b, this.a)
                .uv(u1, v0)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x0, y0, 0)
                .color(this.r, this.g, this.b, this.a)
                .uv(u0, v0)
                .endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
