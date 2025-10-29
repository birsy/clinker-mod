package birsy.clinker.core.registry;

import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.elements.ImagePageElement;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.page.elements.TextPageElement;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerPageElementTypes {
    public static final DeferredRegister<PageElementType<?>> PAGE_ELEMENT_TYPES =
            DeferredRegister.create(ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<PageElementType<TextPageElement>> TEXT =
            PAGE_ELEMENT_TYPES.register("text", () -> create(TextPageElement.CODEC, TextPageElement.STREAM_CODEC));
    public static final Supplier<PageElementType<ImagePageElement>> IMAGE =
            PAGE_ELEMENT_TYPES.register("image", () -> create(ImagePageElement.CODEC, ImagePageElement.STREAM_CODEC));

    private static <T extends PageElement> PageElementType<T> create(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new PageElementType<T>() {
            @Override
            public MapCodec<T> codec() { return codec; }
            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }
        };
    }
}
