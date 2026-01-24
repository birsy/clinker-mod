package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.surface.decorator.BiomeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorators;
import birsy.clinker.common.world.level.gen.system.surface.shaper.BiomeSurfaceShaper;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShapers;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeatureType;
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

    public static final ResourceKey<Registry<NoiseComputer>> NOISE_COMPUTER_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("noise_computer"));
    public static final Registry<NoiseComputer> NOISE_COMPUTER_REGISTRY =
            new RegistryBuilder<>(NOISE_COMPUTER_REGISTRY_KEY)
                    .sync(false)
                    .callback(NoiseComputerCallbacks.INSTANCE)
                    .create();
    static class NoiseComputerCallbacks implements AddCallback<NoiseComputer> {
        static final NoiseComputerCallbacks INSTANCE = new NoiseComputerCallbacks();
        @Override
        public void onAdd(Registry<NoiseComputer> registry, int id, ResourceKey<NoiseComputer> key, NoiseComputer value) {
            value.id = id;
        }
    }

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

    public static final ResourceKey<Registry<BiomeSurfaceDecorator>> SURFACE_DECORATOR_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("surface_decorator"));
    public static final Registry<BiomeSurfaceDecorator> SURFACE_DECORATOR_REGISTRY =
            new RegistryBuilder<>(SURFACE_DECORATOR_REGISTRY_KEY)
                    .sync(false)
                    .callback(SurfaceDecoratorCallbacks.INSTANCE)
                    .create();
    static class SurfaceDecoratorCallbacks implements BakeCallback<BiomeSurfaceDecorator> {
        static final SurfaceDecoratorCallbacks INSTANCE = new SurfaceDecoratorCallbacks();
        @Override
        public void onBake(Registry<BiomeSurfaceDecorator> registry) {
            for (BiomeSurfaceDecorator biomeSurfaceDecorator : registry) {
                if (biomeSurfaceDecorator.biome().left().isPresent()) {
                    SurfaceDecorators.decoratorByBiome.put(
                            biomeSurfaceDecorator.biome().left().get(),
                            biomeSurfaceDecorator.decorator()
                    );
                } else if (biomeSurfaceDecorator.biome().right().isPresent()) {
                    SurfaceDecorators.decoratorByBiomeTag.put(
                            biomeSurfaceDecorator.biome().right().get(),
                            biomeSurfaceDecorator.decorator()
                    );
                }
            }
        }
    }

    public static final ResourceKey<Registry<BiomeSurfaceShaper>> SURFACE_SHAPER_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("surface_shaper"));
    public static final Registry<BiomeSurfaceShaper> SURFACE_SHAPER_REGISTRY =
            new RegistryBuilder<>(SURFACE_SHAPER_REGISTRY_KEY)
                    .sync(false)
                    .callback(SurfaceShaperCallbacks.INSTANCE)
                    .create();
    static class SurfaceShaperCallbacks implements BakeCallback<BiomeSurfaceShaper> {
        static final SurfaceShaperCallbacks INSTANCE = new SurfaceShaperCallbacks();
        @Override
        public void onBake(Registry<BiomeSurfaceShaper> registry) {
            for (BiomeSurfaceShaper biomeSurfaceShaper : registry) {
                if (biomeSurfaceShaper.biome().left().isPresent()) {
                    SurfaceShapers.shaperByBiome.put(
                            biomeSurfaceShaper.biome().left().get(),
                            biomeSurfaceShaper.shaper()
                    );
                } else if (biomeSurfaceShaper.biome().right().isPresent()) {
                    SurfaceShapers.shaperByBiomeTag.put(
                            biomeSurfaceShaper.biome().right().get(),
                            biomeSurfaceShaper.shaper()
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(WORLD_FEATURE_REGISTRY);
        event.register(PAGE_ELEMENT_TYPE_REGISTRY);
        event.register(ALCHEMY_KNOWLEDGE_TYPE_REGISTRY);
        event.register(NOISE_COMPUTER_REGISTRY);
        event.register(SURFACE_DECORATOR_REGISTRY);
        event.register(SURFACE_SHAPER_REGISTRY);
        event.register(PROTO_BIOME_REGISTRY);
    }
}
