package birsy.clinker.common.world.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;

import BlockPileConfiguration;

public class ClinkerBiomeFeatures
{		
	//public static final BlockClusterFeatureConfig ROOT_GRASS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ClinkerBlocks.ROOT_GRASS.get().getDefaultState()), SimpleBlockPlacer.INSTANCE)).tries(32).build();
	
	public static final BlockPileConfiguration ROOT_GRASS_FEATURE_CONFIG = new BlockPileConfiguration((new WeightedStateProvider()).add(ClinkerBlocks.ROOT_GRASS.get().defaultBlockState(), 87));
	
	public static final TreeConfiguration LOCUST_TREE_CONFIG = (new TreeConfiguration.TreeConfigurationBuilder(
			new SimpleStateProvider(ClinkerBlocks.LOCUST_LOG.get().defaultBlockState()),
			new SimpleStateProvider(ClinkerBlocks.LOCUST_LEAVES.get().defaultBlockState()), 
			new PineFoliagePlacer(UniformInt.fixed(3), UniformInt.fixed(4), UniformInt.fixed(5)),
			new FancyTrunkPlacer(5, 2, 6),
			new TwoLayersFeatureSize(4, 8, 4)))
			.decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, LeaveVineDecorator.INSTANCE))
			.ignoreVines()
			.build();

}
