package birsy.clinker.common.level.feature;

import birsy.clinker.common.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AshPileFeature extends Feature<NoneFeatureConfiguration> {
    public AshPileFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    /**
     * Places the given feature at the given location.
     * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
     * that they can safely generate into.
     * @param featureContext A context object with a reference to the level and the position the feature is being placed at
     */
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureContext) {
        WorldGenLevel level = featureContext.level();
        BlockPos origin = featureContext.origin();

        int maxHeight = 2 + featureContext.random().nextInt(6);
        int range =  2 + featureContext.random().nextInt(3);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        if (level.getBlockState(origin).isAir()) {
            for (int xOffset = -range; xOffset < range; ++xOffset) {
                for (int zOffset = -range; zOffset < range; ++zOffset) {
                    for (int yOffset = -range; yOffset < range; ++yOffset) {
                        int x = origin.getX() + xOffset;
                        int y = origin.getY() + yOffset;
                        int z = origin.getZ() + zOffset;
                        pos.set(x, y, z);

                        if (level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP) && level.getBlockState(pos).isAir()) {
                            int ashHeight = (int) MathUtils.mapRange(0, range, maxHeight, 0, pos.distManhattan(origin));
                            ashHeight += featureContext.random().nextGaussian();

                            for (Direction direction : Direction.Plane.HORIZONTAL) {
                                if (level.getBlockState(pos.relative(direction)).isFaceSturdy(level, pos.relative(direction), direction.getOpposite())) {
                                    ashHeight += featureContext.random().nextInt(3);
                                }
                            }

                            if (ashHeight > 0) {
                                level.setBlock(pos, ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, Mth.clamp(ashHeight, 1, 8)), 2);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}