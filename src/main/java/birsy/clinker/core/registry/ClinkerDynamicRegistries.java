package birsy.clinker.core.registry;

import birsy.clinker.common.world.level.gen.worldfeature.WorldFeatureSpawnSet;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClinkerDynamicRegistries {
    public static final ResourceKey<Registry<WorldFeatureSpawnSet>> WORLD_FEATURE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("worldgen/world_feature"));
    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                WORLD_FEATURE_REGISTRY_KEY,
                WorldFeatureSpawnSet.CODEC
        );
    }
}
