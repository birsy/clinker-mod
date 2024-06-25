package birsy.clinker.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiElementParent extends Screen {
    protected final List<GuiElement> roots;
    protected final List<GuiElement> leafNodes;
    protected GuiElement hoveredElement = null;
    protected GuiElement clickedElement = null;

    protected GuiElementParent(Component pTitle) {
        super(pTitle);
        this.roots = new ArrayList<>();
        this.leafNodes = new ArrayList<>();

        this.createGuiElements();
        for (GuiElement root : roots) {
            GuiHelper.generateLeafNodes(root, leafNodes);
        }
    }

    protected void updateHoverState(int pMouseX, int pMouseY, float pPartialTick) {
        GuiElement newHoveredElement = GuiHelper.findHoveredElement(leafNodes, pMouseX, pMouseY, pPartialTick);
        if (newHoveredElement != hoveredElement) {
            if (this.hoveredElement != null) hoveredElement.hovered = false;

            hoveredElement = newHoveredElement;

            if (hoveredElement != null) {
                hoveredElement.hovered = true;
                hoveredElement.onHover(pMouseX, pMouseY);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateHoverState(pMouseX, pMouseY, pPartialTick);

        for (GuiElement root : this.roots) {
            root.render(graphics.pose(), pMouseX, pMouseY, pPartialTick);
        }

        // not calling super so we don't get a background
        for(Renderable renderable : this.renderables) {
            renderable.render(graphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.initializeGuiElements();
    }

    @Override
    public void tick() {
        super.tick();
        Vec2 mousePos = GuiHelper.toGuiSpace(this, (float) Minecraft.getInstance().mouseHandler.xpos(), (float) Minecraft.getInstance().mouseHandler.ypos());
        if (hoveredElement != null) hoveredElement.whileHovered(mousePos.x, mousePos.y);
        if (clickedElement != null) clickedElement.whileClicked(mousePos.x, mousePos.y, clickedElement.button);

        for (GuiElement root : roots) {
            root.tick(mousePos.x, mousePos.y);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (hoveredElement != null) {
            hoveredElement.onClick((float) pMouseX, (float) pMouseY, pButton);
            clickedElement = hoveredElement;
            clickedElement.clicked = true;
            clickedElement.button = pButton;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (clickedElement != null) {
            clickedElement.onRelease((float) pMouseX, (float) pMouseY, pButton);
            clickedElement.clicked = false;
            clickedElement = null;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (clickedElement != null) {
            clickedElement.onDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    protected abstract void createGuiElements();

    protected abstract void initializeGuiElements();
}
