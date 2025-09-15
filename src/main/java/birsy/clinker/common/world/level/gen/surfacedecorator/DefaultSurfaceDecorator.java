package birsy.clinker.common.world.level.gen.surfacedecorator;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultSurfaceDecorator extends SurfaceDecorator {
    public DefaultSurfaceDecorator() {}

    @Override
    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, NoiseComputerContext noiseContext, RandomSource random) {
        //it does nothing.
    }
}
