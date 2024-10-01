package birsy.clinker.common.world.level.gen.chunk.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultSurfaceDecorator extends SurfaceDecorator {
    public DefaultSurfaceDecorator() {}

    @Override
    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, DerivativeProvider noiseDerivative) {
        //it does nothing.
    }
}
