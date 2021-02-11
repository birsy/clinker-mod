package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;

public class ClinkerConfiguredFeatures {
    public static final RuleTest BRIMSTONE_REPLACABLES = new BlockMatchRuleTest(ClinkerBlocks.BRIMSTONE.get());

    public static final ConfiguredFeature<?, ?> COBBLED_BRIMSTONE_VEIN = Feature.ORE.withConfiguration(new OreFeatureConfig(BRIMSTONE_REPLACABLES, ClinkerBlocks.COBBLED_BRIMSTONE.get().getDefaultState(), 40)).range(256).square().func_242731_b(10);
    public static final ConfiguredFeature<?, ?> SMALL_CRAGROCK = ClinkerFeatures.CRAGROCK.get().withConfiguration(new ColumnConfig(FeatureSpread.func_242252_a(1), FeatureSpread.func_242253_a(1, 3))).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));
    public static final ConfiguredFeature<?, ?> LARGE_CRAGROCK = ClinkerFeatures.CRAGROCK.get().withConfiguration(new ColumnConfig(FeatureSpread.func_242253_a(2, 1), FeatureSpread.func_242253_a(5, 5))).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));
    public static final ConfiguredFeature<?, ?> BOULDER = Feature.FOREST_ROCK.withConfiguration(new BlockStateFeatureConfig(ClinkerBlocks.BRIMSTONE.get().getDefaultState())).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).func_242732_c(5);

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "cobbled_brimstone_vein"), COBBLED_BRIMSTONE_VEIN);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "small_cragrock"), SMALL_CRAGROCK);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "large_cragrock"), LARGE_CRAGROCK);
        Registry.register(registry, new ResourceLocation(Clinker.MOD_ID, "boulder"), BOULDER);
    }
}
