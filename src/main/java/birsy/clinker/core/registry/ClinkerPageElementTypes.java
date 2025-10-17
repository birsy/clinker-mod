package birsy.clinker.core.registry;

import birsy.clinker.common.page.elements.ImagePageElement;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.page.elements.TextPageElement;
import birsy.clinker.core.Clinker;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerPageElementTypes {
    public static final DeferredRegister<PageElementType<?>> PAGE_ELEMENT_TYPES =
            DeferredRegister.create(ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<PageElementType<TextPageElement>> TEXT =
            PAGE_ELEMENT_TYPES.register("text", () -> () -> TextPageElement.CODEC);
    public static final Supplier<PageElementType<ImagePageElement>> IMAGE =
            PAGE_ELEMENT_TYPES.register("image", () -> () -> ImagePageElement.CODEC);
}
