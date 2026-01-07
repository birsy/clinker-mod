package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class SurfaceDecorator {
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {}
    public abstract void decorateSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos,
                                         int seaLevel, boolean canSeeSun, int depth,
                                         int maxElevationIncrease, int maxElevationDecrease, int surfaceHeight,
                                         NoiseContext context, RandomSource random);

    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y) {
        return canSeeSun;
    }
}
