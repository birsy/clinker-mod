package birsy.clinker.client.book.formatting;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;

public class TextBox extends PageElement {
    float lineSpace = 0.0F;
    float textSize = 0.2F;
    int color = 0xFFFFFF;

    private List<FormattedCharSequence> textLines = Collections.emptyList();

    public TextBox(float x, float y, float sizeX, float sizeY, int z) {
        super(x, y, sizeX, sizeY, z);
    }

    public void initializeText(FormattedText text) {
        if (Minecraft.getInstance().font != null) this.textLines = Minecraft.getInstance().font.split(text, Math.round(this.getXPixelSize() / this.textSize));
    }

    @Override
    public void renderElementContents(PoseStack stack, MultiBufferSource source) {
        stack.scale(textSize, textSize, 1.0F);
        for (int i = 0; i < textLines.size(); i++) {
            FormattedCharSequence textLine = textLines.get(i);
            Minecraft.getInstance().font.drawInBatch(
                    textLine,
                    0,
                    (Minecraft.getInstance().font.lineHeight + lineSpace) * i,
                    color,
                    false,
                    stack.last().pose(),
                    source,
                    Font.DisplayMode.NORMAL,
                    0,
                    LightTexture.FULL_BRIGHT
            );
        }
    }
}
