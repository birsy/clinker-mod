package birsy.clinker.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiElement<P extends GuiElement, S extends GuiElementParent> {
    private static int NEW_ID;

    protected final int id;
    @Nullable
    public final P parent;
    public final S screen;
    public final List<GuiElement> children;
    public int blitOffset;
    public float x, y, pX, pY, width, height;
    public final boolean interactable;

    public int uOffset, vOffset;
    private ResourceLocation texture;
    protected int textureWidth;
    protected int textureHeight;

    protected boolean hovered = false;
    protected boolean clicked = false;
    protected int button;

    protected int clientTicks = 0;

    protected GuiElement(S screen, float x, float y, float width, float height, boolean interactable) {
        this(screen, null, x, y, width, height, interactable);
    }

    protected GuiElement(S screen, @Nullable P parent, float x, float y, float width, float height, boolean interactable) {
        this.screen = screen;
        id = NEW_ID++;

        this.parent = parent;
        this.children = new ArrayList<>();

        if (this.parent != null) {
            this.parent.children.add(this);
            this.blitOffset = this.parent.blitOffset + 1;
        } else {
            this.blitOffset = 0;
        }

        this.x = x;
        this.pX = x;
        this.y = y;
        this.pY = y;
        this.width = width;
        this.height = height;

        this.interactable = interactable;
    }

    public void tick(float mouseX, float mouseY) {
        this.pX = this.x;
        this.pY = this.y;

        clientTicks++;
        for (GuiElement child : this.children) {
            child.tick(mouseX, mouseY);
        }
    }

    public void onClick(float mouseX, float mouseY, int pButton) {}

    public void whileClicked(float mouseX, float mouseY, int pButton) {}

    public void onDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {}

    public void onRelease(float mouseX, float mouseY, int pButton) {}


    public void onHover(float mouseX, float mouseY) {}

    public void whileHovered(float mouseX, float mouseY) {}


    public float getScreenX(float partialTick) {
        float screenX = Mth.lerp(partialTick, x, pX);
        if (this.parent != null) {
            screenX += this.parent.getScreenX(partialTick);
        }
        return screenX;
    }

    public float getScreenY(float partialTick) {
        float screenY = Mth.lerp(partialTick, y, pY);
        if (this.parent != null) {
            screenY += this.parent.getScreenY(partialTick);
        }
        return screenY;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        pPoseStack.pushPose();
        pPoseStack.translate(Mth.lerp(pPartialTick, x, pX), Mth.lerp(pPartialTick, y, pY), 0);
        GuiHelper.blitOffset = -this.blitOffset;

        //this.renderDebug(pPoseStack);
        this.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);

        for (GuiElement child : children) {
            child.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
        pPoseStack.popPose();
    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiHelper.blit(pPoseStack, 0, 0, this.uOffset, this.vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public void renderDebug(PoseStack pPoseStack) {
        Matrix4f pMatrix = pPoseStack.last().pose();
        float brightness = this.hovered ? 1 : 0.5F;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pMatrix, 0, this.height, this.blitOffset).color(brightness, brightness, brightness, 0.5F).endVertex();
        bufferbuilder.vertex(pMatrix, this.width, this.height, this.blitOffset).color(brightness, brightness, brightness, 0.5F).endVertex();
        bufferbuilder.vertex(pMatrix, this.width, 0, this.blitOffset).color(brightness, brightness, brightness, 0.5F).endVertex();
        bufferbuilder.vertex(pMatrix, 0, 0, this.blitOffset).color(brightness, brightness, brightness, 0.5F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public void setTexture(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
    }
}
