package birsy.clinker.common.page;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PageElementTransform(float x, float y, float width, float height, float rotation, int renderOrder) {
    public static final Codec<PageElementTransform> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(element -> element.x),
                    Codec.FLOAT.fieldOf("y").forGetter(element -> element.y),
                    Codec.FLOAT.fieldOf("width").forGetter(element -> element.width),
                    Codec.FLOAT.fieldOf("height").forGetter(element -> element.height),
                    Codec.FLOAT.optionalFieldOf("rotation", 0.0F).forGetter(element -> element.rotation),
                    Codec.INT.optionalFieldOf("render_order", 0).forGetter(element -> element.renderOrder)
            ).apply(instance, PageElementTransform::new)
    );
}
