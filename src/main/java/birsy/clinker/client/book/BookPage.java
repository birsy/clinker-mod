package birsy.clinker.client.book;

import birsy.clinker.client.book.formatting.PageElement;
import birsy.clinker.client.book.formatting.TextBox;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BookPage {
    private static final ResourceLocation PAGE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/book/book_cover.png");
    private List<PageElement> elements = new ArrayList();

    public BookPage() {
        TextBox box = new TextBox();
        //box.initializeText();
    }

    public void render(PoseStack stack, float partialTicks, int packedLight, int packedOverlay) {
        PageElement.drawRect(stack, 0, 0, 47, 63, -0.05f, packedLight, packedOverlay, PAGE_TEXTURE, 0, 0, 64, 64);
        for (PageElement element : this.elements) {
            element.render(stack, partialTicks);
        }
    }
}
