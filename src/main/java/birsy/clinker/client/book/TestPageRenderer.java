package birsy.clinker.client.book;

import birsy.clinker.client.book.formatting.ImageBox;
import birsy.clinker.client.book.formatting.PageElement;
import birsy.clinker.client.book.formatting.TextBox;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class TestPageRenderer {
    public static PageAtlasRenderer pageAtlasRenderer;
    public static Page page;
    static {
        regeneratePage();
    }

    @SubscribeEvent
    public static void renderGui(RenderGuiEvent.Post guiEvent) {
        PoseStack stack = new PoseStack();
        stack.pushPose();
        //stack.translate(guiEvent.getWindow().getGuiScaledWidth() * 0.5F, guiEvent.getWindow().getGuiScaledHeight() * 0.5F, 0.0F);
        //stack.scale(5.0F, 5.0F, 1.0F);
        //stack.translate(page.getSizeX() * -0.5F, page.getSizeY() * -0.5F, 0.0F);
        drawAtlas(stack);
        //page.render(stack, guiEvent.getGuiGraphics().bufferSource());
        stack.popPose();
    }

    @SubscribeEvent
    public static void input(InputEvent.Key inputEvent) {
        if (inputEvent.getKey() == InputConstants.KEY_R) {
            regeneratePage();
        }
    }

    public static void drawAtlas(PoseStack stack) {
        if (pageAtlasRenderer == null) {
            pageAtlasRenderer = new PageAtlasRenderer(1, 1);
        }
        pageAtlasRenderer.pages.clear();
        pageAtlasRenderer.pages.add(page);
        pageAtlasRenderer.draw(Minecraft.getInstance().renderBuffers().bufferSource());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, pageAtlasRenderer.getTextureID());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Matrix4f pMatrix = stack.last().pose();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float x0 = 0;
        float x1 = 384;
        float y0 = 0;
        float y1 = 384;

        bufferbuilder.vertex(pMatrix, x0, y1, 0)
                .uv(0, 1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x1, y1, 0)
                .uv(1, 1)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x1, y0, 0)
                .uv(1, 0)
                .endVertex();
        bufferbuilder.vertex(pMatrix, x0, y0, 0)
                .uv(0, 0)
                .endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    private static void regeneratePage() {
        page = new Page();
        TextBox box = new TextBox(1, 1, 45, 30, 1);
        box.initializeText(FormattedText.composite(
                FormattedText.of("multiple styles, ", Style.EMPTY.withFont(new ResourceLocation(Clinker.MOD_ID, "alchemical")).withBold(true).withColor(ChatFormatting.DARK_AQUA)),
                FormattedText.of("Multiple styles!, ", Style.EMPTY.withFont(new ResourceLocation(Clinker.MOD_ID, "small")).withItalic(true).withColor(ChatFormatting.DARK_GRAY)),
                FormattedText.of("multiple styles... ", Style.EMPTY.withFont(new ResourceLocation(Clinker.MOD_ID, "alchemical")).withColor(0)),
                FormattedText.of("oooh. how very delightful!", Style.EMPTY.withColor(ChatFormatting.BLUE))
        ));
        page.addElement(box);

        TextBox box2 = new TextBox(1, 23, 20, 20, 1);
        box2.initializeText(FormattedText.composite(
                FormattedText.of("ah. there it is!",
                        Style.EMPTY.withFont(new ResourceLocation(Clinker.MOD_ID, "small")).withColor(0x2b2722).withItalic(true))
                ));
        page.addElement(box2);

        ImageBox imageBox = new ImageBox(5, 25, 40, 40, 1);
        page.addElement(imageBox);

        //page.addElement(new TextBox(0.5F, 0.5F, 0.5f - 0.02F, 0.5f - 0.02F, 1));

    }
}
