package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ClinkerConfiguredFeatures {
    public static final ConfiguredFeature<?, ?> ASH_LAYER = ClinkerFeatures.ASH_LAYER.get().configured(FeatureConfiguration.NONE);

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "ash_layer"), ASH_LAYER);
    }
}
