package birsy.clinker.common.page;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PageElementTransform(double x, double y, double width, double height, double rotation, int renderOrder) {
    public static final Codec<PageElementTransform> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.DOUBLE.fieldOf("x").forGetter(element -> element.x),
                    Codec.DOUBLE.fieldOf("y").forGetter(element -> element.y),
                    Codec.DOUBLE.fieldOf("width").forGetter(element -> element.width),
                    Codec.DOUBLE.fieldOf("height").forGetter(element -> element.height),
                    Codec.DOUBLE.optionalFieldOf("rotation", 0.0).forGetter(element -> element.rotation),
                    Codec.INT.optionalFieldOf("render_order", 0).forGetter(element -> element.renderOrder)
            ).apply(instance, PageElementTransform::new)
    );
}
