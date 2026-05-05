package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class SurfaceDecorator {
    public abstract void prefillNoiseFields(NoiseFieldCache cache);
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos, Direction surfaceNormal,
                                         int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky,
                                         WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random);
}
