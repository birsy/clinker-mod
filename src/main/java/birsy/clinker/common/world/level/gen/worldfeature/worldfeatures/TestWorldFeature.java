package birsy.clinker.common.world.level.gen.worldfeature.worldfeatures;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import net.minecraft.util.RandomSource;

public class TestWorldFeature extends WorldFeature {
    int centerX, centerZ;
    int radius = 16;

    public TestWorldFeature(int depth, int separationRadius) {
        super(depth, separationRadius);
    }

    @Override
    public int getCenterX() {
        return centerX;
    }

    @Override
    public int getCenterZ() {
        return centerZ;
    }

    @Override
    public boolean within(int minX, int minZ, int maxX, int maxZ) {
        return centerX > minX - this.radius &&
               centerX < maxX + this.radius &&
               centerZ > minZ - this.radius &&
               centerZ < maxZ + this.radius;
    }

    @Override
    public boolean plan(MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
        this.radius = (depth + 1) * 3;
        this.centerX = randomSource.nextInt(metaChunk.minX(), metaChunk.maxX());
        this.centerZ = randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ());
        return true;
    }

    @Override
    public double modifyTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
        int centerY = 64;
        double distance = Math.sqrt((x - centerX) * (x - centerX) +
                (y - centerY) * (y - centerY) +
                (z - centerZ) * (z - centerZ));
        return Math.min(currentNoiseValue, distance - radius);
    }
}
