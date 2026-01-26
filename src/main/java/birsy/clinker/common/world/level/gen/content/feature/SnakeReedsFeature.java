package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.world.block.plant.DoubleMudReedsBlock;
import birsy.clinker.common.world.block.plant.OthershorePlantBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class SnakeReedsFeature extends Feature<NoneFeatureConfiguration> {
    public SnakeReedsFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        boolean placed = false;

        BlockPos.MutableBlockPos pos = origin.mutable();
        BlockPos.MutableBlockPos belowPos = origin.mutable();

        for (int i = 0; i < random.nextInt(2, 9); i++) {
            pos.set(origin).move((int) (random.nextGaussian() * 1.5), 2, (int) (random.nextGaussian() * 1.5));
            belowPos.set(pos).move(0, -1, 0);
            // column scan
            for (int y = 0; y < 4; y++) {
                if (OthershorePlantBlock.canPlaceOn(level.getBlockState(belowPos), level, belowPos) && level.getBlockState(pos).getFluidState().isSourceOfType(Fluids.WATER) && level.getBlockState(pos.immutable().above()).isAir()) {
                    level.setBlock(pos, ClinkerBlocks.TALL_MUD_REEDS.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoubleMudReedsBlock.WATERLOGGED, true), 2);
                    level.setBlock(pos.immutable().above(), ClinkerBlocks.TALL_MUD_REEDS.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
                    placed = true;
                    break;
                }

                pos.move(0, -1, 0);
                belowPos.move(0, -1, 0);
            }
        }

        return placed;
    }

}
