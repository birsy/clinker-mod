package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultSurfaceDecorator extends SurfaceDecorator {
    public DefaultSurfaceDecorator() {}

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        // it does nothing
    }
    @Override
    public void decorateSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, int surfaceHeight, NoiseContext context, RandomSource random) {
        // still nothing
    }
}
