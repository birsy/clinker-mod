package birsy.clinker.common.world.level.gen.worldfeature;

import net.minecraft.util.RandomSource;

public class TestWorldFeature extends WorldFeature {
    int centerX, centerZ;
    int radius = 16;

    protected TestWorldFeature(int depth) {
        super(depth);
    }

    @Override
    boolean within(int minX, int minZ, int maxX, int maxZ) {
        return centerX > minX - this.radius &&
               centerX < maxX + this.radius &&
               centerZ > minZ - this.radius &&
               centerZ < maxZ + this.radius;
    }

    @Override
    void plan(MetaChunk metaChunk, RandomSource randomSource) {
        this.centerX = metaChunk.minX() + (metaChunk.size) / 2;
        this.centerZ = metaChunk.minZ() + (metaChunk.size) / 2;
    }

    @Override
    public double modifyTerrain(int x, int y, int z, double currentNoiseValue) {
        int centerY = 64;
        double distance = Math.sqrt((x - centerX) * (x - centerX) +
                (y - centerY) * (y - centerY) +
                (z - centerZ) * (z - centerZ));
        return Math.min(currentNoiseValue, distance - radius);
    }
}
