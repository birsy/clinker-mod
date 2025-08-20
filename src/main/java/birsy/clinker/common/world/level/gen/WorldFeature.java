package birsy.clinker.common.world.level.gen;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public abstract class WorldFeature {
    final int depth;

    protected WorldFeature(int depth) {
        this.depth = depth;
    }

    abstract boolean within(int minX, int minZ, int maxX, int maxZ);

    abstract void plan(MetaChunk metaChunk, RandomSource randomSource);

    double modifyTerrain(int x, int y, int z, double currentNoiseValue) {
        return currentNoiseValue;
    }

    Holder<Biome> modifyBiome(int x, int y, int z, Holder<Biome> currentBiome) {
        return currentBiome;
    }
}
