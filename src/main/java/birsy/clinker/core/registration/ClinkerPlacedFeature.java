package birsy.clinker.core.registration;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record ClinkerPlacedFeature
        (ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, List<PlacementModifier> placement)
        implements DataValue<PlacedFeature> {
    public static ClinkerPlacedFeature create(String name, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, PlacementModifier... placementModifiers) {
        ResourceKey key = ResourceKey.create(Registries.PLACED_FEATURE, Clinker.resource(name));
        return new ClinkerPlacedFeature(key, configuredFeatureKey, List.of(placementModifiers));
    }

    @Override
    public PlacedFeature create(BootstrapContext<PlacedFeature> context) {
        return new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredFeatureKey()), placement());
    }
}
