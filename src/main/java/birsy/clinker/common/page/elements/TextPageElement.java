package birsy.clinker.common.page.elements;

import birsy.clinker.client.localization.LabeledString;
import birsy.clinker.client.localization.LongStringLocalizationAuthority;
import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.PageElementTransform;
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
                    Codec.FLOAT.optionalFieldOf("text_size", 1.0F).forGetter(element -> element.textSize),
                    PageElementTransform.CODEC.fieldOf("transform").forGetter(element -> element.transform)
            ).apply(instance, TextPageElement::new)
    );

    final ResourceLocation text;
    final float textSize;
    public List<FormattedCharSequence> formattedText;

    public TextPageElement(ResourceLocation text, float textSize, PageElementTransform transform) {
        super(transform);
        this.text = text;
        this.textSize = textSize;
    }

    public void resolveFormattedText() {
        LabeledString labeledText = LongStringLocalizationAuthority.get().getLabelledLongString(this.text);

        ImmutableList<LabeledString.Label> labels = labeledText.labels();

        // if there's no formatting labels, we're done here
        if (labels.isEmpty()) {
            this.formattedText = Minecraft.getInstance().font.split(FormattedText.of(labeledText.text()), (int) Math.round(this.transform.width() / this.textSize));
            return;
        }

        // otherwise, we're going to loop through all the labels and apply style changes accordingly.
        List<FormattedText> formattedTextSegments = new ArrayList<>();
        Style currentStyle = Style.EMPTY.withColor(0);
        Deque<Integer> colorStack = new ArrayDeque<>();
        colorStack.add(currentStyle.getColor().getValue());
        Deque<ResourceLocation> fontStack = new ArrayDeque<>();
        fontStack.add(Style.DEFAULT_FONT);

        int textSegmentIndex = 0;
        for (LabeledString.Label label : labels) {
            // add text segment before the next label
            String segment = labeledText.text().substring(textSegmentIndex, label.index());
            if (!segment.isEmpty())
                formattedTextSegments.add(FormattedText.of(segment, currentStyle));

            textSegmentIndex = label.index();

            // parsing the label and update the style for the next text segment
            String id = label.identifier().toLowerCase();
            boolean isClosing = id.startsWith("/");
            String tag = isClosing ? id.substring(1) : id;
            currentStyle = applyTag(currentStyle, tag, isClosing, colorStack, fontStack);
        }

        // add the remaining bits of formatted text, if it exists
        String currentSubstring = labeledText.text().substring(textSegmentIndex);
        if (!currentSubstring.isEmpty())
            formattedTextSegments.add(FormattedText.of(currentSubstring, currentStyle));

        this.formattedText = Minecraft.getInstance().font.split(FormattedText.composite(formattedTextSegments), (int) Math.round(this.transform.width() / this.textSize));
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
                    } else if (colorStack.size() > 1) {
                        colorStack.pop();
                        style = style.withColor(colorStack.peek());
                    }
                } else if (tag.startsWith("font")) {
                    if (!isClosing) {
                        ResourceLocation font = ResourceLocation.parse(tag.substring("font=".length()));
                        fontStack.push(font);
                        style = style.withFont(font);
                    } else if (fontStack.size() > 1) {
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
