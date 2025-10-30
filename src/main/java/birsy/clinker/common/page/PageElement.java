package birsy.clinker.common.page;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Matrix4f;

public abstract class PageElement {
    public static final Codec<PageElement> CODEC = ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY
            .byNameCodec()
            .dispatch(PageElement::type, PageElementType::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, PageElement> STREAM_CODEC =
            ByteBufCodecs.registry(ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY_KEY)
                         .dispatch(PageElement::type, PageElementType::streamCodec);
    public final PageElementTransform transform;
    public PageElement(PageElementTransform transform) {
        this.transform = transform;
    }

    public abstract PageElementType<?> type();

    public abstract void drawToAtlas(MultiBufferSource bufferSource, Matrix4f matrix, int atlasOffsetX, int atlasOffsetY);
}
