package birsy.clinker.common.page.elements;

import birsy.clinker.client.resource.localization.LabeledString;
import birsy.clinker.client.resource.localization.LongStringLocalizationAuthority;
import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.PageElementTransform;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerPageElementTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.*;

public class TextPageElement extends PageElement {
    public static final MapCodec<TextPageElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("text").forGetter(element -> element.text),
                    Codec.FLOAT.optionalFieldOf("text_size", 1.0F).forGetter(element -> element.textSize),
                    Codec.FLOAT.optionalFieldOf("text_wiggle", 0.0F).forGetter(element -> element.textWiggle),
                    Codec.FLOAT.optionalFieldOf("line_height_multiplier", 1.0F).forGetter(element -> element.lineHeightMultiplier),
                    Codec.BOOL.optionalFieldOf("blended", true).forGetter(element -> element.blended),
                    PageElementTransform.CODEC.fieldOf("transform").forGetter(element -> element.transform)
            ).apply(instance, TextPageElement::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TextPageElement> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, element -> element.text,
            ByteBufCodecs.FLOAT, element -> element.textSize,
            ByteBufCodecs.FLOAT, element -> element.textWiggle,
            ByteBufCodecs.FLOAT, element -> element.lineHeightMultiplier,
            ByteBufCodecs.BOOL, element -> element.blended,
            PageElementTransform.STREAM_CODEC, element -> element.transform,
            TextPageElement::new
    );

    private static final int[][] BLENDED_OFFSETS = {{-1, 0}, {1, 0}, {0, 1}};

    final ResourceLocation text;
    final float textSize, textWiggle, lineHeightMultiplier;
    final boolean blended;
    public List<FormattedCharSequence> formattedText;

    public TextPageElement(ResourceLocation text, float textSize, float textWiggle, float lineHeightMultiplier, boolean blended, PageElementTransform transform) {
        super(transform);
        this.text = text;
        this.textSize = textSize;
        this.textWiggle = textWiggle;
        this.lineHeightMultiplier = lineHeightMultiplier;
        this.blended = blended;
    }

    public void resolveFormattedText() {
        LabeledString labeledText = LongStringLocalizationAuthority.get().getLabelledLongString(this.text);

        ImmutableList<LabeledString.Label> labels = labeledText.labels();
        Style currentStyle = Style.EMPTY.withColor(0);

        // if there's no formatting labels, we're done here
        if (labels.isEmpty()) {
            this.formattedText = Minecraft.getInstance().font.split(
                    FormattedText.of(labeledText.text(), currentStyle),
                    Math.round(this.transform.width() / this.textSize)
            );
            return;
        }

        // otherwise, we're going to loop through all the labels and apply style changes accordingly.
        List<FormattedText> formattedTextSegments = new ArrayList<>();
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

        this.formattedText = Minecraft.getInstance().font.split(
                FormattedText.composite(formattedTextSegments),
                Math.round(this.transform.width() / this.textSize)
        );
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

    @Override
    public void drawToAtlas(MultiBufferSource bufferSource, Matrix4f matrix, int atlasOffsetX, int atlasOffsetY) {
        Clinker.FONT_WIGGLINESS = this.textWiggle;
        this.resolveFormattedText();
        float halfWidth  = this.transform.width() * 0.5F,
              halfHeight = this.transform.height() * 0.5F;

        matrix.identity();
        matrix.translate(atlasOffsetX, atlasOffsetY, 0);
        matrix.translate(this.transform.x() + halfWidth, this.transform.y() + halfHeight, this.transform.renderOrder());
        matrix.rotateYXZ(0, 0, (this.transform.rotation()) * Mth.DEG_TO_RAD);
        matrix.translate(-halfWidth, -halfHeight, 0);
        matrix.scale(this.textSize);

        List<FormattedCharSequence> text = this.formattedText;
        float lineHeight = Minecraft.getInstance().font.lineHeight * this.lineHeightMultiplier;
        for (int i = 0; i < text.size(); i++) {
            FormattedCharSequence string = text.get(i);
            Minecraft.getInstance().font.drawInBatch(
                    string,
                    0, lineHeight * i,
                    0xFFFFFFFF,
                    false,
                    matrix,
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    LightTexture.FULL_BRIGHT
            );
            if (blended) {
                for (int[] blendedOffset : BLENDED_OFFSETS) {
                    Minecraft.getInstance().font.drawInBatch(
                            string,
                            blendedOffset[0], lineHeight * i + blendedOffset[1],
                            FastColor.ARGB32.colorFromFloat(0.2F, 1, 1, 1),
                            false,
                            matrix,
                            bufferSource,
                            Font.DisplayMode.SEE_THROUGH,
                            0,
                            LightTexture.FULL_BRIGHT
                    );
                }
            }
        }

        Clinker.FONT_WIGGLINESS = 0.0F;
    }
}
