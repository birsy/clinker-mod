package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.world.level.gen.system.biome.BiomeGenerationInfo;
import birsy.clinker.common.world.level.gen.system.biome.placement.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSpawnSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.WorldFeatureCapability;
import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.ordnance.OrdnanceModifierType;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerRegistries {
    public static final ResourceKey<Registry<OrdnanceModifierType<?>>> ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("ordnance_modifier_type"));
    public static final Registry<OrdnanceModifierType<?>> ORDNANCE_MODIFIER_TYPE_REGISTRY =
            new RegistryBuilder<>(ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY)
                    .sync(true)
                    .withIntrusiveHolders() // todo: figure out a better way of doing this
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

    public static final ResourceKey<Registry<OthershoreWeather.Type<?>>> OTHERSHORE_WEATHER_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("othershore_weather_type"));
    public static final Registry<OthershoreWeather.Type<?>> OTHERSHORE_WEATHER_TYPE_REGISTRY =
            new RegistryBuilder<>(OTHERSHORE_WEATHER_TYPE_REGISTRY_KEY)
                    .sync(true)
                    .create();

    // world gen registries
    public static final ResourceKey<Registry<WorldFeatureType<?>>> WORLD_FEATURE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("world_feature"));
    public static final Registry<WorldFeatureType<?>> WORLD_FEATURE_REGISTRY = new RegistryBuilder<>(WORLD_FEATURE_REGISTRY_KEY)
            .sync(false)
            .create();
    public static final ResourceKey<Registry<WorldFeatureSpawnSet>> WORLD_FEATURE_SPAWN_SET_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("world_feature_spawn_set"));
    public static final Registry<WorldFeatureSpawnSet> WORLD_FEATURE_SPAWN_SET_REGISTRY = new RegistryBuilder<>(WORLD_FEATURE_SPAWN_SET_REGISTRY_KEY)
            .sync(false)
            .create();
    public static final ResourceKey<Registry<Class<? extends WorldFeatureCapability>>> WORLD_FEATURE_CAPABILITY_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("world_feature_capability"));
    public static final Registry<Class<? extends WorldFeatureCapability>> WORLD_FEATURE_CAPABILITY_REGISTRY = new RegistryBuilder<>(WORLD_FEATURE_CAPABILITY_REGISTRY_KEY)
            .sync(false)
            .create();
    public static final ResourceKey<Registry<ProtoBiome>> PROTO_BIOME_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("proto_biome"));
    public static final Registry<ProtoBiome> PROTO_BIOME_REGISTRY =
            new RegistryBuilder<>(PROTO_BIOME_REGISTRY_KEY)
                    .sync(false)
                    .callback(ProtoBiomeCallbacks.INSTANCE)
                    .create();
    static class ProtoBiomeCallbacks implements AddCallback<ProtoBiome> {
        static final ProtoBiomeCallbacks INSTANCE = new ProtoBiomeCallbacks();
        @Override
        public void onAdd(Registry<ProtoBiome> registry, int id, ResourceKey<ProtoBiome> key, ProtoBiome value) {
            value.id = id;
        }
    }


    public static final ResourceKey<Registry<BiomeGenerationInfo>> BIOME_GENERATION_INFO_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("biome_generation_info"));
    public static final Registry<BiomeGenerationInfo> BIOME_GENERATION_INFO_REGISTRY =
            new RegistryBuilder<>(BIOME_GENERATION_INFO_REGISTRY_KEY)
                    .sync(false)
                    .callback(BiomeGenerationInfoCallback.INSTANCE)
                    .create();
    static class BiomeGenerationInfoCallback implements BakeCallback<BiomeGenerationInfo> {
        static final BiomeGenerationInfoCallback INSTANCE = new BiomeGenerationInfoCallback();
        @Override public void onBake(Registry<BiomeGenerationInfo> registry) {
            BiomeGenerationInfo.bakeFromRegistry(registry);
        }
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(PAGE_ELEMENT_TYPE_REGISTRY);
        event.register(ALCHEMY_KNOWLEDGE_TYPE_REGISTRY);
        event.register(ORDNANCE_MODIFIER_TYPE_REGISTRY);
        event.register(OTHERSHORE_WEATHER_TYPE_REGISTRY);

        event.register(WORLD_FEATURE_CAPABILITY_REGISTRY);
        event.register(WORLD_FEATURE_REGISTRY);
        event.register(WORLD_FEATURE_SPAWN_SET_REGISTRY);
        event.register(BIOME_GENERATION_INFO_REGISTRY);
        event.register(PROTO_BIOME_REGISTRY);
    }
}
