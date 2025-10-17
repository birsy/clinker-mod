package birsy.clinker.common.page.elements;

import birsy.clinker.client.localization.LabeledString;
import birsy.clinker.client.localization.LocalizationAuthority;
import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.core.registry.ClinkerPageElementTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;

public class TextPageElement extends PageElement {
    public static final MapCodec<TextPageElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("text").forGetter(element -> element.text),
                    Codec.DOUBLE.fieldOf("text_size").orElse(1.0).forGetter(element -> element.x),
                    Codec.DOUBLE.fieldOf("x").forGetter(element -> element.x),
                    Codec.DOUBLE.fieldOf("y").forGetter(element -> element.y),
                    Codec.DOUBLE.fieldOf("width").forGetter(element -> element.width),
                    Codec.DOUBLE.fieldOf("height").forGetter(element -> element.height),
                    Codec.DOUBLE.fieldOf("rotation").orElse(0.0).forGetter(element -> element.rotation),
                    Codec.INT.fieldOf("render_order").orElse(0).forGetter(element -> element.renderOrder)
            ).apply(instance, TextPageElement::new)
    );

    final ResourceLocation text;
    final double textSize;
    private List<FormattedCharSequence> formattedText;

    public TextPageElement(ResourceLocation text, double textSize,
                           double x, double y,
                           double width, double height,
                           double rotation, int renderOrder) {
        super(x, y, width, height, rotation, renderOrder);
        this.text = text;
        this.textSize = textSize;
    }

    // todo: parsing codes and such idk
    private void resolveFormattedText() {
        LabeledString labeledText = LocalizationAuthority.get().getLabelledLongString(this.text);

        ImmutableList<LabeledString.Label> labels = labeledText.labels();

        // if there's no formatting labels, we're done here
        if (labels.isEmpty()) {
            this.formattedText = Minecraft.getInstance().font.split(FormattedText.of(labeledText.text()), (int) Math.round(this.width / this.textSize));
            return;
        }

        // otherwise, we're going to loop through all the labels and apply style changes accordingly.
        List<FormattedText> formattedTextSegments = new ArrayList<>();
        Style currentStyle = Style.EMPTY;
        Deque<Integer> colorStack = new ArrayDeque<>();
        Deque<ResourceLocation> fontStack = new ArrayDeque<>();

        int textSegmentIndex = 0;
        for (LabeledString.Label label : labels) {
            // add text segment before the next label
            String segment = labeledText.text().substring(textSegmentIndex, label.index());
            if (!segment.isEmpty())
                formattedTextSegments.add(FormattedText.of(segment, currentStyle));

            textSegmentIndex = label.index();

            // parsing the label and update the style for the next text segment
            String id = label.identifier();
            boolean isClosing = id.startsWith("/");
            String tag = isClosing ? id.substring(1) : id;
            currentStyle = applyTag(currentStyle, tag, isClosing, colorStack, fontStack);
        }

        // add the remaining bits of formatted text, if it exists
        String currentSubstring = labeledText.text().substring(textSegmentIndex);
        if (!currentSubstring.isEmpty())
            formattedTextSegments.add(FormattedText.of(currentSubstring, currentStyle));

        this.formattedText = Minecraft.getInstance().font.split(FormattedText.composite(formattedTextSegments), (int) Math.round(this.width / this.textSize));
    }

    private static Style applyTag(Style style, String tag, boolean isClosing, Deque<Integer> colorStack, Deque<ResourceLocation> fontStack) {
        switch (tag) {
            case "b" -> style = style.withBold(!isClosing);
            case "i" -> style = style.withItalic(!isClosing);
            case "u" -> style = style.withUnderlined(!isClosing);
            case "s" -> style = style.withStrikethrough(!isClosing);

            default -> {
                if (tag.startsWith("color")) {
                    if (!isClosing) {
                        int color = Integer.parseInt(tag.substring("color=".length()), 16);
                        colorStack.push(color);
                        style = style.withColor(color);
                    } else if (!colorStack.isEmpty()) {
                        colorStack.pop();
                        style = style.withColor(colorStack.peek());
                    }
                } else if (tag.startsWith("font")) {
                    if (!isClosing) {
                        ResourceLocation font = ResourceLocation.parse(tag.substring("font=".length()));
                        fontStack.push(font);
                        style = style.withFont(font);
                    } else if (!fontStack.isEmpty()) {
                        fontStack.pop();
                        style = style.withFont(fontStack.peek());
                    }
                }
            }
        }

        return style;
    }

    @Override
    public PageElementType<?> type() {
        return ClinkerPageElementTypes.TEXT.get();
    }
}
