package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.level.gen.feature.*;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Clinker.MOD_ID);



    public static final Supplier<Feature<NoneFeatureConfiguration>> ASH_LAYER = FEATURES.register("ash_layer", () -> new AshBuildupFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> ASH_PILE = FEATURES.register("ash_pile", () -> new AshPileFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> LAYERED_REPLACEMENT = FEATURES.register("layered_replacement", () -> new LayeredReplacementFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> SURFACE_DRIED_CLOVERS = FEATURES.register("surface_dried_clovers", () -> new SurfaceDriedCloversFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> MUD_REEDS_PATCH = FEATURES.register("mud_reeds_patch", () -> new MudReedsPatchFeature(NoneFeatureConfiguration.CODEC));

}
