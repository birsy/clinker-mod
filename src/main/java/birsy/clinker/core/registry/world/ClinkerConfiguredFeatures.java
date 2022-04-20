package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ClinkerConfiguredFeatures {
    public static ConfiguredFeature<?, ?> ASH_LAYER = registerConfiguredFeature("ash_layer", ClinkerFeatures.ASH_LAYER.get().configured(FeatureConfiguration.NONE));
    public static ConfiguredFeature<?, ?> ASH_PILE = registerConfiguredFeature("ash_pile", ClinkerFeatures.ASH_PILE.get().configured(FeatureConfiguration.NONE).squared().rangeUniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(180)).count(UniformInt.of(20, 30)));
    public static ConfiguredFeature<?, ?> CAPSTONE_REPLACEMENT = registerConfiguredFeature("capstone_replacement", ClinkerFeatures.LAYERED_REPLACEMENT.get().configured(FeatureConfiguration.NONE).squared().rangeUniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(180)).count(UniformInt.of(20, 30)));

    public static ConfiguredFeature<?, ?> registerConfiguredFeature(String registryName, ConfiguredFeature<?, ?> configuredFeature) {
        ResourceLocation resourceLocation = new ResourceLocation(Clinker.MOD_ID, registryName);

        if (BuiltinRegistries.CONFIGURED_FEATURE.keySet().contains(resourceLocation)) {
            throw new IllegalStateException("Configured Feature ID: \"" + resourceLocation.toString() + "\" is already in the registry!");
        }

        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, resourceLocation, configuredFeature);
    }
}
