package birsy.clinker.common.world.level.gen.worldfeature;

import birsy.clinker.common.world.level.gen.LocalFluidLevelMap;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public abstract class WorldFeature {
    public final int depth;

    public WorldFeature(int depth) {
        this.depth = depth;
    }

    public abstract boolean within(int minX, int minZ, int maxX, int maxZ);

    public abstract boolean plan(MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context);

    public double modifyTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
        return currentNoiseValue;
    }

    public Holder<Biome> modifyBiome(int x, int y, int z, Holder<Biome> currentBiome, NoiseComputerContext context) {
        return currentBiome;
    }

    public LocalFluidLevelMap.FluidLevel modifyFluidLevel(int x, int y, int z, LocalFluidLevelMap.FluidLevel currentFluidLevel, NoiseComputerContext context) {
        return currentFluidLevel;
    }
}
