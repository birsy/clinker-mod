package birsy.clinker.common.block.trees;

import java.util.Random;

import birsy.clinker.common.world.feature.ClinkerBiomeFeatures;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class LocustTree extends AbstractTreeGrower {
	@Override
	protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random randomIn, boolean largeHive) {
		return Feature.TREE.configured(ClinkerBiomeFeatures.LOCUST_TREE_CONFIG);
	}
}
