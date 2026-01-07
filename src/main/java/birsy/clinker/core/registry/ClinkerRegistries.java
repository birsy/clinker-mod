package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.surface.BiomeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.SurfaceDecorators;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;

import java.util.HashMap;
import java.util.Map;

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

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(WORLD_FEATURE_REGISTRY);
        event.register(PAGE_ELEMENT_TYPE_REGISTRY);
        event.register(ALCHEMY_KNOWLEDGE_TYPE_REGISTRY);
        event.register(NOISE_COMPUTER_REGISTRY);
        event.register(SURFACE_DECORATOR_REGISTRY);
    }
}
