package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.feature.*;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Clinker.MOD_ID);

    public static final Supplier<Feature<NoneFeatureConfiguration>> ASH_LAYER =
            FEATURES.register("ash_layer", () -> new AshBuildupFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> ASH_PILE =
            FEATURES.register("ash_pile", () -> new AshPileFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> LAYERED_REPLACEMENT =
            FEATURES.register("layered_replacement", () -> new LayeredReplacementFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> DRIED_CLOVERS =
            FEATURES.register("dried_clovers", () -> new DriedCloversFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> MUD_REEDS_PATCH =
            FEATURES.register("mud_reeds_patch", () -> new MudReedsPatchFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> SALTMOSS_BLOOM =
            FEATURES.register("saltmoss_bloom", () -> new SaltmossBloomFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> SHEET_MOSS =
            FEATURES.register("sheet_moss", () -> new SheetMossFeature2(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<BlockStateConfiguration>> BOULDER =
            FEATURES.register("boulder", () -> new BoulderFeature(BlockStateConfiguration.CODEC));
    public static final Supplier<Feature<BlockStateConfiguration>> FLUID_LEAK =
            FEATURES.register("fluid_leak", () -> new FluidLeakFeature(BlockStateConfiguration.CODEC));
    public static final Supplier<Feature<FluidCrackFeature.FluidCrackConfiguration>> FLUID_CRACK =
            FEATURES.register("fluid_crack", () -> new FluidCrackFeature(FluidCrackFeature.FluidCrackConfiguration.CODEC));
    public static final Supplier<Feature<PoolFeature.PoolFeatureConfiguration>> POOL =
            FEATURES.register("pool", () -> new PoolFeature(PoolFeature.PoolFeatureConfiguration.CODEC));
    public static final Supplier<Feature<SurfaceBlobFeature.SurfaceBlobConfiguration>> SURFACE_BLOB =
            FEATURES.register("surface_blob", () -> new SurfaceBlobFeature(SurfaceBlobFeature.SurfaceBlobConfiguration.CODEC));
}
