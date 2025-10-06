package birsy.clinker.core.registry;

import birsy.clinker.common.world.level.gen.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
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

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(WORLD_FEATURE_REGISTRY);
    }
}
