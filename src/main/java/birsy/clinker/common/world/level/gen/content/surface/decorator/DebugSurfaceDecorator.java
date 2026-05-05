package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DebugSurfaceDecorator extends SurfaceDecorator {
    private static final Block[] INCREASE_BLOCKS = {
            Blocks.BLUE_TERRACOTTA,
            Blocks.BLUE_CONCRETE,
            Blocks.LIGHT_BLUE_TERRACOTTA,
            Blocks.LIGHT_BLUE_CONCRETE,
            Blocks.WHITE_CONCRETE,
    };
    private static final Block[] DECREASE_BLOCKS = {
            Blocks.RED_CONCRETE,
            Blocks.RED_TERRACOTTA,
            Blocks.ORANGE_TERRACOTTA,
            Blocks.ORANGE_CONCRETE,
            Blocks.YELLOW_CONCRETE,
    };

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {}

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, Direction surfaceNormal,
                                int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky,
                                WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random) {
        Block block;
        if (maxUpwardsOffset > maxDownwardsOffset) {
            block = INCREASE_BLOCKS[Math.min(maxUpwardsOffset, INCREASE_BLOCKS.length) - 1];
        } else if (maxDownwardsOffset > 0) {
            block = DECREASE_BLOCKS[Math.min(maxDownwardsOffset, DECREASE_BLOCKS.length) - 1];
        } else {
            block = Blocks.LIME_CONCRETE;
        }
        level.setBlock(pos, block.defaultBlockState(), 0);
    }
}
