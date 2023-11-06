package birsy.clinker.client.book.formatting;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;

public class TextBox extends PageElement {
    float width;
    protected Font font;
    float lineSpace;

    private List<FormattedCharSequence> textLines = Collections.emptyList();


    public void initializeText(FormattedText text) {
        this.textLines = this.font.split(text, Math.round(this.width));
    }

    @Override
    public void render(PoseStack stack, float partialTicks) {
        for (int i = 0; i < textLines.size(); i++) {
            FormattedCharSequence formattedcharsequence = this.textLines.get(i);
            //this.font.drawInBatch(stack, formattedcharsequence, x, y + i * (this.font.lineHeight + lineSpace), 0);
        }
    }
}
