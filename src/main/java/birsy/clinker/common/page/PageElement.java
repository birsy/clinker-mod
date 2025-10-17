package birsy.clinker.common.page;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;

public abstract class PageElement {
    public static final Codec<PageElement> CODEC = ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY
            .byNameCodec()
            .dispatch(PageElement::type, PageElementType::codec);

    public final int renderOrder;
    public final double x, y, width, height, rotation;

    public PageElement(double x, double y, double width, double height, double rotation, int renderOrder) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.renderOrder = renderOrder;
    }

    public abstract PageElementType<?> type();
}
