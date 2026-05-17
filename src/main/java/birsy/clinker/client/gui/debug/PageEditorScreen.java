package birsy.clinker.client.gui.debug;

import foundry.imgui.api.ImGuiMCEvents;
import imgui.ImGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PageEditorScreen extends Screen {
    private static boolean LOADED = false;
    private static PageEditorScreen INSTANCE;

    public PageEditorScreen() {
        super(Component.literal("page editor"));
        INSTANCE = this;
        if (!LOADED) ImGuiMCEvents.INSTANCE.preRenderImGuiEvents(() -> INSTANCE.renderImgui());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    void renderImgui() {
        ImGui.showDemoWindow();
    }
}
