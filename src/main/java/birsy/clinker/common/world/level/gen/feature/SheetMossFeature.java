package birsy.clinker.common.world.level.gen.feature;

import birsy.clinker.common.world.block.plant.DoubleSheetMossBlock;
import birsy.clinker.common.world.block.plant.SheetMossBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class SheetMossFeature extends Feature<NoneFeatureConfiguration> {
    public SheetMossFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos.MutableBlockPos pos = origin.mutable();

        boolean placed = false;

        int count = random.nextInt(16, 32);

        MOSS:
        for (int i = 0; i < count; i++) {
            int xOffset = (int) Math.clamp(random.nextGaussian() * 3, -8, 8),
                zOffset = (int) Math.clamp(random.nextGaussian() * 3, -8, 8);
            pos.set(origin).move(xOffset, random.nextInt(-3, 0), zOffset);

            for (int j = 0; j < 6; j++) {
                BlockState state = level.getBlockState(pos);

                // there is already a block here, skip the column check.
                if (!state.canBeReplaced() || state.is(ClinkerBlocks.SHEET_MOSS) || state.is(ClinkerBlocks.LONG_SHEET_MOSS))
                    continue MOSS;

                pos.move(0, 1, 0);
                BlockState aboveState = level.getBlockState(pos);

                if (SheetMossBlock.canPlaceOn(aboveState, level, pos)) {
                    pos.move(0, -1, 0);
                    boolean placeLong = Math.sqrt((xOffset * xOffset) + (zOffset * zOffset)) + (random.nextDouble() * 2 - 1) < 2;
                    if (placeLong) {
                        placeLong = level.getBlockState(pos.move(0, -1, 0)).canBeReplaced();
                        pos.move(0, 1, 0);
                    }

                    if (placeLong) {
                        DoubleSheetMossBlock.placeAt(level, ClinkerBlocks.LONG_SHEET_MOSS.get().defaultBlockState(), pos, 2);
                    } else {
                        if (level.getFluidState(pos).is(Fluids.WATER)) {
                            level.setBlock(pos, ClinkerBlocks.SHEET_MOSS.get().defaultBlockState().setValue(SheetMossBlock.WATERLOGGED, true), 2);
                        } else {
                            level.setBlock(pos, ClinkerBlocks.SHEET_MOSS.get().defaultBlockState(), 2);
                        }
                    }
                    placed = true;
                    break;
                }
            }
        }

        return placed;
    }

}
