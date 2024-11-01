package birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator;

import birsy.clinker.common.world.level.gen.NoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.joml.Vector3fc;

import java.util.concurrent.ExecutionException;

public abstract class SurfaceDecorator {
    protected long seed = 0;
    protected RandomSource random = RandomSource.create();
    public void setSeed(long seed) {
        if (this.seed != seed) this.random.setSeed(seed);
    }
    public abstract void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, DerivativeProvider noiseDerivative, NoiseSampler sampler) throws ExecutionException;
    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y) {
        return canSeeSun;
    }
    public interface DerivativeProvider {
        Vector3fc apply(double x, double y, double z);
    }
}
