package birsy.clinker.common.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomBricksBlock extends Block {
    public MushroomBricksBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos position, RandomSource random) {
        super.randomTick(blockState, level, position, random);

        if (random.nextInt(5) == 0) {
            level.setBlock(position, Blocks.AIR.defaultBlockState(), 2);
        }
    }
}
