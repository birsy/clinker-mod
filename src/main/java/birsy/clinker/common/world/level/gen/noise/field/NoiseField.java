package birsy.clinker.common.world.level.gen.noise.field;

import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;

public interface NoiseField {
    int CHUNK_WIDTH = 16;
    void fill(int minX, int minY, int minZ, NoiseComputer noiseComputer, NoiseComputerContext context);
    double retrieve(int x, int y, int z);
}
