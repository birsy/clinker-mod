package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerRegistries {
    public static final ResourceKey<Registry<WorldFeatureType>> WORLD_FEATURE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("world_feature"));
    public static final Registry<WorldFeatureType> WORLD_FEATURE_REGISTRY = new RegistryBuilder<>(WORLD_FEATURE_REGISTRY_KEY)
            .sync(false)
            .create();

    public static final ResourceKey<Registry<PageElementType<?>>> PAGE_ELEMENT_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("page_element_type"));
    public static final Registry<PageElementType<?>> PAGE_ELEMENT_TYPE_REGISTRY =
            new RegistryBuilder<>(PAGE_ELEMENT_TYPE_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final ResourceKey<Registry<AlchemyKnowledgeType<?>>> ALCHEMY_KNOWLEDGE_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("alchemy_knowledge_type"));
    public static final Registry<AlchemyKnowledgeType<?>> ALCHEMY_KNOWLEDGE_TYPE_REGISTRY =
            new RegistryBuilder<>(ALCHEMY_KNOWLEDGE_TYPE_REGISTRY_KEY)
                    .sync(true)
                    .create();

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(WORLD_FEATURE_REGISTRY);
        event.register(PAGE_ELEMENT_TYPE_REGISTRY);
        event.register(ALCHEMY_KNOWLEDGE_TYPE_REGISTRY);
    }
}
