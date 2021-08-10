package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.feature.enviornment.SpeleothemConfig;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import net.minecraft.data.worldgen.Features;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

import RuleTest;

public class ClinkerConfiguredFeatures {
    public static final RuleTest BRIMSTONE_REPLACABLES = new BlockMatchTest(ClinkerBlocks.BRIMSTONE.get());

    public static final ConfiguredFeature<?, ?> COBBLED_BRIMSTONE_VEIN = Feature.ORE.configured(new OreConfiguration(BRIMSTONE_REPLACABLES, ClinkerBlocks.COBBLED_BRIMSTONE.get().defaultBlockState(), 40)).range(256).squared().count(10);
    public static final ConfiguredFeature<?, ?> CAVE_PUDDLE = ClinkerFeatures.CAVE_PUDDLE.get().configured(new ReplaceSphereConfiguration(ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), UniformInt.of(5, 5))).range(128).squared().count(5);
    public static final ConfiguredFeature<?, ?> CAVE_FLOOR = ClinkerFeatures.CAVE_FLOOR.get().configured(new ReplaceSphereConfiguration(ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), UniformInt.of(3, 3))).range(128).squared().count(7);
    public static final ConfiguredFeature<?, ?> ASH_GEODE = ClinkerFeatures.ASH_GEODE.get().configured(new ReplaceSphereConfiguration(ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), ClinkerBlocks.ASH.get().defaultBlockState(), UniformInt.of(2, 2))).range(128).squared().count(4);
    public static final ConfiguredFeature<?, ?> LARGE_SPELEOTHEM = ClinkerFeatures.SPELEOTHEM.get().configured(FeatureConfiguration.NONE).range(128).squared().count(120);

    public static final ConfiguredFeature<?, ?> SHORELINE_PUDDLE = ClinkerFeatures.SHORELINE_PUDDLE.get().configured(new ReplaceSphereConfiguration(ClinkerBlocks.SCORSTONE.get().defaultBlockState(), Blocks.WATER.defaultBlockState(), UniformInt.of(5, 5))).range(128).squared().count(5);

    // Will definitely try to come up with a better system for this - something with the carvers, perhaps?
    public static final ConfiguredFeature<?, ?> AQUIFER = ClinkerFeatures.AQUIFER.get().configured(new BlockStateConfiguration(Blocks.WATER.defaultBlockState())).decorated(FeatureDecorator.WATER_LAKE.configured(new ChanceDecoratorConfiguration(4)));

    public static final ConfiguredFeature<?, ?> ASH_DUNE = ClinkerFeatures.ASH_DUNE.get().configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(1)));

    public static final ConfiguredFeature<?, ?> SMALL_CRAGROCK = ClinkerFeatures.CRAGROCK.get().configured(new ColumnFeatureConfiguration(UniformInt.fixed(1), UniformInt.of(1, 3))).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(1)));
    public static final ConfiguredFeature<?, ?> LARGE_CRAGROCK = ClinkerFeatures.CRAGROCK.get().configured(new ColumnFeatureConfiguration(UniformInt.of(2, 1), UniformInt.of(5, 5))).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(1)));
    public static final ConfiguredFeature<?, ?> BOULDER = ClinkerFeatures.BOULDER.get().configured(new BlockStateConfiguration(ClinkerBlocks.BRIMSTONE.get().defaultBlockState())).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(5)));

    public static final ConfiguredFeature<?, ?> ROOTED_ASH = ClinkerFeatures.ROOTED_ASH.get().configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(2)));

    //Temporary - will try to find a better way to make it fill caves more, as it can currently have big gaps. Might just make it larger.
    public static final ConfiguredFeature<?, ?> BRAMBLE_CAVE = Feature.DISK.configured(new DiskConfiguration(ClinkerBlocks.BRAMBLE.get().defaultBlockState(), UniformInt.of(5, 6), 5, ImmutableList.of(Blocks.CAVE_AIR.defaultBlockState(), ClinkerBlocks.FOUL_AIR.get().defaultBlockState()))).decorated(FeatureDecorator.LAVA_LAKE.configured(new ChanceDecoratorConfiguration(3)));
    public static final ConfiguredFeature<?, ?> ROOTSTALK_CAVE = Feature.DISK.configured(new DiskConfiguration(ClinkerBlocks.ROOTSTALK.get().defaultBlockState(), UniformInt.of(5, 6), 5, ImmutableList.of(Blocks.CAVE_AIR.defaultBlockState(), ClinkerBlocks.FOUL_AIR.get().defaultBlockState()))).decorated(FeatureDecorator.LAVA_LAKE.configured(new ChanceDecoratorConfiguration(3)));

    //plants
    public static final ConfiguredFeature<?, ?> CAVE_MOSS_PATCH = Feature.RANDOM_PATCH.configured((new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(ClinkerBlocks.CAVE_MOSS.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE)).tries(128).canReplace().build());
    public static final ConfiguredFeature<?, ?> CAVE_MOSS_NORMAL = CAVE_MOSS_PATCH.decorated(Features.Decorators.HEIGHTMAP_DOUBLE_SQUARE).chance(8);

    public static final ConfiguredFeature<?, ?> BRAMBLE_BLOB = ClinkerFeatures.BRAMBLE_BLOB.get().configured(FeatureConfiguration.NONE).range(128).squared().count(30);

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "cobbled_brimstone_vein"), COBBLED_BRIMSTONE_VEIN);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "cave_puddle"), CAVE_PUDDLE);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "cave_floor"), CAVE_FLOOR);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "ash_geode"), ASH_GEODE);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "large_speleothem"), LARGE_SPELEOTHEM);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "shoreline_puddle"), SHORELINE_PUDDLE);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "patch_rootstalk_root"), ASH_GEODE);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "ash_dune"), ASH_DUNE);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "small_cragrock"), SMALL_CRAGROCK);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "large_cragrock"), LARGE_CRAGROCK);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "boulder"), BOULDER);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "rooted_ash"), ROOTED_ASH);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "bramble_cave"), BRAMBLE_CAVE);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "rootstalk_cave"), ROOTSTALK_CAVE);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "cave_moss_patch"), CAVE_MOSS_PATCH);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "cave_moss_normal"), CAVE_MOSS_NORMAL);

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "bramble_blob"), BRAMBLE_BLOB);

    }
}
