package birsy.clinker.client.book;

import birsy.clinker.client.book.formatting.ImageBox;
import birsy.clinker.client.book.formatting.TextBox;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Matrix4f;


@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class TestPageRenderer {
    public static PageAtlas pageAtlas;
    public static Page page;
    static {
        regeneratePage();
    }

    @SubscribeEvent
    public static void renderGui(RenderGuiEvent.Post guiEvent) {

    }

    @SubscribeEvent
    public static void render(RenderFrameEvent.Post event) {
        if (pageAtlas == null) pageAtlas = new PageAtlas();
        pageAtlas.draw();
    }

    @SubscribeEvent
    public static void input(InputEvent.Key inputEvent) {
        if (inputEvent.getKey() == InputConstants.KEY_R) {
            //regeneratePage();
        }
    }


    private static void regeneratePage() {
        if (Minecraft.getInstance() == null) return;
        page = new Page();
        TextBox box = new TextBox(1, 1, 45, 30, 1);
        box.initializeText(FormattedText.composite(
                FormattedText.of("multiple styles, ", Style.EMPTY.withFont(Clinker.resource("alchemical")).withBold(true).withColor(ChatFormatting.DARK_AQUA)),
                FormattedText.of("Multiple styles!, ", Style.EMPTY.withFont(Clinker.resource("small")).withItalic(true).withColor(ChatFormatting.DARK_GRAY)),
                FormattedText.of("multiple styles... ", Style.EMPTY.withFont(Clinker.resource("alchemical")).withColor(0)),
                FormattedText.of("oooh. how very delightful!", Style.EMPTY.withColor(ChatFormatting.BLUE))
        ));
        page.addElement(box);

        TextBox box2 = new TextBox(1, 23, 20, 20, 1);
        box2.initializeText(FormattedText.composite(
                FormattedText.of("ah. there it is!",
                        Style.EMPTY.withFont(Clinker.resource("small")).withColor(0x2b2722).withItalic(true))
                ));
        page.addElement(box2);

        ImageBox imageBox = new ImageBox(5, 25, 40, 40, 1);
        page.addElement(imageBox);

        //page.addElement(new TextBox(0.5F, 0.5F, 0.5f - 0.02F, 0.5f - 0.02F, 1));

    }
}
