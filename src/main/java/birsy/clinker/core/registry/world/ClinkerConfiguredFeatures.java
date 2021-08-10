package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.feature.enviornment.SpeleothemConfig;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;

public class ClinkerConfiguredFeatures {
    public static final RuleTest BRIMSTONE_REPLACABLES = new BlockMatchRuleTest(ClinkerBlocks.BRIMSTONE.get());

    public static final ConfiguredFeature<?, ?> COBBLED_BRIMSTONE_VEIN = Feature.ORE.withConfiguration(new OreFeatureConfig(BRIMSTONE_REPLACABLES, ClinkerBlocks.COBBLED_BRIMSTONE.get().getDefaultState(), 40)).range(256).square().count(10);
    public static final ConfiguredFeature<?, ?> CAVE_PUDDLE = ClinkerFeatures.CAVE_PUDDLE.get().withConfiguration(new BlobReplacementConfig(ClinkerBlocks.BRIMSTONE.get().getDefaultState(), ClinkerBlocks.BRIMSTONE.get().getDefaultState(), FeatureSpread.create(5, 5))).range(128).square().count(5);
    public static final ConfiguredFeature<?, ?> CAVE_FLOOR = ClinkerFeatures.CAVE_FLOOR.get().withConfiguration(new BlobReplacementConfig(ClinkerBlocks.BRIMSTONE.get().getDefaultState(), ClinkerBlocks.BRIMSTONE.get().getDefaultState(), FeatureSpread.create(3, 3))).range(128).square().count(7);
    public static final ConfiguredFeature<?, ?> ASH_GEODE = ClinkerFeatures.ASH_GEODE.get().withConfiguration(new BlobReplacementConfig(ClinkerBlocks.BRIMSTONE.get().getDefaultState(), ClinkerBlocks.ASH.get().getDefaultState(), FeatureSpread.create(2, 2))).range(128).square().count(4);
    public static final ConfiguredFeature<?, ?> LARGE_SPELEOTHEM = ClinkerFeatures.SPELEOTHEM.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).range(128).square().count(120);

    public static final ConfiguredFeature<?, ?> SHORELINE_PUDDLE = ClinkerFeatures.SHORELINE_PUDDLE.get().withConfiguration(new BlobReplacementConfig(ClinkerBlocks.SCORSTONE.get().getDefaultState(), Blocks.WATER.getDefaultState(), FeatureSpread.create(5, 5))).range(128).square().count(5);

    // Will definitely try to come up with a better system for this - something with the carvers, perhaps?
    public static final ConfiguredFeature<?, ?> AQUIFER = ClinkerFeatures.AQUIFER.get().withConfiguration(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).withPlacement(Placement.WATER_LAKE.configure(new ChanceConfig(4)));

    public static final ConfiguredFeature<?, ?> ASH_DUNE = ClinkerFeatures.ASH_DUNE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));

    public static final ConfiguredFeature<?, ?> SMALL_CRAGROCK = ClinkerFeatures.CRAGROCK.get().withConfiguration(new ColumnConfig(FeatureSpread.create(1), FeatureSpread.create(1, 3))).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));
    public static final ConfiguredFeature<?, ?> LARGE_CRAGROCK = ClinkerFeatures.CRAGROCK.get().withConfiguration(new ColumnConfig(FeatureSpread.create(2, 1), FeatureSpread.create(5, 5))).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));
    public static final ConfiguredFeature<?, ?> BOULDER = ClinkerFeatures.BOULDER.get().withConfiguration(new BlockStateFeatureConfig(ClinkerBlocks.BRIMSTONE.get().getDefaultState())).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(5)));

    public static final ConfiguredFeature<?, ?> ROOTED_ASH = ClinkerFeatures.ROOTED_ASH.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(2)));

    //Temporary - will try to find a better way to make it fill caves more, as it can currently have big gaps. Might just make it larger.
    public static final ConfiguredFeature<?, ?> BRAMBLE_CAVE = Feature.DISK.withConfiguration(new SphereReplaceConfig(ClinkerBlocks.BRAMBLE.get().getDefaultState(), FeatureSpread.create(5, 6), 5, ImmutableList.of(Blocks.CAVE_AIR.getDefaultState(), ClinkerBlocks.FOUL_AIR.get().getDefaultState()))).withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(3)));
    public static final ConfiguredFeature<?, ?> ROOTSTALK_CAVE = Feature.DISK.withConfiguration(new SphereReplaceConfig(ClinkerBlocks.ROOTSTALK.get().getDefaultState(), FeatureSpread.create(5, 6), 5, ImmutableList.of(Blocks.CAVE_AIR.getDefaultState(), ClinkerBlocks.FOUL_AIR.get().getDefaultState()))).withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(3)));

    //plants
    public static final ConfiguredFeature<?, ?> CAVE_MOSS_PATCH = Feature.RANDOM_PATCH.withConfiguration((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ClinkerBlocks.CAVE_MOSS.get().getDefaultState()), SimpleBlockPlacer.PLACER)).tries(128).replaceable().build());
    public static final ConfiguredFeature<?, ?> CAVE_MOSS_NORMAL = CAVE_MOSS_PATCH.withPlacement(Features.Placements.PATCH_PLACEMENT).chance(8);

    public static final ConfiguredFeature<?, ?> BRAMBLE_BLOB = ClinkerFeatures.BRAMBLE_BLOB.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).range(128).square().count(30);

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

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
