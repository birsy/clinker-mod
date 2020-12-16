package birsy.clinker.common.block.trees;

import java.util.Random;

import birsy.clinker.common.world.feature.ClinkerBiomeFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class LocustTree extends Tree {
	@Override
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive) {
		return Feature.TREE.withConfiguration(ClinkerBiomeFeatures.LOCUST_TREE_CONFIG);
	}
}
