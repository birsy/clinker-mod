package birsy.clinker.common.page;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public abstract class PageElement {
    public static final Codec<PageElement> CODEC = ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY
            .byNameCodec()
            .dispatch(PageElement::type, PageElementType::codec);

    public final PageElementTransform transform;
    public PageElement(PageElementTransform transform) {
        this.transform = transform;
    }

    public abstract PageElementType<?> type();

    public abstract void drawToAtlas(MultiBufferSource bufferSource, Matrix4f matrix, int atlasOffsetX, int atlasOffsetY);
}
