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

import java.util.function.Function;

public record ClinkerConfiguredFeature<F extends Feature<C>, C extends FeatureConfiguration>
        (ResourceKey<ConfiguredFeature<?,?>> genericKey, ResourceKey<ConfiguredFeature<C, F>> key, Function<BootstrapContext<ConfiguredFeature<C, F>>, ConfiguredFeature<?, ?>> valueFunction)
         implements DataValue<ConfiguredFeature<C, F>> {

    public static <F extends Feature<C>, C extends FeatureConfiguration> ClinkerConfiguredFeature<F, C> create(String name, F feature, C config) {
        ResourceKey key = ResourceKey.create(Registries.CONFIGURED_FEATURE, Clinker.resource(name));
        ConfiguredFeature<C, F> configuredFeature = new ConfiguredFeature<>(feature, config);
        return new ClinkerConfiguredFeature<>(key, key, context -> configuredFeature);
    }

    public static <F extends Feature<C>, C extends FeatureConfiguration> ClinkerConfiguredFeature<F, C> create(String name, Function<BootstrapContext<ConfiguredFeature<C, F>>, ConfiguredFeature<?, ?>> valueFunction) {
        ResourceKey key = ResourceKey.create(Registries.CONFIGURED_FEATURE, Clinker.resource(name));
        return new ClinkerConfiguredFeature<>(key, key, valueFunction);
    }

    @Override
    public ConfiguredFeature<C, F> create(BootstrapContext<ConfiguredFeature<C, F>> context) {
        return (ConfiguredFeature<C, F>) valueFunction.apply(context);
    }
}
