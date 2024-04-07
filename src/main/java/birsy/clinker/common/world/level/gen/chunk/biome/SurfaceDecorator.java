package birsy.clinker.common.world.level.gen.chunk.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public abstract class SurfaceDecorator {
    protected long seed = 0;
    protected RandomSource random = RandomSource.create();
    public void setSeed(long seed) {
        if (this.seed != seed) this.random.setSeed(seed);
    }
    public abstract void buildSurface(BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, float noiseDerivative, int maxElevationIncrease, int maxElevationDecrease, ChunkAccess chunk, NoiseGeneratorSettings settings);
}
