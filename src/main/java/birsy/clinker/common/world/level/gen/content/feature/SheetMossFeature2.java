package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.block.plant.DoubleSheetMossBlock;
import birsy.clinker.common.block.plant.SheetMossBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SheetMossFeature2 extends Feature<NoneFeatureConfiguration> {
    public SheetMossFeature2(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        boolean placed = false;
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos.MutableBlockPos cursor = origin.mutable();

        int radius = random.nextInt(2, 5);
        NEXT_POS:
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-radius, 0, -radius), origin.offset(radius, 0, radius))) {
            // compute "factor"
            double factor = Math.sqrt(pos.distToCenterSqr(origin.getX(), origin.getY(), origin.getZ())) / radius;
            factor -= 1;
            factor = factor * factor * Mth.sign(factor);
            factor += random.triangle(0, 1);
            factor *= -1;
            if (factor < 0) continue;
            // find a valid position
            cursor.set(pos.getX(), origin.getY() - 2, pos.getZ());
            boolean foundPosition = false;
            boolean canPlaceInLastPos = isReplaceable(level.getBlockState(cursor));
            for (int i = 0; i < 5; i++) {
                cursor.move(Direction.UP);
                BlockState state = level.getBlockState(cursor);
                boolean supports = state.isFaceSturdy(level, cursor, Direction.DOWN, SupportType.FULL);
                if (supports && canPlaceInLastPos) {
                    foundPosition = true;
                    break;
                } else if (supports) {
                    continue NEXT_POS;
                }
                canPlaceInLastPos = isReplaceable(state);
            }
            if (!foundPosition) continue;
            cursor.move(Direction.DOWN);

            // add factor bonus if it borders a solid face
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                cursor.move(direction);
                BlockState state = level.getBlockState(cursor);
                boolean faceSolid = state.isFaceSturdy(level, cursor, direction.getOpposite());
                cursor.move(direction.getOpposite());
                if (faceSolid) {
                    factor += 0.25;
                }
            }

            if (placeSheetMoss(level, cursor, factor > 0.7)) {
                placed = true;
            }
        }

        return placed;
    }

    private static boolean placeSheetMoss(WorldGenLevel level, BlockPos pos, boolean isDouble) {
//        // current block is solid, can't place
//        if (!isReplaceable(level.getBlockState(pos)))
//            return false;
//        // above block can't support, can't place
//        if (!level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN, SupportType.FULL))
//            return false;
        // below block is solid, so it can't be a double tall.
        if (isDouble && !isReplaceable(level.getBlockState(pos.below())))
            isDouble = false;

        if (isDouble) {
            DoubleSheetMossBlock.placeAt(level, ClinkerBlocks.LONG_SHEET_MOSS.get().defaultBlockState(), pos, 2);
        } else {
            boolean isWaterlogged = level.isWaterAt(pos);
            level.setBlock(pos, ClinkerBlocks.SHEET_MOSS.get().defaultBlockState().setValue(SheetMossBlock.WATERLOGGED, isWaterlogged), 2);
        }
        return true;
    }

    private static boolean isReplaceable(BlockState blockState) {
        return blockState.isAir() || blockState.is(Blocks.WATER);
    }
}
