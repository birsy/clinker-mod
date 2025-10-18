package birsy.clinker.common.page.elements;

import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.PageElementTransform;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.core.registry.ClinkerPageElementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ImagePageElement extends PageElement {
    public static final MapCodec<ImagePageElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(element -> element.texture),
                    Codec.DOUBLE.listOf(4, 4).optionalFieldOf("uv_coordinates", List.of(0.0, 0.0, 1.0, 1.0)).forGetter(element -> List.of(element.u1, element.v1, element.u2, element.v2)),
                    PageElementTransform.CODEC.fieldOf("transform").forGetter(element -> element.transform)
            ).apply(instance, ImagePageElement::new)
    );

    final ResourceLocation texture;
    final double u1, v1, u2, v2;

    public ImagePageElement(ResourceLocation texture, List<Double> textureCoordinates, PageElementTransform transform) {
        super(transform);
        this.texture = texture;
        this.u1 = textureCoordinates.get(0); this.v1 = textureCoordinates.get(1);
        this.u2 = textureCoordinates.get(2); this.v2 = textureCoordinates.get(3);
    }

    @Override
    public PageElementType<?> type() {
        return ClinkerPageElementTypes.IMAGE.get();
    }
}
