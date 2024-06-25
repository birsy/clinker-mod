package birsy.clinker.client.book.formatting;

import birsy.clinker.client.book.Page;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public abstract class PageElement {
    public Page page;
    float x, y;
    int z;
    float sizeX, sizeY;
    float rotation;

    protected PageElement(float x, float y, float sizeX, float sizeY, int z) {
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.z = z;
    }

    public void render(PoseStack stack, MultiBufferSource source, float partialTicks) {
        stack.pushPose();
        stack.translate(this.getXPixelPosition(), this.getYPixelPosition(), 0);
        stack.mulPose(new Quaternionf().rotateZ(this.rotation));
        this.renderElementContents(stack, source, partialTicks);
        stack.popPose();
    }

    public abstract void renderElementContents(PoseStack stack, MultiBufferSource source, float partialTicks);
    public void tick() {}

    public float getXPixelPosition() {
        return this.x;
    }
    public float getYPixelPosition() {
        return this.y;
    }

    public float getXPixelSize() {
        return this.sizeX;
    }
    public float getYPixelSize() {
        return this.sizeY;
    }

    public int getZ() {
        return this.z;
    }
    public void setZ(int z) {
        this.z = z;
        if (this.page != null) this.page.markNeedsLayerRegen();
    }


    public void renderLayout(PoseStack stack, MultiBufferSource source) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Matrix4f pMatrix = stack.last().pose();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float x0 = this.getXPixelPosition();
        float x1 = x0 + this.getXPixelSize();
        float y0 = this.getYPixelPosition();
        float y1 = y0 + this.getYPixelSize();

        float r = 1.0F, g = 1.0F, b = 1.0F, a = 0.25F;

        bufferbuilder.vertex(pMatrix, x0, y1, 0)
                .color(r, g, b, a)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x1, y1, 0)
                .color(r, g, b, a)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x1, y0, 0)
                .color(r, g, b, a)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x0, y0, 0)
                .color(r, g, b, a)
                .endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
