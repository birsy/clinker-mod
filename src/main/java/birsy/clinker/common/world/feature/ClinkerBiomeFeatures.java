package birsy.clinker.common.world.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateProvidingFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.PineFoliagePlacer;
import net.minecraft.world.gen.treedecorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.trunkplacer.FancyTrunkPlacer;

public class ClinkerBiomeFeatures
{		
	//public static final BlockClusterFeatureConfig ROOT_GRASS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ClinkerBlocks.ROOT_GRASS.get().getDefaultState()), SimpleBlockPlacer.field_236447_c_)).tries(32).build();
	
	public static final BlockClusterFeatureConfig BRAMBLES_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ClinkerBlocks.BRAMBLE.get().getDefaultState()), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(ClinkerBlocks.ASH.get())).func_227317_b_().build();
	
	public static final BlockStateProvidingFeatureConfig ROOT_GRASS_FEATURE_CONFIG = new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).addWeightedBlockstate(ClinkerBlocks.ROOT_GRASS.get().getDefaultState(), 87));
	
	public static final BaseTreeFeatureConfig LOCUST_TREE_CONFIG = (new BaseTreeFeatureConfig.Builder(
			new SimpleBlockStateProvider(ClinkerBlocks.LOCUST_LOG.get().getDefaultState()),
			new SimpleBlockStateProvider(ClinkerBlocks.LOCUST_LEAVES.get().getDefaultState()), 
			new PineFoliagePlacer(FeatureSpread.func_242252_a(3), FeatureSpread.func_242252_a(4), FeatureSpread.func_242252_a(5)),
			new FancyTrunkPlacer(5, 2, 6),
			new TwoLayerFeature(4, 8, 4)))
			.setDecorators(ImmutableList.of(TrunkVineTreeDecorator.field_236879_b_, LeaveVineTreeDecorator.field_236871_b_))
			.setIgnoreVines()
			.build();

}
