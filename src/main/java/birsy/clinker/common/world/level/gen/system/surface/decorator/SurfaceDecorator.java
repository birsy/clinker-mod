package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class SurfaceDecorator {
    public abstract void prefillNoiseFields(NoiseFieldCache cache);
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos,
                                         int seaLevel,
                                         ChunkAccess chunk, NoiseContext noiseContext, RandomSource random,
                                         SurfaceDecorationContext surfaceContext);
    public boolean shouldCalculateElevationChange(boolean visibleToSky, int y, double surfaceHeight) {
        return visibleToSky;
    }
}
