package birsy.clinker.common.world.level.feature;

import birsy.clinker.common.world.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AshBuildupFeature extends Feature<NoneFeatureConfiguration> {
    public AshBuildupFeature(Codec<NoneFeatureConfiguration> config) {
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

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for(int xOffset = 0; xOffset < 16; ++xOffset) {
            for(int yOffset = 0; yOffset < 16; ++yOffset) {
                int x = origin.getX() + xOffset;
                int z = origin.getZ() + yOffset;
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                pos.set(x, y, z);

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    //Second clause ensures that it doesn't happen on steeper cliffsides, only smooth plains.
                    if (level.getBlockState(pos.relative(direction)).isFaceSturdy(level, pos.relative(direction), direction.getOpposite()) && !level.getBlockState(pos.relative(direction).above()).isFaceSturdy(level, pos.relative(direction).above(), direction.getOpposite())) {
                        level.setBlock(pos, ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, featureContext.random().nextInt(3) + 1), 2);
                        break;
                    }
                }


            }
        }

        return true;
    }
}