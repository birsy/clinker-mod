package birsy.clinker.client.book;

import birsy.clinker.client.book.formatting.PageElement;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Page {
    public final float xSize, ySize;
    private final ResourceLocation texture;
    private List<PageElement> elements = new ArrayList();
    private boolean needsLayerRegeneration = true;

    public Page(ResourceLocation texture, float xSize, float ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.texture = texture;
    }

    public Page() {
        this.xSize = 47;
        this.ySize = 63;
        this.texture = Clinker.resource("textures/book/book_cover.png");
    }

    public void render(PoseStack stack, MultiBufferSource source) {
        if (this.needsLayerRegeneration) this.sortElementsByZIndex();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Matrix4f pMatrix = stack.last().pose();

        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float x0 = 0;
        float x1 = this.xSize;
        float y0 = 0;
        float y1 = this.ySize;

        float r = 199F/255F, g = 189F/255F, b = 155F/255F;

        bufferbuilder.addVertex(pMatrix, x0, y1, 0)
                .setColor(r, g, b, 1.0F);
        bufferbuilder.addVertex(pMatrix, x1, y1, 0)
                .setColor(r, g, b, 1.0F);
        bufferbuilder.addVertex(pMatrix, x1, y0, 0)
                .setColor(r, g, b, 1.0F);
        bufferbuilder.addVertex(pMatrix, x0, y0, 0)
                .setColor(r, g, b, 1.0F);

        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        for (PageElement element : this.elements) {
            element.render(stack, source);
        }
    }

    public void renderLayout(PoseStack stack, MultiBufferSource source) {
        if (this.needsLayerRegeneration) this.sortElementsByZIndex();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Matrix4f pMatrix = stack.last().pose();

        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float x0 = 0;
        float x1 = x0 + this.xSize;
        float y0 = 0;
        float y1 = y0 + this.ySize;

        float r = 0.1F, g = 0.1F, b = 0.1F;

        bufferbuilder.addVertex(pMatrix, x0, y1, 0)
                .setColor(r, g, b, 1.0F);
        bufferbuilder.addVertex(pMatrix, x1, y1, 0)
                .setColor(r, g, b, 1.0F);
        bufferbuilder.addVertex(pMatrix, x1, y0, 0)
                .setColor(r, g, b, 1.0F);
        bufferbuilder.addVertex(pMatrix, x0, y0, 0)
                .setColor(r, g, b, 1.0F);

        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        for (PageElement element : this.elements) {
            element.renderLayout(stack, source);
        }
    }

    public float getSizeX() {
        return this.xSize;
    }
    public float getSizeY() {
        return this.ySize;
    }

    public void tick() {
        for (PageElement element : this.elements) {
            element.tick();
        }
    }

    public void sortElementsByZIndex() {
        this.elements.sort(Comparator.comparingInt(PageElement::getZ));
        this.needsLayerRegeneration = false;
    }

    public void markNeedsLayerRegen() {
        this.needsLayerRegeneration = true;
    }

    public void addElement(PageElement pageElement) {
        this.elements.add(pageElement);
        pageElement.page = this;
        this.markNeedsLayerRegen();
    }
}
