package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.feature.*;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
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
    public static final Supplier<Feature<NoneFeatureConfiguration>> SNAKE_MUD_REEDS =
            FEATURES.register("snake_mud_reeds", () -> new SnakeReedsFeature(NoneFeatureConfiguration.CODEC));
//    public static final Supplier<Feature<NoneFeatureConfiguration>> WATERLINE_FERN =
//            FEATURES.register("waterline_fern", () -> new WaterlineFernFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> BRINE_BRAMBLE =
            FEATURES.register("brine_bramble", () -> new BrineBrambleFeature(NoneFeatureConfiguration.CODEC));
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
    public static final Supplier<Feature<FluidPoolFeature.PoolConfiguration>> FLUID_POOL =
            FEATURES.register("fluid_pool", () -> new FluidPoolFeature(FluidPoolFeature.PoolConfiguration.CODEC));
    public static final Supplier<Feature<SurfaceBlobFeature.SurfaceBlobConfiguration>> SURFACE_BLOB =
            FEATURES.register("surface_blob", () -> new SurfaceBlobFeature(SurfaceBlobFeature.SurfaceBlobConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> CORPSE_LILY =
            FEATURES.register("corpse_lily", () -> new CorpseLilyFeature(NoneFeatureConfiguration.CODEC));
    public static final Supplier<Feature<SpotreedFeature.SpotreedFeatureConfiguration>> SPOTREED =
            FEATURES.register("spotreed", () -> new SpotreedFeature(SpotreedFeature.SpotreedFeatureConfiguration.CODEC));
    public static final Supplier<Feature<NoneFeatureConfiguration>> TAPROOT =
            FEATURES.register("taproot", () -> new TaprootFeature(NoneFeatureConfiguration.CODEC));
//    static class Configured {
//        public static final ClinkerConfiguredFeature<?, ?> BRIMSTONE_BOULDER = ClinkerConfiguredFeature.create(
//                "brimstone_boulder",
//                ClinkerFeatures.BOULDER.get(),
//                new BlockStateConfiguration(ClinkerBlocks.BRIMSTONE.get().defaultBlockState())
//        );
//        public static final ClinkerConfiguredFeature<?, ?> CALC_BOULDER = ClinkerConfiguredFeature.create(
//                "calc_boulder",
//                ClinkerFeatures.BOULDER.get(),
//                new BlockStateConfiguration(ClinkerBlocks.CALC.get().defaultBlockState())
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SHALE_BOULDER = ClinkerConfiguredFeature.create(
//                "shale_boulder",
//                ClinkerFeatures.BOULDER.get(),
//                new BlockStateConfiguration(ClinkerBlocks.SHALE.get().defaultBlockState())
//        );
//
//        public static final ClinkerConfiguredFeature<?, ?> BLUE_ROSE = ClinkerConfiguredFeature.create(
//                "blue_rose",
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.BLUE_ROSE.get()))
//        );
//        public static final ClinkerConfiguredFeature<?, ?> CORPSE_LILY = ClinkerConfiguredFeature.create(
//                "corpse_lily",
//                ClinkerFeatures.CORPSE_LILY.get(),
//                new NoneFeatureConfiguration()
//        );
//        public static final ClinkerConfiguredFeature<?, ?> MOTH_BALLS = ClinkerConfiguredFeature.create(
//                "moth_balls",
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(new WeightedStateProvider(
//                        SimpleWeightedRandomList.<BlockState>builder()
//                                .add(ClinkerBlocks.MOTH_BALL.get().defaultBlockState().setValue(MothBallBlock.COUNT, 1), 5)
//                                .add(ClinkerBlocks.MOTH_BALL.get().defaultBlockState().setValue(MothBallBlock.COUNT, 2), 4)
//                                .add(ClinkerBlocks.MOTH_BALL.get().defaultBlockState().setValue(MothBallBlock.COUNT, 3), 1)
//                ))
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SEA_SHELL = ClinkerConfiguredFeature.create(
//                "sea_shell",
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.SEA_SHELL.get()))
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SALTMOSS_BLOOM = ClinkerConfiguredFeature.create(
//                "saltmoss_bloom",
//                ClinkerFeatures.SALTMOSS_BLOOM.get(),
//                new NoneFeatureConfiguration()
//        );
//        public static final ClinkerConfiguredFeature<?, ?> BRAMBLE_BRINE = ClinkerConfiguredFeature.create(
//                "bramble_brine",
//                ClinkerFeatures.BRINE_BRAMBLE.get(),
//                new NoneFeatureConfiguration()
//        );
//        public static final ClinkerConfiguredFeature<?, ?> DRIED_CLOVERS = ClinkerConfiguredFeature.create(
//                "dried_clovers",
//                ClinkerFeatures.DRIED_CLOVERS.get(),
//                new NoneFeatureConfiguration()
//        );
//
//        public static final ClinkerConfiguredFeature<?, ?> CAVE_SPROUTS_PATCH = createRandomPatch(
//                "cave_sprouts_patch",
//                24, 7, 3,
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.CAVE_SPROUTS.get())),
//                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
//        );
//        public static final ClinkerConfiguredFeature<?, ?> INDIGO_TORMENTIL_PATCH = createRandomPatch(
//                "indigo_tormentil_patch",
//                24, 4, 3,
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.INDIGO_TORMENTIL.get())),
//                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
//        );
//        public static final ClinkerConfiguredFeature<?, ?> YELLOW_TORMENTIL_PATCH = createRandomPatch(
//                "yellow_tormentil_patch",
//                24, 4, 3,
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.YELLOW_TORMENTIL.get())),
//                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SPOTREED_PATCH = createRandomPatch(
//                "spotreed_patch",
//                48, 4, 4,
//                ClinkerFeatures.SPOTREED.get(),
//                new SpotreedFeature.SpotreedFeatureConfiguration(
//                        ClampedNormalInt.of(4, 2, 3, 7),
//                        false
//                ),
//                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
//        );
//        public static final ClinkerConfiguredFeature<?,?> PEAT_MOSS_BUDS_PATCH = createRandomPatch(
//                "peat_moss_buds_patch",
//                48, 7, 3,
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.PEAT_MOSS_BUDS.get())),
//                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SALTMOSS_SPROUTS_PATCH = createRandomPatch(
//                "saltmoss_sprouts_patch",
//                48, 7, 3,
//                Feature.SIMPLE_BLOCK,
//                new SimpleBlockConfiguration(BlockStateProvider.simple(ClinkerBlocks.SALTMOSS_SPROUTS.get())),
//                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
//        );
//        public static final ClinkerConfiguredFeature<?, ?> MUD_REEDS_PATCH = ClinkerConfiguredFeature.create(
//                "mud_reeds_patch",
//                ClinkerFeatures.MUD_REEDS_PATCH.get(),
//                new NoneFeatureConfiguration()
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SNAKE_MUD_REEDS_PATCH = ClinkerConfiguredFeature.create(
//                "snake_mud_reeds_patch",
//                ClinkerFeatures.SNAKE_MUD_REEDS.get(),
//                new NoneFeatureConfiguration()
//        );
//        public static final ClinkerConfiguredFeature<?, ?> SHEET_MOSS_PATCH = ClinkerConfiguredFeature.create(
//                "sheet_moss_patch",
//                ClinkerFeatures.SHEET_MOSS.get(),
//                new NoneFeatureConfiguration()
//        );
//
//        public static final ClinkerConfiguredFeature<?, ?> PEAT_MOSS_SURFACE_BLOB = ClinkerConfiguredFeature.create(
//                "peat_moss_surface_blob",
//                (context) -> new ConfiguredFeature<>(
//                        ClinkerFeatures.SURFACE_BLOB.get(),
//                        new SurfaceBlobFeature.SurfaceBlobConfiguration(
//                                Optional.of(Holder.direct(new PlacedFeature(
//                                        context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(PEAT_MOSS_BUDS_PATCH.genericKey()),
//                                        List.of(RarityFilter.onAverageOnceEvery(8))
//                                ))),
//                                BlockStateProvider.simple(ClinkerBlocks.PEAT_MOSS.get()),
//                                BlockStateProvider.simple(ClinkerBlocks.PEAT_MOSS.get()),
//                                ConstantInt.of(4),
//                                ConstantFloat.of(3.0F)
//                        )
//                )
//        );
//
//        public static final ClinkerConfiguredFeature<?, ?> WATER_CRACK = ClinkerConfiguredFeature.create(
//                "water_crack",
//                ClinkerFeatures.FLUID_CRACK.get(),
//                new FluidCrackFeature.FluidCrackConfiguration(
//                        Blocks.WATER.defaultBlockState(),
//                        ConstantInt.of(5),
//                        ConstantInt.of(32)
//                )
//        );
//        public static final ClinkerConfiguredFeature<?, ?> WATER_LEAK = ClinkerConfiguredFeature.create(
//                "water_leak",
//                ClinkerFeatures.FLUID_LEAK.get(),
//                new BlockStateConfiguration(Blocks.WATER.defaultBlockState())
//        );
//        public static final ClinkerConfiguredFeature<?, ?> WATER_POOL = ClinkerConfiguredFeature.create(
//                "water_pool",
//                ClinkerFeatures.FLUID_POOL.get(),
//                new FluidPoolFeature.PoolConfiguration(
//                        BlockStateProvider.simple(Blocks.WATER),
//                        ConstantInt.of(5)
//                )
//        );
//
//        static <F extends Feature<C>, C extends FeatureConfiguration> ClinkerConfiguredFeature<RandomPatchFeature, RandomPatchConfiguration> createRandomPatch(
//                String name,
//                int tries, int xzSpread, int ySpread,
//                F feature, C featureConfig, PlacementModifier... placementModifiers
//        ) {
//            return ClinkerConfiguredFeature.create(
//                    name,
//                    (RandomPatchFeature) Feature.RANDOM_PATCH,
//                    new RandomPatchConfiguration(
//                            tries, xzSpread, ySpread,
//                            Holder.direct(new PlacedFeature(
//                                    Holder.direct(new ConfiguredFeature<>(feature, featureConfig)),
//                                    List.of(placementModifiers)
//                            )
//                    )
//            ));
//        }
//    }
//
//    static class Placed {
//        public static final ClinkerPlacedFeature
//    }
}
