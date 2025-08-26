package birsy.clinker.common.world.level.gen.surfacedecorator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class SurfaceDecorator {
    public abstract void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, RandomSource random);
    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y) {
        return canSeeSun;
    }
}
