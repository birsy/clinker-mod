package birsy.clinker.common.page.elements;

import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.core.registry.ClinkerPageElementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class TextPageElement extends PageElement {
    public static final MapCodec<TextPageElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("text").forGetter(element -> element.text),
                    Codec.DOUBLE.fieldOf("x").forGetter(element -> element.x),
                    Codec.DOUBLE.fieldOf("y").forGetter(element -> element.y),
                    Codec.DOUBLE.fieldOf("width").forGetter(element -> element.width),
                    Codec.DOUBLE.fieldOf("height").forGetter(element -> element.height),
                    Codec.DOUBLE.fieldOf("rotation").orElse(0.0).forGetter(element -> element.rotation),
                    Codec.INT.fieldOf("render_order").orElse(0).forGetter(element -> element.renderOrder)
            ).apply(instance, TextPageElement::new)
    );

    final ResourceLocation text;

    public TextPageElement(ResourceLocation text,
                           double x, double y,
                           double width, double height,
                           double rotation, int renderOrder) {
        super(x, y, width, height, rotation, renderOrder);
        this.text = text;
    }

    @Override
    public PageElementType<?> type() {
        return ClinkerPageElementTypes.TEXT.get();
    }
}
